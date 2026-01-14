package com.example_plugin;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class ExamplePlugin extends JavaPlugin {
    private static ExamplePlugin instance;

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public static ExamplePlugin get() {
        return instance;
    }

    @Override
    protected void setup() {
        instance = this;

        getLogger().at(Level.INFO).log("ExamplePlugin setup complete!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("ExamplePlugin started!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("ExamplePlugin shutting down!");
    }
}
