plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sentry.android.gradle)
}

import java.io.File
import java.util.Properties

fun parseBooleanProperty(value: String?): Boolean {
    val normalized = value?.trim()?.lowercase() ?: return false
    return normalized == "1" || normalized == "true" || normalized == "yes" || normalized == "on"
}

fun resolveProperty(dev: Properties, local: Properties, key: String, fallback: String = ""): String {
    return dev.getProperty(key)?.trim()?.takeIf { it.isNotBlank() }
        ?: local.getProperty(key)?.trim()?.takeIf { it.isNotBlank() }
        ?: fallback
}

fun buildConfigString(value: String): String {
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}

fun cmakePath(path: String): String {
    if (path.isBlank()) return ""
    val file = File(path)
    val resolved = if (file.isAbsolute) file else rootProject.file(path)
    return resolved.absolutePath.replace("\\", "/")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

val devProperties = Properties().apply {
    val devPropertiesFile = rootProject.file("local.dev.properties")
    if (devPropertiesFile.exists()) {
        load(devPropertiesFile.inputStream())
    }
}

val enableDoviNative = parseBooleanProperty(
    resolveProperty(devProperties, localProperties, "DOVI_NATIVE_ENABLED")
)
val doviExtractorHookReady = parseBooleanProperty(
    resolveProperty(devProperties, localProperties, "DOVI_EXTRACTOR_HOOK_READY")
)
val doviEnableRealLink = parseBooleanProperty(
    resolveProperty(devProperties, localProperties, "DOVI_ENABLE_REAL_LINK")
)
val doviStaticLibPath = resolveProperty(devProperties, localProperties, "DOVI_LIBDOVI_STATIC_LIB")
val doviIncludeDirPath = resolveProperty(devProperties, localProperties, "DOVI_LIBDOVI_INCLUDE_DIR")
val doviPrebuiltRootPath = resolveProperty(devProperties, localProperties, "DOVI_LIBDOVI_PREBUILT_ROOT")
val sponsorNames = resolveProperty(devProperties, localProperties, "SPONSOR_NAMES", "ragmehos.")
val sentryDsn = providers.environmentVariable("SENTRY_DSN").orNull?.trim()?.takeIf { it.isNotBlank() }
    ?: resolveProperty(devProperties, localProperties, "SENTRY_DSN")
val sentryAuthToken = providers.environmentVariable("SENTRY_AUTH_TOKEN").orNull?.trim()?.takeIf { it.isNotBlank() }
    ?: resolveProperty(devProperties, localProperties, "SENTRY_AUTH_TOKEN").takeIf { it.isNotBlank() }
val sentryOrg = providers.environmentVariable("SENTRY_ORG").orNull?.trim()?.takeIf { it.isNotBlank() }
    ?: resolveProperty(devProperties, localProperties, "SENTRY_ORG").takeIf { it.isNotBlank() }
val sentryProject = providers.environmentVariable("SENTRY_PROJECT").orNull?.trim()?.takeIf { it.isNotBlank() }
    ?: resolveProperty(devProperties, localProperties, "SENTRY_PROJECT").takeIf { it.isNotBlank() }
val sentryMappingUploadEnabled = sentryAuthToken != null && sentryOrg != null && sentryProject != null

fun env(name: String): String? = providers.environmentVariable(name).orNull

fun truthy(value: String?): Boolean {
    return value.equals("true", ignoreCase = true) ||
        value.equals("1", ignoreCase = true) ||
        value.equals("yes", ignoreCase = true)
}

val buildingAppBundle = gradle.startParameter.taskNames.any { it.contains("bundle", ignoreCase = true) }
val useDebugReleaseSigning = env("CI_USE_DEBUG_SIGNING").equals("true", ignoreCase = true)
val useLocalFfmpegDecoder = truthy(
    providers.gradleProperty("useLocalFfmpegDecoder").orNull
        ?: env("USE_LOCAL_FFMPEG_DECODER")
        ?: localProperties.getProperty("USE_LOCAL_FFMPEG_DECODER")
)
val releaseStoreFilePath = env("NUVIO_RELEASE_STORE_FILE")
    ?: localProperties.getProperty("NUVIO_RELEASE_STORE_FILE")
val releaseKeyAliasValue = env("NUVIO_RELEASE_KEY_ALIAS")
    ?: localProperties.getProperty("NUVIO_RELEASE_KEY_ALIAS", "nuviotv")
val releaseKeyPasswordValue = env("NUVIO_RELEASE_KEY_PASSWORD")
    ?: localProperties.getProperty("NUVIO_RELEASE_KEY_PASSWORD", "815787")
val releaseStorePasswordValue = env("NUVIO_RELEASE_STORE_PASSWORD")
    ?: localProperties.getProperty("NUVIO_RELEASE_STORE_PASSWORD", "815787")

android {
    namespace = "com.nuvio.tv"
    compileSdk = 36
    ndkVersion = "29.0.14206865"

    defaultConfig {
        applicationId = "com.mediahub.tv"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-alpha"

        buildConfigField("String", "PARENTAL_GUIDE_API_URL", "\"${localProperties.getProperty("PARENTAL_GUIDE_API_URL", "")}\"")
        buildConfigField("String", "INTRODB_API_URL", "\"${localProperties.getProperty("INTRODB_API_URL", "")}\"")
        buildConfigField("String", "TRAILER_API_URL", "\"${localProperties.getProperty("TRAILER_API_URL", "")}\"")
        buildConfigField("String", "IMDB_RATINGS_API_BASE_URL", "\"${localProperties.getProperty("IMDB_RATINGS_API_BASE_URL", "")}\"")
        buildConfigField("String", "IMDB_TAPFRAME_API_BASE_URL", "\"${localProperties.getProperty("IMDB_TAPFRAME_API_BASE_URL", "")}\"")
        buildConfigField("String", "TRAKT_CLIENT_ID", "\"${localProperties.getProperty("TRAKT_CLIENT_ID", "")}\"")
        buildConfigField("String", "TRAKT_CLIENT_SECRET", "\"${localProperties.getProperty("TRAKT_CLIENT_SECRET", "")}\"")
        buildConfigField("String", "TRAKT_API_URL", "\"${localProperties.getProperty("TRAKT_API_URL", "https://api.trakt.tv/")}\"")
        buildConfigField("String", "TRAKT_REDIRECT_URI", "\"${localProperties.getProperty("TRAKT_REDIRECT_URI", "urn:ietf:wg:oauth:2.0:oob")}\"")
        buildConfigField("String", "SIMKL_CLIENT_ID", buildConfigString(resolveProperty(devProperties, localProperties, "SIMKL_CLIENT_ID")))
        buildConfigField("String", "SIMKL_APP_NAME", buildConfigString(resolveProperty(devProperties, localProperties, "SIMKL_APP_NAME", "nuvio")))
        buildConfigField("String", "TMDB_API_KEY", "\"${localProperties.getProperty("TMDB_API_KEY", "")}\"")
        buildConfigField("String", "TV_LOGIN_WEB_BASE_URL", "\"${localProperties.getProperty("TV_LOGIN_WEB_BASE_URL", "https://nuvio.tv/tv-login")}\"")
        buildConfigField("String", "DEVICE_LOGIN_WEB_BASE_URL", "\"${localProperties.getProperty("DEVICE_LOGIN_WEB_BASE_URL", "https://nuvio.tv/link")}\"")
        buildConfigField("boolean", "DOVI_NATIVE_ENABLED", enableDoviNative.toString())
        buildConfigField("boolean", "DOVI_EXTRACTOR_HOOK_READY", doviExtractorHookReady.toString())
        if (enableDoviNative) {
            externalNativeBuild {
                cmake {
                    arguments(
                        "-DDOVI_ENABLE_LIBDOVI=${if (doviEnableRealLink) "ON" else "OFF"}",
                        "-DDOVI_LIBDOVI_STATIC_LIB=${cmakePath(doviStaticLibPath)}",
                        "-DDOVI_LIBDOVI_INCLUDE_DIR=${cmakePath(doviIncludeDirPath)}",
                        "-DDOVI_LIBDOVI_PREBUILT_ROOT=${cmakePath(doviPrebuiltRootPath)}"
                    )
                }
            }
        }
        buildConfigField("String", "SUPPORTERS_API_BASE_URL", buildConfigString(localProperties.getProperty("SUPPORTERS_API_BASE_URL", "https://nuvio.tv/")))
        buildConfigField("String", "SUPPORT_URL", buildConfigString(localProperties.getProperty("SUPPORT_URL", "https://nuvio.tv/support")))
        buildConfigField("String", "AVATAR_PUBLIC_BASE_URL", "\"${localProperties.getProperty("AVATAR_PUBLIC_BASE_URL", "")}\"")
        buildConfigField("String", "UNIQUE_CONTRIBUTIONS_BASE_URL", "\"${localProperties.getProperty("UNIQUE_CONTRIBUTIONS_BASE_URL", "")}\"")
        buildConfigField("String", "PLAYBACK_REPORTS_BASE_URL", buildConfigString(localProperties.getProperty("PLAYBACK_REPORTS_BASE_URL", "")))
        buildConfigField("String", "PREMIUMIZE_CLIENT_ID", "\"${localProperties.getProperty("PREMIUMIZE_CLIENT_ID", "")}\"")
        buildConfigField("String", "SPONSOR_NAMES", buildConfigString(sponsorNames))
        buildConfigField("String", "SENTRY_DSN", buildConfigString(sentryDsn))

        // In-app updater (GitHub Releases)
        buildConfigField("String", "GITHUB_OWNER", "\"NuvioMedia\"")
        buildConfigField("String", "GITHUB_REPO", "\"NuvioTV\"")
    }

    flavorDimensions += "distribution"
    productFlavors {
        create("full") {
            dimension = "distribution"
            buildConfigField("boolean", "FEATURE_PLUGINS_ENABLED", "true")
            buildConfigField("boolean", "FEATURE_IN_APP_UPDATES_ENABLED", "false")
            buildConfigField("boolean", "FEATURE_IN_APP_TRAILERS_ENABLED", "true")
            buildConfigField("boolean", "FEATURE_EXTERNAL_TRAILERS_ENABLED", "true")
            buildConfigField("boolean", "FEATURE_EXTERNAL_PLAYBACK_KEEP_ALIVE_ENABLED", "true")
            buildConfigField("boolean", "FEATURE_CUSTOM_SERVER_CONNECTIONS_ENABLED", "true")
        }
        create("playstore") {
            dimension = "distribution"
            applicationId = "com.nuvio.app"
            buildConfigField("boolean", "FEATURE_PLUGINS_ENABLED", "false")
            buildConfigField("boolean", "FEATURE_IN_APP_UPDATES_ENABLED", "false")
            buildConfigField("boolean", "FEATURE_IN_APP_TRAILERS_ENABLED", "false")
            buildConfigField("boolean", "FEATURE_EXTERNAL_TRAILERS_ENABLED", "true")
            buildConfigField("boolean", "FEATURE_EXTERNAL_PLAYBACK_KEEP_ALIVE_ENABLED", "false")
            buildConfigField("boolean", "FEATURE_CUSTOM_SERVER_CONNECTIONS_ENABLED", "false")
        }
    }

    if (enableDoviNative) {
        externalNativeBuild {
            cmake {
                path = file("src/main/cpp/CMakeLists.txt")
            }
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = releaseKeyAliasValue
            keyPassword = releaseKeyPasswordValue
            storeFile = releaseStoreFilePath?.let(::file) ?: file("../nuviotv.jks")
            storePassword = releaseStorePasswordValue
        }
    }

    buildTypes {
        debug {
            signingConfig = if (useDebugReleaseSigning) {
                signingConfigs.getByName("debug")
            } else {
                signingConfigs.getByName("release")
            }
            isDebuggable = false
            isMinifyEnabled = false

            buildConfigField("boolean", "IS_DEBUG_BUILD", "true")
            buildConfigField("String", "SENTRY_ENVIRONMENT", buildConfigString("debug"))

            // Dev environment (from local.dev.properties)
            buildConfigField("String", "SUPABASE_URL", buildConfigString(resolveProperty(devProperties, localProperties, "NUVIO_SUPABASE_URL")))
            buildConfigField("String", "SUPABASE_ANON_KEY", buildConfigString(resolveProperty(devProperties, localProperties, "NUVIO_SUPABASE_ANON_KEY")))
            buildConfigField("String", "SUPABASE_FALLBACK_URL", buildConfigString(resolveProperty(devProperties, localProperties, "NUVIO_SUPABASE_FALLBACK_URL")))
            buildConfigField("String", "TV_LOGIN_WEB_BASE_URL", "\"${devProperties.getProperty("TV_LOGIN_WEB_BASE_URL", "https://nuvio.tv/tv-login")}\"")
            buildConfigField("String", "DEVICE_LOGIN_WEB_BASE_URL", "\"${devProperties.getProperty("DEVICE_LOGIN_WEB_BASE_URL", "https://nuvio.tv/link")}\"")
            buildConfigField("String", "PARENTAL_GUIDE_API_URL", "\"${devProperties.getProperty("PARENTAL_GUIDE_API_URL", "")}\"")
            buildConfigField("String", "INTRODB_API_URL", "\"${devProperties.getProperty("INTRODB_API_URL", "")}\"")
            buildConfigField("String", "TRAILER_API_URL", "\"${devProperties.getProperty("TRAILER_API_URL", "")}\"")
            buildConfigField("String", "IMDB_RATINGS_API_BASE_URL", "\"${devProperties.getProperty("IMDB_RATINGS_API_BASE_URL", "")}\"")
            buildConfigField("String", "IMDB_TAPFRAME_API_BASE_URL", "\"${devProperties.getProperty("IMDB_TAPFRAME_API_BASE_URL", "")}\"")
            buildConfigField("String", "SUPPORTERS_API_BASE_URL", buildConfigString(resolveProperty(devProperties, localProperties, "SUPPORTERS_API_BASE_URL", "https://nuvio.tv/")))
            buildConfigField("String", "SUPPORT_URL", buildConfigString(resolveProperty(devProperties, localProperties, "SUPPORT_URL", "https://nuvio.tv/support")))
            buildConfigField("String", "AVATAR_PUBLIC_BASE_URL", "\"${devProperties.getProperty("AVATAR_PUBLIC_BASE_URL", localProperties.getProperty("AVATAR_PUBLIC_BASE_URL", ""))}\"")
            buildConfigField("String", "UNIQUE_CONTRIBUTIONS_BASE_URL", "\"${devProperties.getProperty("UNIQUE_CONTRIBUTIONS_BASE_URL", localProperties.getProperty("UNIQUE_CONTRIBUTIONS_BASE_URL", ""))}\"")
            buildConfigField("String", "PLAYBACK_REPORTS_BASE_URL", buildConfigString(resolveProperty(devProperties, localProperties, "PLAYBACK_REPORTS_BASE_URL")))
            buildConfigField("String", "PREMIUMIZE_CLIENT_ID", "\"${devProperties.getProperty("PREMIUMIZE_CLIENT_ID", localProperties.getProperty("PREMIUMIZE_CLIENT_ID", ""))}\"")
            buildConfigField("String", "SPONSOR_NAMES", buildConfigString(sponsorNames))
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = if (useDebugReleaseSigning) {
                signingConfigs.getByName("debug")
            } else {
                signingConfigs.getByName("release")
            }

            buildConfigField("boolean", "IS_DEBUG_BUILD", "false")
            buildConfigField("String", "SENTRY_ENVIRONMENT", buildConfigString("production"))

            // Production environment (from local.properties)
            buildConfigField("String", "SUPABASE_URL", buildConfigString(localProperties.getProperty("NUVIO_SUPABASE_URL", "")))
            buildConfigField("String", "SUPABASE_ANON_KEY", buildConfigString(localProperties.getProperty("NUVIO_SUPABASE_ANON_KEY", "")))
            buildConfigField("String", "SUPABASE_FALLBACK_URL", buildConfigString(localProperties.getProperty("NUVIO_SUPABASE_FALLBACK_URL", "")))
            buildConfigField("String", "TV_LOGIN_WEB_BASE_URL", "\"${localProperties.getProperty("TV_LOGIN_WEB_BASE_URL", "https://nuvio.tv/tv-login")}\"")
            buildConfigField("String", "DEVICE_LOGIN_WEB_BASE_URL", "\"${localProperties.getProperty("DEVICE_LOGIN_WEB_BASE_URL", "https://nuvio.tv/link")}\"")
            buildConfigField("String", "PARENTAL_GUIDE_API_URL", "\"${localProperties.getProperty("PARENTAL_GUIDE_API_URL", "")}\"")
            buildConfigField("String", "INTRODB_API_URL", "\"${localProperties.getProperty("INTRODB_API_URL", "")}\"")
            buildConfigField("String", "TRAILER_API_URL", "\"${localProperties.getProperty("TRAILER_API_URL", "")}\"")
            buildConfigField("String", "IMDB_RATINGS_API_BASE_URL", "\"${localProperties.getProperty("IMDB_RATINGS_API_BASE_URL", "")}\"")
            buildConfigField("String", "IMDB_TAPFRAME_API_BASE_URL", "\"${localProperties.getProperty("IMDB_TAPFRAME_API_BASE_URL", "")}\"")
            buildConfigField("String", "SUPPORTERS_API_BASE_URL", buildConfigString(localProperties.getProperty("SUPPORTERS_API_BASE_URL", "https://nuvio.tv/")))
            buildConfigField("String", "SUPPORT_URL", buildConfigString(localProperties.getProperty("SUPPORT_URL", "https://nuvio.tv/support")))
            buildConfigField("String", "AVATAR_PUBLIC_BASE_URL", "\"${localProperties.getProperty("AVATAR_PUBLIC_BASE_URL", "")}\"")
            buildConfigField("String", "UNIQUE_CONTRIBUTIONS_BASE_URL", "\"${localProperties.getProperty("UNIQUE_CONTRIBUTIONS_BASE_URL", "")}\"")
            buildConfigField("String", "PLAYBACK_REPORTS_BASE_URL", buildConfigString(localProperties.getProperty("PLAYBACK_REPORTS_BASE_URL", "")))
            buildConfigField("String", "PREMIUMIZE_CLIENT_ID", "\"${localProperties.getProperty("PREMIUMIZE_CLIENT_ID", "")}\"")
            buildConfigField("String", "SPONSOR_NAMES", buildConfigString(sponsorNames))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":libmpv-android"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.datasource.okhttp)
    implementation(libs.androidx.media3.datasource.rtmp)
    implementation(libs.androidx.media3.decoder.ffmpeg)
    implementation(libs.androidx.media3.decoder.av1)
    implementation(libs.androidx.media3.decoder.vp9)
    implementation(libs.androidx.media3.container)
    implementation(libs.androidx.media3.exoplayer.smoothstreaming)
    implementation(libs.androidx.media3.exoplayer.rtsp)
    implementation(libs.androidx.media3.exoplayer.ima)
    implementation(libs.androidx.media3.exoplayer.workmanager)
    implementation(libs.androidx.media3.exoplayer.midi)
    implementation(libs.androidx.media3.effect)
    implementation(libs.androidx.media3.muxer)
    implementation(libs.androidx.media3.transformer)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.media3.datasource.cronet)
    implementation(libs.androidx.media3.datasource.rtmp)
    implementation(libs.androidx.media3.cast)
    implementation(libs.androidx.media3.exoplayer.workmanager)
    implementation(libs.androidx.media3.exoplayer.midi)
    implementation(libs.androidx.media3.effect)
    implementation(libs.androidx.media3.muxer)
    implementation(libs.androidx.media3.transformer)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.media3.datasource.cronet)
    implementation(libs.androidx.media3.cast)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.security.crypto)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose)
    implementation(libs.sentry.okhttp)
    implementation(libs.sentry.timber)
    implementation(libs.timber)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    baselineProfile(project(":baselineprofile"))
}

sentry {
    autoUploadProguardMapping.set(sentryMappingUploadEnabled)
    uploadNativeSymbols.set(false)
    includeNativeSources.set(false)
    autoInstallation {
        enabled.set(true)
    }
}
