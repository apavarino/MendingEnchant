package me.crylonz.mendingenchant.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MendingEnchantConfig {

    private final Plugin plugin;
    private final Map<String, ConfigEntry> entries = new LinkedHashMap<>();

    public MendingEnchantConfig(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(String key, Object defaultValue) {
        register(key, null, defaultValue);
    }

    public void register(String key, String legacyKey, Object defaultValue) {
        entries.put(key, new ConfigEntry(key, legacyKey, defaultValue));
        plugin.getConfig().addDefault(key, defaultValue);
    }

    public Boolean getBoolean(String key) {
        return plugin.getConfig().getBoolean(key, (Boolean) getDefaultValue(key));
    }

    public double getDouble(String key) {
        Object defaultValue = getDefaultValue(key);
        return plugin.getConfig().getDouble(key, defaultValue instanceof Number ? ((Number) defaultValue).doubleValue() : 0.0D);
    }

    public int getInt(String key) {
        Object defaultValue = getDefaultValue(key);
        return plugin.getConfig().getInt(key, defaultValue instanceof Number ? ((Number) defaultValue).intValue() : 0);
    }

    public String getString(String key) {
        Object defaultValue = getDefaultValue(key);
        return plugin.getConfig().getString(key, defaultValue == null ? null : defaultValue.toString());
    }

    public List<String> getStringList(String key) {
        Object defaultValue = getDefaultValue(key);
        if (defaultValue instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<String> defaultList = (List<String>) defaultValue;
            return plugin.getConfig().getStringList(key).isEmpty() ? defaultList : plugin.getConfig().getStringList(key);
        }

        return plugin.getConfig().getStringList(key);
    }

    public void updateConfig() {
        plugin.reloadConfig();

        boolean changed = false;
        for (ConfigEntry entry : entries.values()) {
            Object currentValue = resolveValue(entry);
            if (!plugin.getConfig().contains(entry.key)) {
                plugin.getConfig().set(entry.key, currentValue);
                changed = true;
            }

            if (entry.legacyKey != null && plugin.getConfig().contains(entry.legacyKey)) {
                plugin.getConfig().set(entry.legacyKey, null);
                changed = true;
            }
        }

        plugin.getConfig().options().copyDefaults(true);
        if (changed) {
            plugin.getLogger().info("Updating config.yml to the latest format");
            plugin.saveConfig();
        }
    }

    public FileConfiguration getConfiguration() {
        return plugin.getConfig();
    }

    private Object resolveValue(ConfigEntry entry) {
        if (plugin.getConfig().contains(entry.key)) {
            return plugin.getConfig().get(entry.key);
        }

        if (entry.legacyKey != null && plugin.getConfig().contains(entry.legacyKey)) {
            return plugin.getConfig().get(entry.legacyKey);
        }

        return entry.defaultValue;
    }

    private Object getDefaultValue(String key) {
        ConfigEntry entry = entries.get(key);
        return entry == null ? null : entry.defaultValue;
    }

    private static final class ConfigEntry {
        private final String key;
        private final String legacyKey;
        private final Object defaultValue;

        private ConfigEntry(String key, String legacyKey, Object defaultValue) {
            this.key = key;
            this.legacyKey = legacyKey;
            this.defaultValue = defaultValue;
        }
    }
}
