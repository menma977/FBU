import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation("com.microsoft.playwright:playwright:1.41.0")
            implementation("org.jetbrains.exposed:exposed-core:0.47.0")
            implementation("org.jetbrains.exposed:exposed-dao:0.47.0")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.47.0")
            implementation("org.xerial:sqlite-jdbc:3.45.1.0")
            implementation("org.slf4j:slf4j-simple:2.0.9")
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.owl.minerva.fbu.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.owl.minerva.fbu"
            packageVersion = "1.0.0"
        }
    }
}

val applicationProperties = Properties()
val applicationPropertiesFile = project.rootProject.file("local.properties")
if (applicationPropertiesFile.exists()) {
    applicationPropertiesFile.inputStream().use { inputStream ->
        applicationProperties.load(inputStream)
    }
}

buildkonfig {
    packageName = "com.owl.minerva.fbu.cores.configs"
    objectName = "AppConfig"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "APP_NAME",
            applicationProperties.getProperty("app.name", "FBU")
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "APP_ENVIRONMENT",
            applicationProperties.getProperty("app.environment", "production")
        )
    }
}
