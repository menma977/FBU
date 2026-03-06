import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
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
        jvmArgs += listOf(
            "-DisDevelopment=true",
            "--enable-native-access=ALL-UNNAMED",
        )

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.owl.minerva.fbu"
            packageVersion = "1.0.0"
        }
    }
}
