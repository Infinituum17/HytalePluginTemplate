plugins {
    java
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

afterEvaluate {
    if(System.getProperty("idea.active") == "true") {
        config.generateConfiguration()
    }
}