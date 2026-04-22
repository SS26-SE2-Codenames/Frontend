plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.google.devtools.ksp") version "2.3.6"
    id("com.google.dagger.hilt.android")
    id("jacoco")
}

kotlin {
    jvmToolchain(17)
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test> {
    finalizedBy("jacocoTestReport")
}

tasks.register<JacocoReport>("jacocoTestReport") {

    dependsOn("testDebugUnitTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports after running tests."

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // Quellcode-Verzeichnisse – alle zu testenden Pakete
    sourceDirectories.setFrom(
        files(
            "${project.projectDir}/src/main/java",
            "${project.projectDir}/src/main/kotlin",
        ),
    )

    // Klassenverzeichnis – compiled Bytecode (NICHT Hilt-generierte Dateien)
    classDirectories.setFrom(
        files(
            fileTree("$buildDir/intermediates/classes/debug") {
                exclude(
                    "**/R.class",
                    "**/R\$*.class",
                    "**/BuildConfig.*",
                    "**/Manifest*.*",
                    // UI-Komponenten sind schwer zu testen, optional ausschließen:
                    "**/ui/**",
                    // Hilt-generierte Dateien ausschließen:
                    "**/*_Hilt*",
                    "**/*_Factory*",
                    "**/*_MembersInjector*",
                    "**/*_Provide*",
                    "**/*_BindsInstance*",
                )
            },
        ),
    )

    // Execution-Daten – wo Jacoco die Laufdaten speichert
    executionData.setFrom(
        files(
            "$buildDir/jacoco/testDebugUnitTest.exec",
        ),
    )
}

android {
    namespace = "com.codenames.frontend"
    compileSdk {
        version =
            release(36) {
                minorApiLevel = 1
            }
    }

    defaultConfig {
        applicationId = "com.codenames.codenames_frontend"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Android Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Network (HTTP)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Network: Krossbow
    implementation(libs.krossbow.websocket.builtin)
    implementation(libs.krossbow.stomp.kxserialization.json)
    implementation(libs.krossbow.stomp.core)
    implementation(libs.krossbow.websocket.okhttp)

    // DI: Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Coroutines for concurrent programming
    implementation(libs.kotlinx.coroutines.android)

    // Tests
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(kotlin("test"))

    // Hilt for tests
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)
}
