buildscript {
    repositories { google(); mavenCentral() }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.HILT}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
