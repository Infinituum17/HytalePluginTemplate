import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    java
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
}

val config = HytalePluginConfig(project)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(config.javaVersion))
        withSourcesJar()
        withJavadocJar()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(config.getHytaleServerJar())
}

tasks.processResources {
    inputs.properties(config.manifest)

    filesMatching("manifest.json", { expand(config.manifest) })
}

tasks.jar {
    archiveBaseName.set(config.pluginName)
    archiveVersion.set(config.pluginVersion)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

idea.project.settings.runConfigurations {
    create<org.jetbrains.gradle.ext.Application>("HytaleServer") {
        mainClass = config.getHytaleMainClass()
        moduleName = "${project.idea.module.name}.main"
        programParameters =
            "--allow-op --disable-sentry " +
            "--assets=${config.getHytaleAssets()} " +
            "--mods=${layout.buildDirectory.get()}/resources/main" +
            "--auth-mode=authenticated"

        workingDirectory = getRunDirectory()
    }
}