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

val unzipHytaleServerJar by tasks.registering(Copy::class) {
    from(zipTree(config.getHytaleServerJar().singleFile)) {
        include("com/hypixel/hytale/**")
    }
    into(layout.buildDirectory.dir("generated/sources/fernflower/tmp"))
}

tasks.register<JavaExec>("decompileJar") {
    dependsOn(unzipHytaleServerJar)

    val inputDir = layout.buildDirectory.dir("generated/sources/fernflower/tmp")
    val outputDir = layout.buildDirectory.dir("generated/sources/fernflower")

    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(
                java.toolchain.languageVersion.get()
            )
        }
    )

    group = "decompilation"
    description = "Decompile with FernFlower"

    classpath = configurations["fernflower"]
    mainClass.set(
        "org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler"
    )

    jvmArgs("-Xmx6G", "-Xms2G")

    args(
        "-dgs=1",
        "-asc=1",
        inputDir.get(),
        outputDir.get()
    )

    inputs.dir(inputDir)
    outputs.dir(outputDir)

    doLast {
        inputDir.get().asFile.deleteRecursively()
    }
}

afterEvaluate {
    if(System.getProperty("idea.active") == "true") {
        config.generateConfiguration(HytalePluginConfig.Platform.INTELLIJIDEA)
    }
}