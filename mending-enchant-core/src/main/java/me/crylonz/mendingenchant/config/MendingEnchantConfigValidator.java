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
        changed |= validateLocalizationLocale();
        changed |= validateProbability("fishing.probability");
        changed |= validateProbability("enchanting.probabilities.default");
        changed |= validateProbability("enchanting.probabilities.custom-permission-1");
        changed |= validateProbability("enchanting.probabilities.custom-permission-2");
        changed |= validateProbability("enchanting.probabilities.custom-permission-3");
        changed |= validatePityValues();
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

    private boolean validateLocalizationLocale() {
        String locale = config.getString("localization.locale");
        if (locale == null || locale.trim().isEmpty()) {
            plugin.getLogger().warning("Missing locale in 'localization.locale'. Falling back to 'en_US'.");
            plugin.getConfig().set("localization.locale", "en_US");
            return true;
        }

        return false;
    }

    private boolean validateProbability(String path) {
        double value = config.getDouble(path);
        double clamped = Math.max(0.0, Math.min(100.0, value));
        if (value != clamped) {
            plugin.getLogger().warning("Invalid probability '" + value + "' at '" + path + "'. Clamping to " + clamped + ".");
            plugin.getConfig().set(path, clamped);
            return true;
        }

        return false;
    }

    private boolean validatePityValues() {
        boolean changed = false;

        double bonusPerFailure = config.getDouble("enchanting.pity.bonus-per-failure");
        if (bonusPerFailure < 0.0) {
            plugin.getLogger().warning("Invalid pity bonus-per-failure '" + bonusPerFailure + "'. Falling back to 0.0.");
            plugin.getConfig().set("enchanting.pity.bonus-per-failure", 0.0);
            changed = true;
        }

        double maxBonus = config.getDouble("enchanting.pity.max-bonus");
        double normalizedMaxBonus = Math.max(0.0, Math.min(100.0, maxBonus));
        if (maxBonus != normalizedMaxBonus) {
            plugin.getLogger().warning("Invalid pity max-bonus '" + maxBonus + "'. Clamping to " + normalizedMaxBonus + ".");
            plugin.getConfig().set("enchanting.pity.max-bonus", normalizedMaxBonus);
            changed = true;
        }

        return changed;
    }
}
