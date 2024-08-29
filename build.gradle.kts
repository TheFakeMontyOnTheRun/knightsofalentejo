buildscript {
    val kotlin_version by extra("2.0.20")
    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
