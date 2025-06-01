plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktlint)
}

group = "me.bossm0n5t3r"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url =
            uri(
                libs.versions.koog.maven.url
                    .get(),
            )
    }
}

dependencies {
    implementation(libs.koog.agents)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlin.test.junit5)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(
        libs.versions.jdk.version
            .get()
            .toInt(),
    )
}

ktlint {
    version.set(
        libs.versions.ktlint.version
            .get(),
    )
}
