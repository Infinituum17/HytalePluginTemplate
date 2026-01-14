import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    java
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(getRequiredProperty("java_version")))
        withSourcesJar()
        withJavadocJar()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(files("${getHytaleDirectory()}/Server/HytaleServer.jar"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    val manifest = mapOf(
        "plugin_group" to getRequiredProperty("plugin_group"),
        "plugin_name" to getRequiredProperty("plugin_name"),
        "plugin_version" to getRequiredProperty("plugin_version"),
        "plugin_description" to getRequiredProperty("plugin_description"),
        "plugin_main" to "${getRequiredProperty("plugin_group")}.${getRequiredProperty("plugin_name")}",
        "plugin_website" to getRequiredProperty("plugin_website"),
    )

    inputs.properties(manifest)

    filesMatching("manifest.json", { expand(manifest) })
}

tasks.jar {
    archiveBaseName.set(getRequiredProperty("plugin_name"))
    archiveVersion.set(getRequiredProperty("plugin_version"))

    // Handle duplicates (resources are already included by default)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

idea.project.settings.runConfigurations {
    create<org.jetbrains.gradle.ext.Application>("HytaleServer") {
        mainClass = "com.hypixel.hytale.Main"
        moduleName = "${project.idea.module.name}.main"
        programParameters =
            "--allow-op --disable-sentry " +
            "--assets=${getHytaleDirectory()}/Assets.zip " +
            "--mods=${layout.buildDirectory.get()}/resources/main" +
            "--auth-mode=authenticated"

        workingDirectory = getRunDirectory()
    }
}

// Checks if "run/" exists, or else it creates it
fun getRunDirectory(): String {
    val directory = file("$projectDir/run")

    if(!directory.exists()) {
        directory.mkdir()
    }

    return directory.absolutePath
}

// Checks if some property is defined in "gradle.properties"
fun getRequiredProperty(propertyName: String): String {
    return providers.gradleProperty(propertyName).orNull
        ?: throw GradleException("Cannot find property '$propertyName' in gradle.properties file")
}

// Gets the Hytale game directory. If "hytale_directory" is specified in "gradle.properties", it uses that path instead
fun getHytaleDirectory(): String {
    val hytaleDir = providers.gradleProperty("hytale_directory")
      .getOrElse("${System.getProperty("user.home")}/AppData/Roaming/Hytale/install")

    return hytaleDir +
            "/${getRequiredProperty("hytale_version_type")}" +
            "/package/game/${getRequiredProperty("hytale_version")}"
}