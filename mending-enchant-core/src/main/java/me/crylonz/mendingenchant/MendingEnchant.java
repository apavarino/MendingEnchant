package me.crylonz.mendingenchant;

import me.crylonz.mendingenchant.utils.MendingEnchantConfig;
import me.crylonz.mendingenchant.utils.MendingEnchantUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

import static org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_FISH;

public class MendingEnchant extends JavaPlugin implements Listener, TabCompleter {
    private static final String RELOAD_PERMISSION = "mendingenchant.admin.reload";
    private static final String INFO_PERMISSION = "mendingenchant.admin.info";
    private static final String BYPASS_ITEM_FILTER_PERMISSION = "mendingenchant.bypass.itemfilter";
    private static final String BYPASS_WORLD_FILTER_PERMISSION = "mendingenchant.bypass.worldfilter";
    private static final Set<String> VALID_FILTER_MODES = new HashSet<>(Arrays.asList("disabled", "whitelist", "blacklist"));

    // metrics don't work during testing
    public static boolean allowMetrics = true;

    public final Logger log = Logger.getLogger("Minecraft");

    public MendingEnchantConfig config = new MendingEnchantConfig(this);

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        if (allowMetrics) {
            Metrics metrics = new Metrics(this, 16292);
        }
        this.log.info("[MendingEnchant] is enabled !");

        registerConfig();
        reloadPluginConfiguration();

        if (config.getBoolean("updater.enabled")) {
            MendingEnchantUpdater updater = new MendingEnchantUpdater(this, 322356, this.getFile(), MendingEnchantUpdater.UpdateType.DEFAULT, true);
        }

    }

    public void onDisable() {
        this.log.info("[MendingEnchant] is disabled !");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mendingenchant")) {
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(RELOAD_PERMISSION)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            reloadPluginConfiguration();
            sender.sendMessage(ChatColor.GREEN + "MendingEnchant configuration reloaded.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission(INFO_PERMISSION)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            sendInfo(sender);
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <reload|info>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("mendingenchant")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission(RELOAD_PERMISSION)) {
                completions.add("reload");
            }
            if (sender.hasPermission(INFO_PERMISSION)) {
                completions.add("info");
            }

            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> filteredCompletions = new ArrayList<>();
            for (String completion : completions) {
                if (completion.startsWith(prefix)) {
                    filteredCompletions.add(completion);
                }
            }
            return filteredCompletions;
        }

        return Collections.emptyList();
    }

    public void registerConfig() {
        config.register("updater.enabled", "auto-update", true);
        config.register("enchanting.probabilities.default", "DefaultProbability", 6.0);
        config.register("enchanting.probabilities.custom-permission-1", "CustomProbability1", 12.0);
        config.register("enchanting.probabilities.custom-permission-2", "CustomProbability2", 18.0);
        config.register("enchanting.probabilities.custom-permission-3", "CustomProbability3", 24.0);
        config.register("enchanting.item-filter.mode", "disabled");
        config.register("enchanting.item-filter.materials", java.util.Collections.emptyList());
        config.register("world-filter.mode", "disabled");
        config.register("world-filter.worlds", java.util.Collections.emptyList());
        config.register("fishing.probability", "FishingProbability", 5.0);
    }

    public void reloadPluginConfiguration() {
        reloadConfig();
        saveDefaultConfig();
        config.updateConfig();
        validateConfiguration();
    }

    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent e) {
        if (e.getEnchanter().hasPermission("mendingenchant.use")
                && isWorldAllowed(e.getEnchanter(), e.getEnchanter().getLocation())
                && isItemAllowedForMending(e.getEnchanter(), e.getItem())) {
            double randomValue = Math.random() * 100;

            double div;
            if (e.getEnchanter().hasPermission("mendingenchant.custom1"))
                div = 30 / config.getDouble("enchanting.probabilities.custom-permission-1");
            else if (e.getEnchanter().hasPermission("mendingenchant.custom2"))
                div = 30 / config.getDouble("enchanting.probabilities.custom-permission-2");
            else if (e.getEnchanter().hasPermission("mendingenchant.custom3"))
                div = 30 / config.getDouble("enchanting.probabilities.custom-permission-3");
            else
                div = 30 / config.getDouble("enchanting.probabilities.default");

            randomValue += e.getExpLevelCost() / div;

            if (randomValue > 100 && e.getEnchantsToAdd().get(Enchantment.ARROW_INFINITE) == null) {
                e.getEnchantsToAdd().put(Enchantment.MENDING, 1);
            }
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (e.getState() == CAUGHT_FISH && isWorldAllowed(e.getPlayer(), e.getPlayer().getLocation())) {
            double randomValue = Math.random() * 100;
            if (randomValue <= config.getDouble("fishing.probability")) {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta) book.getItemMeta();
                esm.addStoredEnchant(Enchantment.MENDING, 1, true);
                book.setItemMeta(esm);

                e.getPlayer().getInventory().addItem(book);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
            }
        }
    }

    private boolean isItemAllowedForMending(CommandSender sender, ItemStack item) {
        if (sender.hasPermission(BYPASS_ITEM_FILTER_PERMISSION)) {
            return true;
        }

        if (item == null) {
            return true;
        }

        return isAllowedByMode(config.getString("enchanting.item-filter.mode"), getConfiguredMaterials().contains(item.getType().name()));
    }

    private boolean isWorldAllowed(CommandSender sender, Location location) {
        if (sender.hasPermission(BYPASS_WORLD_FILTER_PERMISSION)) {
            return true;
        }

        if (location == null || location.getWorld() == null) {
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

    private void validateConfiguration() {
        boolean changed = false;
        changed |= validateFilterMode("enchanting.item-filter.mode");
        changed |= validateFilterMode("world-filter.mode");
        changed |= validateConfiguredMaterials();
        changed |= validateConfiguredWorlds();

        if (changed) {
            saveConfig();
        }
    }

    private boolean validateFilterMode(String path) {
        String configuredMode = config.getString(path);
        if (configuredMode == null) {
            getLogger().warning("Missing filter mode at '" + path + "'. Falling back to 'disabled'.");
            getConfig().set(path, "disabled");
            return true;
        }

        String normalizedMode = configuredMode.toLowerCase(Locale.ROOT);
        if (!VALID_FILTER_MODES.contains(normalizedMode)) {
            getLogger().warning("Invalid filter mode '" + configuredMode + "' at '" + path + "'. Falling back to 'disabled'.");
            getConfig().set(path, "disabled");
            return true;
        }

        if (!configuredMode.equals(normalizedMode)) {
            getConfig().set(path, normalizedMode);
            return true;
        }

        return false;
    }

    private boolean validateConfiguredMaterials() {
        List<String> configuredMaterials = config.getStringList("enchanting.item-filter.materials");
        List<String> normalizedMaterials = new ArrayList<>();
        for (String materialName : configuredMaterials) {
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                getLogger().warning("Unknown material '" + materialName + "' in 'enchanting.item-filter.materials'. It will be ignored.");
                continue;
            }

            normalizedMaterials.add(material.name());
        }

        List<String> deduplicatedMaterials = new ArrayList<>(new LinkedHashSet<>(normalizedMaterials));
        if (!configuredMaterials.equals(deduplicatedMaterials)) {
            getConfig().set("enchanting.item-filter.materials", deduplicatedMaterials);
            return true;
        }

        return false;
    }

    private boolean validateConfiguredWorlds() {
        List<String> configuredWorlds = config.getStringList("world-filter.worlds");
        List<String> normalizedWorlds = new ArrayList<>();
        for (String worldName : configuredWorlds) {
            if (getServer().getWorld(worldName) == null) {
                getLogger().warning("Unknown world '" + worldName + "' in 'world-filter.worlds'. It will be ignored.");
                continue;
            }

            normalizedWorlds.add(worldName.toLowerCase(Locale.ROOT));
        }

        List<String> deduplicatedWorlds = new ArrayList<>(new LinkedHashSet<>(normalizedWorlds));
        if (!configuredWorlds.equals(deduplicatedWorlds)) {
            getConfig().set("world-filter.worlds", deduplicatedWorlds);
            return true;
        }

        return false;
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

    private void sendInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "MendingEnchant " + getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Updater: " + ChatColor.WHITE + config.getBoolean("updater.enabled"));
        sender.sendMessage(ChatColor.YELLOW + "Enchant probabilities: " + ChatColor.WHITE
                + "default=" + config.getDouble("enchanting.probabilities.default")
                + ", custom1=" + config.getDouble("enchanting.probabilities.custom-permission-1")
                + ", custom2=" + config.getDouble("enchanting.probabilities.custom-permission-2")
                + ", custom3=" + config.getDouble("enchanting.probabilities.custom-permission-3"));
        sender.sendMessage(ChatColor.YELLOW + "Fishing probability: " + ChatColor.WHITE + config.getDouble("fishing.probability"));
        sender.sendMessage(ChatColor.YELLOW + "Item filter: " + ChatColor.WHITE
                + config.getString("enchanting.item-filter.mode")
                + " " + formatList(config.getStringList("enchanting.item-filter.materials")));
        sender.sendMessage(ChatColor.YELLOW + "World filter: " + ChatColor.WHITE
                + config.getString("world-filter.mode")
                + " " + formatList(config.getStringList("world-filter.worlds")));
    }

    private String formatList(List<String> values) {
        return values.isEmpty() ? "(none)" : values.toString();
    }
}
