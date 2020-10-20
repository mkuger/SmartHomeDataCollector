import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "me.mikuger"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "13"
}

application {
    mainClassName = "MainKt"
}

dependencies {
    implementation("com.github.kittinunf.fuel:fuel:2.3.0")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("org.influxdb:influxdb-java:2.20")
    implementation("com.sksamuel.hoplite:hoplite-core:1.3.7")
    implementation("com.sksamuel.hoplite:hoplite-yaml:1.3.7")
}