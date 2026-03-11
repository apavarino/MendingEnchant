package me.crylonz.mendingenchant.service;

import me.crylonz.mendingenchant.utils.MendingEnchantConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MendingEnchantFilterService {
    public static final String BYPASS_ITEM_FILTER_PERMISSION = "mendingenchant.bypass.itemfilter";
    public static final String BYPASS_WORLD_FILTER_PERMISSION = "mendingenchant.bypass.worldfilter";

    private final MendingEnchantConfig config;

    public MendingEnchantFilterService(MendingEnchantConfig config) {
        this.config = config;
    }

    public boolean isItemAllowed(CommandSender sender, ItemStack item) {
        if (sender.hasPermission(BYPASS_ITEM_FILTER_PERMISSION) || item == null) {
            return true;
        }

        return isAllowedByMode(config.getString("enchanting.item-filter.mode"), getConfiguredMaterials().contains(item.getType().name()));
    }

    public boolean isWorldAllowed(CommandSender sender, Location location) {
        if (sender.hasPermission(BYPASS_WORLD_FILTER_PERMISSION) || location == null || location.getWorld() == null) {
            return true;
        }

        return isAllowedByMode(config.getString("world-filter.mode"), getConfiguredWorlds().contains(location.getWorld().getName().toLowerCase(Locale.ROOT)));
    }

    private boolean isAllowedByMode(String mode, boolean listed) {
        if (mode == null) {
            return true;
        }

        switch (mode.toLowerCase(Locale.ROOT)) {
            case "whitelist":
                return listed;
            case "blacklist":
                return !listed;
            case "disabled":
            default:
                return true;
        }
    }

    private Set<String> getConfiguredMaterials() {
        Set<String> filteredMaterials = new HashSet<>();
        for (String materialName : config.getStringList("enchanting.item-filter.materials")) {
            filteredMaterials.add(materialName.toUpperCase(Locale.ROOT));
        }
        return filteredMaterials;
    }

    private Set<String> getConfiguredWorlds() {
        Set<String> filteredWorlds = new HashSet<>();
        for (String worldName : config.getStringList("world-filter.worlds")) {
            filteredWorlds.add(worldName.toLowerCase(Locale.ROOT));
        }
        return filteredWorlds;
    }

    public static boolean isValidFilterMode(String mode) {
        return "disabled".equals(mode) || "whitelist".equals(mode) || "blacklist".equals(mode);
    }

    public static String normalizeFilterMode(String mode) {
        return mode == null ? null : mode.toLowerCase(Locale.ROOT);
    }

    public static Material resolveMaterial(String materialName) {
        return Material.matchMaterial(materialName);
    }
}
