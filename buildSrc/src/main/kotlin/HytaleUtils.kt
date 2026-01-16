import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import java.io.File

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

    fun generateConfiguration() {
        val dir = project.file(".idea/runConfigurations")
        dir.mkdirs()

        val file = File(dir, "HytaleServer.xml")
        file.writeText("""
        <component name="ProjectRunConfigurationManager">
          <configuration default="false" name="HytaleServer" type="Application" factoryName="Application">
            <module name="${project.name}.main" />
            <option name="MAIN_CLASS_NAME" value="${getHytaleMainClass()}" />
            <option name="PROGRAM_PARAMETERS" value="--allow-op --disable-sentry --assets=${getHytaleAssets().singleFile.absolutePath} --mods=build/resources/main --auth-mode=authenticated" />
            <option name="WORKING_DIRECTORY" value="${'$'}PROJECT_DIR$/run" />
          </configuration>
        </component>
        """.trimIndent())
    }

    private fun getHytaleMainClass(): String {
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