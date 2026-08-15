import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
    id("com.gradleup.shadow") version("9.6.1")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.Bytephoria:byte-companions-api:2.3.0")
    compileOnly("org.spongepowered:configurate-yaml:4.2.0")
}

paper {
    name = getProjectName(rootProject.name)
    main = "${rootProject.group}.${rootProject.name.replace("-", "")}.PaperPlugin"
    description = rootProject.description
    version = rootProject.version.toString()
    apiVersion = "1.20"

    authors = listOf("Bytephoria", "iAmForyy_")
    website = "https://bytephoria.team"
    generateLibrariesJson = true
    foliaSupported = true

    serverDependencies {
        register("ByteCompanions") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

    }

}

tasks {

    jar {
        enabled = false
    }

    generatePaperPluginDescription {
        useGoogleMavenCentralProxy()
    }

    shadowJar {
        archiveBaseName.set(getProjectName(rootProject.name))
        archiveVersion.set(rootProject.version.toString())
        archiveClassifier.set("")

    }

}

/**
 * Converts a hyphen-separated project name into PascalCase.
 */
fun getProjectName(baseName: String): String {
    return baseName.split("-")
        .joinToString("") {
                part -> part.replaceFirstChar {
            it.uppercase()
        }
        }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))