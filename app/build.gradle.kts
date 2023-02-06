plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.github.ben-manes.versions") version Versions.GRADLE_VERSIONS
}
android {
    namespace = Build.PACKAGE_NAME
    compileSdk = Build.COMPILE_SDK
    defaultConfig {
        applicationId = Build.PACKAGE_NAME
        minSdk = Build.MIN_SDK
        targetSdk = Build.TARGET_SDK
        versionCode = Build.VERSION_CODE
        versionName = Build.VERSION_NAME
        multiDexEnabled = true
        testInstrumentationRunner = Build.TEST_INSTRUMENTATION_RUNNER
        vectorDrawables.useSupportLibrary = true
        val apiKey = "\"${com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty("key.properties")}\""
        buildConfigField("String", "API_KEY", apiKey)
    }
    buildFeatures.dataBinding = true
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = "1.4.0"
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    packagingOptions {
        resources.excludes.add("/META-INF/AL2.0" + "/META-INF/LGPL2.1")
    }
}

dependencies {
    // Compose
    val composeBom = platform("androidx.compose:compose-bom:${Versions.composeBom}")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.activity:activity-compose:${Versions.activity}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.viewModel}")

    // Core KTX
    implementation("androidx.core:core-ktx:${Versions.ktx}")

    // Fragment KTX
    implementation("androidx.fragment:fragment-ktx:${Versions.fragment}")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:${Versions.work}")

    // AppCompat
    implementation("androidx.appcompat:appcompat:${Versions.appCompat}")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:${Versions.recyclerView}")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.navigation}")
    implementation("androidx.navigation:navigation-ui-ktx:${Versions.navigation}")
    implementation("androidx.navigation:navigation-testing:${Versions.navigation}")

    // Transition
    implementation("androidx.transition:transition-ktx:${Versions.transition}")

    // Material UI
    implementation("com.google.android.material:material:${Versions.material}")

    // Retrofit, Moshi, HttpLoggingInterceptor
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.retrofit}")
    implementation("com.squareup.okhttp3:logging-interceptor:${Versions.httpLoggingInterceptor}")

    // Circular progress bar
    implementation("com.mikhaellopez:circularprogressbar:${Versions.circularProgressBar}")

    // Pie chart
    implementation("com.github.PhilJay:MPAndroidChart:${Versions.pieChart}")

    // Timber for Logging
    implementation("com.jakewharton.timber:timber:${Versions.timber}")

    // Hilt
    implementation("com.google.dagger:hilt-android:${Versions.HILT}")
    kapt("com.google.dagger:hilt-compiler:${Versions.HILT}")
    kapt("androidx.hilt:hilt-compiler:${Versions.hiltKaptCompiler}")

    // Room
    implementation("androidx.room:room-runtime:${Versions.room}")
    kapt("androidx.room:room-compiler:${Versions.room}")
    implementation("androidx.room:room-ktx:${Versions.room}")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:${Versions.splashScreen}")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:${Versions.dataStore}")

    // Testing
    // Local Unit Tests
    implementation("androidx.test:core:${Versions.testCore}")
    testImplementation("junit:junit:${Versions.junitCore}")
    testImplementation("org.hamcrest:hamcrest-all:${Versions.hamcrest}")
    testImplementation("androidx.arch.core:core-testing:${Versions.archCoreTesting}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutineTest}")
    testImplementation("com.google.truth:truth:${Versions.truth}")
    testImplementation("org.mockito:mockito-core:${Versions.mockitoCore}")

    // Instrumented Unit Tests
    androidTestImplementation("junit:junit:${Versions.junitCore}")
    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:${Versions.mockitoDexmaker}")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutineTest}")
    androidTestImplementation("androidx.arch.core:core-testing:${Versions.archCoreTesting}")
    androidTestImplementation("com.google.truth:truth:${Versions.truth}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.junitAndroidX}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.espresso}")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:${Versions.espresso}")
    androidTestImplementation("org.mockito:mockito-core:${Versions.mockitoCore}")
    androidTestImplementation("com.google.dagger:hilt-android-testing:${Versions.HILT}")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${Versions.HILT}")
    debugImplementation("androidx.fragment:fragment-testing:${Versions.fragment}")
}
repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven { url = uri("https://www.jitpack.io") }
}
kapt {
    correctErrorTypes = true
}
