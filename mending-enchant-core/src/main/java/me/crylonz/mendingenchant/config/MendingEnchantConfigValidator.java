package me.crylonz.mendingenchant.config;

import me.crylonz.mendingenchant.service.MendingEnchantFilterService;
import me.crylonz.mendingenchant.utils.MendingEnchantConfig;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public class MendingEnchantConfigValidator {
    private final Plugin plugin;
    private final MendingEnchantConfig config;
    private final Server server;

    public MendingEnchantConfigValidator(Plugin plugin, MendingEnchantConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.server = plugin.getServer();
    }

    public void validate() {
        boolean changed = false;
        changed |= validateFilterMode("enchanting.item-filter.mode");
        changed |= validateFilterMode("world-filter.mode");
        changed |= validateConfiguredMaterials();
        changed |= validateConfiguredWorlds();

        if (changed) {
            plugin.saveConfig();
        }
    }

    private boolean validateFilterMode(String path) {
        String configuredMode = config.getString(path);
        if (configuredMode == null) {
            plugin.getLogger().warning("Missing filter mode at '" + path + "'. Falling back to 'disabled'.");
            plugin.getConfig().set(path, "disabled");
            return true;
        }

        String normalizedMode = MendingEnchantFilterService.normalizeFilterMode(configuredMode);
        if (!MendingEnchantFilterService.isValidFilterMode(normalizedMode)) {
            plugin.getLogger().warning("Invalid filter mode '" + configuredMode + "' at '" + path + "'. Falling back to 'disabled'.");
            plugin.getConfig().set(path, "disabled");
            return true;
        }

        if (!configuredMode.equals(normalizedMode)) {
            plugin.getConfig().set(path, normalizedMode);
            return true;
        }

        return false;
    }

    private boolean validateConfiguredMaterials() {
        List<String> configuredMaterials = config.getStringList("enchanting.item-filter.materials");
        List<String> normalizedMaterials = new ArrayList<>();
        for (String materialName : configuredMaterials) {
            Material material = MendingEnchantFilterService.resolveMaterial(materialName);
            if (material == null) {
                plugin.getLogger().warning("Unknown material '" + materialName + "' in 'enchanting.item-filter.materials'. It will be ignored.");
                continue;
            }

            normalizedMaterials.add(material.name());
        }

        List<String> deduplicatedMaterials = new ArrayList<>(new LinkedHashSet<>(normalizedMaterials));
        if (!configuredMaterials.equals(deduplicatedMaterials)) {
            plugin.getConfig().set("enchanting.item-filter.materials", deduplicatedMaterials);
            return true;
        }

        return false;
    }

    private boolean validateConfiguredWorlds() {
        List<String> configuredWorlds = config.getStringList("world-filter.worlds");
        List<String> normalizedWorlds = new ArrayList<>();
        for (String worldName : configuredWorlds) {
            if (server.getWorld(worldName) == null) {
                plugin.getLogger().warning("Unknown world '" + worldName + "' in 'world-filter.worlds'. It will be ignored.");
                continue;
            }

            normalizedWorlds.add(worldName.toLowerCase(Locale.ROOT));
        }

        List<String> deduplicatedWorlds = new ArrayList<>(new LinkedHashSet<>(normalizedWorlds));
        if (!configuredWorlds.equals(deduplicatedWorlds)) {
            plugin.getConfig().set("world-filter.worlds", deduplicatedWorlds);
            return true;
        }

        return false;
    }
}
