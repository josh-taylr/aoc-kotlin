plugins {
    kotlin("jvm") version "1.9.22"
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
    application
}

sourceSets {
    main {
        kotlin.srcDir("src/main/kotlin")
        resources.srcDir("src/main/resources")
    }
}

application {
    mainClass.set("MainKt")
}

ktfmt {
    kotlinLangStyle()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:multik-core:0.2.3")
    implementation("org.jetbrains.kotlinx:multik-default:0.2.3")
}

tasks {
    wrapper {
        gradleVersion = "8.7"
    }

    register("day01", JavaExec::class) {
        group = "advent of code"
        description = "Run Day01.kt solution"
        mainClass.set("aoc2023.Day01Kt")
        classpath = sourceSets["main"].runtimeClasspath
    }

    named<JavaExec>("run") {
        project.findProperty("day")?.let { day ->
            environment("AOC_DAY", day)
        }
        project.findProperty("year")?.let { year ->
            environment("AOC_YEAR", year)
        }
    }
}
