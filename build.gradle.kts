import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.20"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("com.gradleup.shadow") version "8.3.6"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    kotlin("plugin.serialization") version "2.1.20"
}

group = "de.joker"
version = "1.0-SNAPSHOT"

val mcVersion = "1.21.5"
val kUtilsVersion = "$mcVersion-beta-0.0.2" // Use the latest snapshot version of KUtils
val commandAPIVersion = "10.0.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://nexus.fruxz.dev/repository/public/") {
        content {
            includeGroup("dev.fruxz")
        }
    }
}

paperweight {
    reobfArtifactConfiguration = io.papermc.paperweight.userdev
        .ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

val shadowDependencies = listOf(
    "dev.invalidjoker.KUtils:core:$kUtilsVersion",
    "dev.invalidjoker.KUtils:paper:$kUtilsVersion",
    "dev.invalidjoker.KUtils:paper-inventory:$kUtilsVersion",
    //"com.github.InvalidJoker.KUtils:paper-commands:$kUtilsVersion",
    "dev.jorel:commandapi-bukkit-shade-mojang-mapped:$commandAPIVersion",
    "dev.jorel:commandapi-bukkit-kotlin:$commandAPIVersion",
    "com.github.ben-manes.caffeine:caffeine:3.2.0",
    "org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1"
)

dependencies {
    paperweight.paperDevBundle("$mcVersion-R0.1-SNAPSHOT")

    shadowDependencies.forEach { dependency ->
        implementation(dependency)
        shadow(dependency)
    }
}

tasks {
    build {
        dependsOn("shadowJar")
        dependsOn(reobfJar)
    }

    withType<ShadowJar> {
        mergeServiceFiles()
        configurations = listOf(project.configurations.shadow.get())
        archiveFileName.set("${project.name}.jar")
    }

    runServer {
        minecraftVersion(mcVersion)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlin.RequiresOptIn",
            )
        )
    }
}

paper {
    main = "de.joker.disasters.DisastersPlugin"
    apiVersion = "1.21"
    authors = listOf("InvalidJoker")
}