import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
  java
  id("io.freefair.lombok") version "8.6"
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

val minecraftVersion = "1.21"

version = "$minecraftVersion-1.0.0"

paperweight.reobfArtifactConfiguration.set(ReobfArtifactConfiguration.MOJANG_PRODUCTION)

repositories {
  mavenCentral()
  maven("https://libraries.minecraft.net/")
}

dependencies {
  implementation(project(":dom"))
  implementation(project(":api"))

  paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
  jar {
    archiveBaseName.set("delphi-papermc")
  }
}