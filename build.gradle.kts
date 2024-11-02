plugins {
    java
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "8.3.4"
}

group = "dev.jaqobb"
version = "1.1.1-SNAPSHOT"
description = "Turn dripleaves into weighted ones"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

bukkit {
    name = "WeightedDripleaves"
    main = "dev.jaqobb.weighted_dripleaves.WeightedDripleavesPlugin"
    version = project.version as String
    apiVersion = "1.17"
    description = project.description
    author = "jaqobb"
    website = "https://jaqobb.dev"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")
}
