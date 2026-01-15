# Hytale Plugin Template 🏗️

A plugin template for Hytale.

## Building 🔧

```bash
./gradlew build
```

The compiled plugin JAR will be in `build/libs/`.

## Setup 🚧

1. If you haven't installed Hytale in the default directory, specify your custom directory in your `gradle.properties` file like this:
    ```properties
    hytale_directory=C:\Users\<User>\AppData\Roaming\Hytale\install
    ```
2. Customize your `gradle.properties` (you should change only the properties prefixed with "plugin_")
3. Edit your `src/main/resources/manifest.json` file to have finer control on your plugin's metadata and to specify authors. You can see an example [here](https://britakee-studios.gitbook.io/hytale-modding-documentation/plugins-java-development/07-getting-started-with-plugins#id-3.3-update-manifest-file) (all the properties containing `${something}` will be populated when the project is built or run).

## Installation ⬇️

1. Run the `HytaleServer` task in IntellijIDEA to run a Hytale server.
2. When the server is up and running, use the command `/auth login device` in the console to login into your Hytale account. Since the authentication doesn't persist between sessions, you can also set the authentication save method to `Encrypted`.
3. Connect to your server through your Hytale Client (ip: localhost, 127.0.0.1).


## Requirements 📃

- Java 25+
- Hytale Game (and Account)
- IntellijIDEA (any edition)

## License 📄

[MIT](LICENSE)

## Credits 👀

This repo is based on [vulpeslab/hytale-example-plugin](https://github.com/vulpeslab/hytale-example-plugin).