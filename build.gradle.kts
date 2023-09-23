import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.0.0"
    kotlin("plugin.serialization") version "1.9.0"
    application
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
    testImplementation(kotlin("test"))
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    implementation("org.litote.kmongo:kmongo:4.10.0")
    implementation("io.insert-koin:koin-core:3.4.3")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.mineinabyss:idofront-serializers:0.18.24")
    implementation("org.json:json:20230618")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

}

java {
    sourceCompatibility = JavaVersion.VERSION_17 // or higher version
    targetCompatibility = JavaVersion.VERSION_17 // or higher version
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--release", "17")) // Replace "17" with your desired Java version
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions.javaParameters = true
    kotlinOptions.jvmTarget = "17" // Replace "17" with your desired Java version
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

application {
    mainClass.set("MainKt")
}