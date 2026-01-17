plugins {
    java
}

val fernflower: Configuration by configurations.creating
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

    maven {
        url = uri("https://www.jetbrains.com/intellij-repository/releases")
    }
}

dependencies {
    implementation(config.getHytaleServerJar())

    fernflower("com.jetbrains.intellij.java:java-decompiler-engine:253.28294.334")
}

tasks.processResources {
    inputs.properties(config.manifest)

    filesMatching("manifest.json") { expand(provider { config.manifest }.get()) }
}

tasks.jar {
    archiveBaseName.set(config.pluginName)
    archiveVersion.set(config.pluginVersion)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.register("generateVSCodeConfiguration") {
    config.generateConfiguration(HytalePluginConfig.Platform.VSCODE)
}

tasks.register<JavaExec>("decompileJar") {
    val inputJar = config.getHytaleServerJar().singleFile
    val outputDir = layout.buildDirectory.dir("generated/sources/fernflower")

    group = "decompilation"
    description = "Decompile with FernFlower"

    classpath = configurations["fernflower"]
    mainClass.set(
        "org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler"
    )

    args(
        "-log=ERROR",
        "-nls=true",
        "-dgs=true",
        "-rsy=true",
        inputJar,
        outputDir.get().asFile.absolutePath
    )

    inputs.file(inputJar)
    outputs.dir(outputDir)
}

afterEvaluate {
    if(System.getProperty("idea.active") == "true") {
        config.generateConfiguration(HytalePluginConfig.Platform.INTELLIJIDEA)
    }
}