package com.example.attendanceapp.features.reports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.features.reports.data.AttendanceHistoryRecord
import com.example.attendanceapp.features.reports.data.ParentChildReport
import com.example.attendanceapp.features.reports.data.ReportsRepositoryImpl
import com.example.attendanceapp.features.reports.data.StudentStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

class ReportsViewModel(
    private val repository: ReportsRepositoryImpl = ReportsRepositoryImpl()
) : ViewModel() {

    private val _children = MutableStateFlow<List<ParentChildReport>>(emptyList())
    val children: StateFlow<List<ParentChildReport>> = _children.asStateFlow()

    private val _selectedChildIndex = MutableStateFlow(0)
    val selectedChildIndex: StateFlow<Int> = _selectedChildIndex.asStateFlow()

    private val _statistics = MutableStateFlow<StudentStatistics?>(null)
    val statistics: StateFlow<StudentStatistics?> = _statistics.asStateFlow()

    private val _monthlyData = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val monthlyData: StateFlow<List<Pair<String, Int>>> = _monthlyData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var allAttendanceRecords = emptyList<AttendanceHistoryRecord>()

    init {
        fetchChildren()
    }

    fun selectChild(index: Int) {
        if (index != _selectedChildIndex.value) {
            _selectedChildIndex.value = index
            loadAttendanceForSelectedChild()
        }
    }

    private fun fetchChildren() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getChildren()
            result.fold(
                onSuccess = { childrenList ->
                    _children.value = childrenList
                    if (childrenList.isNotEmpty()) {
                        loadAttendanceForSelectedChild()
                    }
                    _error.value = null
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Error al cargar hijos"
                }
            )
            _isLoading.value = false
        }
    }

    private fun loadAttendanceForSelectedChild() {
        val childrenList = _children.value
        val index = _selectedChildIndex.value
        if (index >= childrenList.size) return

        val studentId = childrenList[index].student.id
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAttendanceHistory(studentId)
            result.fold(
                onSuccess = { records ->
                    allAttendanceRecords = records
                    computeStatistics(childrenList[index], records)
                    computeMonthlyData(records)
                    _error.value = null
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Error al cargar asistencia"
                    _statistics.value = null
                    _monthlyData.value = emptyList()
                }
            )
            _isLoading.value = false
        }
    }

    private fun computeStatistics(
        parentChild: ParentChildReport,
        records: List<AttendanceHistoryRecord>
    ) {
        val studentName = "${parentChild.student.nombres} ${parentChild.student.apellidos}"

        val daysPresent = records
            .filter { it.tipo.equals("INGRESO", ignoreCase = true) }
            .map { it.fecha?.take(10) }
            .filterNotNull()
            .distinct()
            .size

        val tardies = records.count {
            it.tipo.equals("TARDANZA", ignoreCase = true) ||
            it.tipo.equals("TARDÍA", ignoreCase = true)
        }

        val uniqueDays = records
            .map { it.fecha?.take(10) }
            .filterNotNull()
            .distinct()
            .size

        val daysAbsent = if (uniqueDays > 0) {
            val schoolDaysEstimate = uniqueDays + (uniqueDays * 0.05).toInt()
            (schoolDaysEstimate - daysPresent).coerceAtLeast(0)
        } else 0

        val totalDays = daysPresent + daysAbsent
        val attendanceRate = if (totalDays > 0) {
            (daysPresent.toDouble() / totalDays * 100.0)
        } else 0.0

        _statistics.value = StudentStatistics(
            studentId = parentChild.student.id,
            studentName = studentName,
            totalDays = totalDays,
            daysPresent = daysPresent,
            daysAbsent = daysAbsent,
            tardies = tardies,
            attendanceRate = round(attendanceRate * 10.0) / 10.0
        )
    }

    private fun computeMonthlyData(records: List<AttendanceHistoryRecord>) {
        val monthlyMap = mutableMapOf<String, Int>()

        records
            .filter { it.tipo.equals("INGRESO", ignoreCase = true) }
            .forEach { record ->
                val fecha = record.fecha ?: return@forEach
                if (fecha.length >= 7) {
                    val yearMonth = fecha.substring(0, 7)
                    monthlyMap[yearMonth] = (monthlyMap[yearMonth] ?: 0) + 1
                }
            }

        _monthlyData.value = monthlyMap.entries.sortedBy { it.key }.map { (key, count) ->
            val monthName = getMonthAbbreviation(key)
            Pair(monthName, count)
        }
    }

    private fun getMonthAbbreviation(yearMonth: String): String {
        val month = try {
            yearMonth.split("-").getOrNull(1)?.toIntOrNull() ?: 0
        } catch (_: Exception) { 0 }

        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> "??"
        }
    }
}
