plugins {
    kotlin("jvm") version "1.9.21"
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
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
