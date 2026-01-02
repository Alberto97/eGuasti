import com.codingfeline.buildkonfig.compiler.FieldSpec
import io.github.frankois944.spmForKmp.swiftPackageConfig
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.spmForKmp)
    alias(libs.plugins.buildKonfig)
}

val secretsProperties = Properties()
val secretsPropertiesFile: File = rootProject.file("secrets.properties")
if (secretsPropertiesFile.exists()) {
    secretsProperties.load(secretsPropertiesFile.inputStream())
}

kotlin {
    androidLibrary {
        namespace = "net.albertopedron.eguasti.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources.enable = true

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        @Suppress("OPT_IN_USAGE")
        iosTarget.swiftPackageConfig(cinteropName ="spmMaplibre") {
            dependency {
                remotePackageVersion(
                    url = URI("https://github.com/maplibre/maplibre-gl-native-distribution.git"),
                    products = { add("MapLibre") },
                    version = "6.20.0",
                )
            }
        }
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.protobuf)
            implementation(libs.ktorfit.lib)
            implementation(libs.sandwich.ktorfit)

            implementation(libs.kotlinx.io.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.protobuf)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.maplibre.compose)
            implementation(libs.maplibre.spatialk.geojson)

            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas/")
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

dependencies {
    "androidRuntimeClasspath"(compose.uiTooling)
}

buildkonfig {
    packageName = "net.albertopedron.eguasti"

    defaultConfigs {
        val mapTilerApiKey = secretsProperties.getProperty("MAPTILER_API_KEY") ?: ""
        val protoMapsApiKey = secretsProperties.getProperty("PROTOMAPS_API_KEY") ?: ""

        buildConfigField(FieldSpec.Type.STRING, "MAPTILER_API_KEY", mapTilerApiKey)
        buildConfigField(FieldSpec.Type.STRING, "PROTOMAPS_API_KEY", protoMapsApiKey)
    }
}
