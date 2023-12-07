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

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
