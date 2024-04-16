plugins {
    kotlin("jvm") version "1.9.22"
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
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
}
