import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    val kotlin_version = "1.9.10"
    kotlin("jvm") version kotlin_version
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version kotlin_version
}

group = "github.astridalia"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.mineinabyss.com/releases")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    implementation("org.litote.kmongo:kmongo:4.10.0")
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.mineinabyss:idofront-serializers:0.18.24")
    implementation("org.json:json:20230618")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("redis.clients:jedis:3.8.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--release", "17"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.javaParameters = true
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<ShadowJar> {
    relocate("co.aikar.commands", "github.astridalia.acf")
    relocate("co.aikar.locales", "github.astridalia.locales")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
