/*
This file was generated by the Gradle 'init' task.
* For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.12.1/userguide/building_java_projects.html in the Gradle documentation.
*/
plugins {
    application
    id("com.github.ben-manes.versions") version "0.52.0"
    id("com.dorongold.task-tree") version "4.0.0"
    kotlin("jvm") version "2.1.20-RC"
}

repositories {
    mavenCentral()
}

dependencies {
    // None - all JDK stuff
}

kotlin {
    jvmToolchain(11)
}

application {
    // Define the main class for the application.
    mainClass = "com.nurflugel.gravitydoodle.DoodleFrame"
}
