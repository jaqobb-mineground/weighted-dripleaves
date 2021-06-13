plugins {
    java
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "dev.jaqobb"
version = "1.0.0"
description = "Turn dripleaves into weighted ones"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
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

//tasks {
//    shadowJar {
//        relocate("org.bstats.bukkit", "dev.jaqobb.weighted_dripleaves.metrics")
//    }
//}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        content {
            includeGroup("org.spigotmc")
        }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        content {
            includeGroup("net.md-5")
        }
    }
//    maven("https://repo.codemc.org/repository/maven-public/") {
//        content {
//            includeGroup("org.bstats")
//        }
//    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
//    implementation("org.bstats:bstats-bukkit:1.7")
}
