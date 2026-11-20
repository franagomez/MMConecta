import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")

    id("org.jetbrains.kotlin.plugin.compose")
}

// EP4: configuracion de firma para el APK de release. Las contraseñas NO
// estan en este archivo: se leen de keystore/keystore.properties, que no se
// sube al repo (ver .gitignore). Cada integrante crea su propia copia local
// a partir de keystore/keystore.properties.example.
val keystorePropertiesFile = rootProject.file("keystore/keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "cl.grupo11.mmconecta"
    compileSdk = 35
    defaultConfig {
        applicationId = "cl.grupo11.mmconecta"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = rootProject.file("keystore/" + keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

}

// EP4 (Guía 15): las pruebas unitarias usan JUnit5 (en vez del JUnit4 que
// trae Android por defecto), por eso hay que decirle a Gradle que corra
// las pruebas con la plataforma de JUnit5.
tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    // EP4 (Guía 15): stack de pruebas unitarias - JUnit5 + Kotest + MockK
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.13.13")
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
    implementation("androidx.core:core-ktx:1.13.1")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // EP4: consumo de la API REST del backend Spring Boot
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
