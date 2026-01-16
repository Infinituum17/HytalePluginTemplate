import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection


fun Project.getRunDirectory(): String {
    val directory = file("$projectDir/run")

    if(!directory.exists()) {
        directory.mkdir()
    }

    return directory.absolutePath
}

class HytalePluginConfig(private val project: Project) {
    val javaVersion: String
        get() = getRequiredProperty("java_version")

    val pluginGroup: String
        get() = getRequiredProperty("plugin_group")

    val pluginName: String
        get() = getRequiredProperty("plugin_name")

    val pluginVersion: String
        get() = getRequiredProperty("plugin_version")

    val pluginDescription: String
        get() = getRequiredProperty("plugin_description")

    val pluginWebsite: String
        get() = getRequiredProperty("plugin_website")

    val hytaleVersion: String
        get() = getRequiredProperty("hytale_version")

    val hytaleVersionType: String
        get() = getRequiredProperty("hytale_version_type")

    val manifest: Map<String, String>
        get() = mapOf(
            "plugin_group" to this.pluginGroup,
            "plugin_name" to this.pluginName,
            "plugin_version" to this.pluginVersion,
            "plugin_description" to this.pluginDescription,
            "plugin_main" to "${this.pluginGroup}.${this.pluginName}",
            "plugin_website" to this.pluginWebsite,
        )

    fun getHytaleAssets(): FileCollection {
        return project.files("${getHytaleDirectory()}/Assets.zip")
    }

    fun getHytaleServerJar(): FileCollection {
        return project.files("${getHytaleDirectory()}/Server/HytaleServer.jar")
    }

    fun getHytaleMainClass(): String {
        return "com.hypixel.hytale.Main"
    }

    private fun getRequiredProperty(propertyName: String): String {
        return project.providers.gradleProperty(propertyName).orNull
            ?: throw GradleException("Cannot find property '$propertyName' in gradle.properties file")
    }

    private fun getHytaleDirectory(): String {
        val hytaleDir = project.providers.gradleProperty("hytale_directory")
            .getOrElse("${System.getProperty("user.home")}/AppData/Roaming/Hytale/install")

        return "$hytaleDir/$hytaleVersionType/package/game/$hytaleVersion"
    }
}