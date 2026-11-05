package com.mrmelon54.DraggableLists.config;

import com.mrmelon54.DraggableLists.DraggableLists;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

/**
 * Settings in config/draggable_lists.properties. A plain file keeps the mod free of dependencies.
 */
public class DraggableConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DraggableLists.MOD_ID);

    public boolean disableResourcePackArrows = true;
    public boolean disableServerArrows = true;
    public EnabledMode resourcePackDraggingEnabled = EnabledMode.ENABLED;
    public EnabledMode serverDraggingEnabled = EnabledMode.ENABLED;

    public static DraggableConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(DraggableLists.MOD_ID + ".properties");
        DraggableConfig config = new DraggableConfig();
        Properties properties = new Properties();

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                properties.load(reader);
            } catch (IOException | IllegalArgumentException e) {
                LOGGER.warn("Could not read {}, using defaults", path, e);
            }
            config.disableResourcePackArrows = readBoolean(properties, "disableResourcePackArrows", config.disableResourcePackArrows);
            config.disableServerArrows = readBoolean(properties, "disableServerArrows", config.disableServerArrows);
            config.resourcePackDraggingEnabled = readMode(properties, "resourcePackDraggingEnabled", config.resourcePackDraggingEnabled);
            config.serverDraggingEnabled = readMode(properties, "serverDraggingEnabled", config.serverDraggingEnabled);
        } else {
            config.save(path);
        }
        return config;
    }

    private void save(Path path) {
        Properties properties = new Properties();
        properties.setProperty("disableResourcePackArrows", Boolean.toString(disableResourcePackArrows));
        properties.setProperty("disableServerArrows", Boolean.toString(disableServerArrows));
        properties.setProperty("resourcePackDraggingEnabled", resourcePackDraggingEnabled.name());
        properties.setProperty("serverDraggingEnabled", serverDraggingEnabled.name());

        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                properties.store(writer, "Draggable Lists. Dragging modes: DISABLED, REQUIRES_MODIFIER (hold Shift), ENABLED");
            }
        } catch (IOException e) {
            LOGGER.warn("Could not write {}", path, e);
        }
    }

    private static boolean readBoolean(Properties properties, String key, boolean fallback) {
        String value = properties.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value.trim());
    }

    private static EnabledMode readMode(Properties properties, String key, EnabledMode fallback) {
        String value = properties.getProperty(key);
        if (value == null) return fallback;
        try {
            return EnabledMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Unknown value {} for {}", value, key);
            return fallback;
        }
    }
}
