// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.sentry.android.gradle) apply false
}

// MEDIA•HUB still compiles the inherited Nuvio playback/plugin surface while the
// migration is in progress. Keep the compatibility dependencies here so the
// MEDIA•HUB app module can stay focused on product-specific configuration.
subprojects {
    if (name == "app") {
        plugins.withId("com.android.application") {
            dependencies {
                add("implementation", "androidx.core:core-splashscreen:1.0.1")
                add("implementation", "androidx.recyclerview:recyclerview:1.4.0")
                add("implementation", "androidx.compose.material:material-icons-extended")
                add("implementation", "androidx.tvprovider:tvprovider:1.0.0")

                add("implementation", "io.coil-kt.coil3:coil-gif:3.3.0")
                add("implementation", "io.coil-kt.coil3:coil-svg:3.3.0")
                add("implementation", "io.coil-kt.coil3:coil-network-cache-control:3.3.0")
                add("implementation", "com.airbnb.android:lottie-compose:6.7.1")

                add("implementation", "io.github.peerless2012:ass-media:0.4.0")
                add("implementation", files("app/libs/nextlib-mediainfo-local.aar"))
                add("implementation", "io.github.abdallahmehiz:mpv-android-lib:0.1.12")
                add("implementation", "dev.chrisbanes.haze:haze-android:1.7.2") {
                    exclude(group = "org.jetbrains.compose.ui")
                    exclude(group = "org.jetbrains.compose.foundation")
                }

                add("fullImplementation", files("app/libs/quickjs-kt-android-1.0.5-nuvio.aar"))
                add("fullImplementation", "org.jsoup:jsoup:1.17.2")
                add("fullImplementation", "com.fasterxml.jackson.core:jackson-databind:2.17.0")
                add("fullImplementation", "com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
                add("fullImplementation", "com.github.Blatzar:NiceHttp:0.4.16")
                add("fullImplementation", "org.conscrypt:conscrypt-android:2.5.2")
                add("fullImplementation", "com.github.recloudstream.cloudstream:library:v4.7.0") {
                    exclude(group = "org.mozilla", module = "rhino")
                    exclude(group = "com.github.AmarullisVFX", module = "newpipeextractor")
                    exclude(group = "com.github.AmaryllisVFX", module = "newpipeextractor")
                    exclude(group = "com.github.AmaryllisVFX.newpipeextractor")
                    exclude(group = "info.debatty", module = "java-string-similarity")
                }
                add("fullImplementation", "org.webjars.npm:crypto-js:4.2.0")

                add("implementation", "com.mikepenz:multiplatform-markdown-renderer-m3:0.33.0")
                add("implementation", "org.nanohttpd:nanohttpd:2.3.1")
                add("implementation", "com.google.zxing:core:3.5.3")
                add("implementation", "androidx.metrics:metrics-performance:1.0.0-rc01")
                add("debugImplementation", "androidx.compose.runtime:runtime-tracing")
            }
        }
    }
}
