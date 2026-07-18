import org.gradle.declarative.dsl.schema.FqName.Empty.packageName
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
    id("com.github.gmazzo.buildconfig") version "6.0.10"
    id("com.google.gms.google-services")
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    androidLibrary {
       namespace = "com.example.attendanceapp.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.okhttp)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.9.0"))
            implementation(libs.firebase.messaging.ktx)
        }
        iosMain.dependencies {

            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            implementation(libs.easyqrscan)
            implementation("io.github.kalinjul.easyqrscan:scanner:0.7.1")
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.websockets)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
            implementation("dev.gitlive:firebase-messaging:2.5.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

// 1. Leemos el archivo local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

// 2. Sacamos la URL (y le ponemos una de respaldo por si acaso)
val apiUrl = localProperties.getProperty("API_BASE_URL") ?: "http://localhost:8080"

// 3. Generamos la clase BuildConfig para todo tu proyecto
buildConfig {
    packageName("com.example.attendanceapp.core") // Tu paquete principal
    buildConfigField("String", "API_URL", "\"${apiUrl}\"") // Ojo con las comillas escapadas
}