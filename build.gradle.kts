buildscript {
    val kotlinVersion by extra("2.1.21")
    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
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
