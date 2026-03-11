package me.crylonz.mendingenchant;

import me.crylonz.mendingenchant.utils.MendingEnchantConfig;
import me.crylonz.mendingenchant.utils.MendingEnchantUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import static org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_FISH;

public class MendingEnchant extends JavaPlugin implements Listener {
    private static final String RELOAD_PERMISSION = "mendingenchant.admin.reload";

    // metrics don't work during testing
    public static boolean allowMetrics = true;

    public final Logger log = Logger.getLogger("Minecraft");

    public MendingEnchantConfig config = new MendingEnchantConfig(this);

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        if(allowMetrics) {
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

        sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " reload");
        return true;
    }

    public void registerConfig() {
        config.register("updater.enabled", "auto-update", true);
        config.register("enchanting.probabilities.default", "DefaultProbability", 6.0);
        config.register("enchanting.probabilities.custom-permission-1", "CustomProbability1", 12.0);
        config.register("enchanting.probabilities.custom-permission-2", "CustomProbability2", 18.0);
        config.register("enchanting.probabilities.custom-permission-3", "CustomProbability3", 24.0);
        config.register("enchanting.item-filter.mode", "disabled");
        config.register("enchanting.item-filter.materials", java.util.Collections.emptyList());
        config.register("fishing.probability", "FishingProbability", 5.0);
    }

    public void reloadPluginConfiguration() {
        reloadConfig();
        saveDefaultConfig();
        config.updateConfig();
    }

    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent e) {
        if (e.getEnchanter().hasPermission("mendingenchant.use") && isItemAllowedForMending(e.getItem())) {
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
        if (e.getState() == CAUGHT_FISH) {
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

    private boolean isItemAllowedForMending(ItemStack item) {
        if (item == null) {
            return true;
        }

        String mode = config.getString("enchanting.item-filter.mode");
        if (mode == null) {
            return true;
        }

        Set<String> filteredMaterials = new HashSet<>();
        for (String materialName : config.getStringList("enchanting.item-filter.materials")) {
            filteredMaterials.add(materialName.toUpperCase(Locale.ROOT));
        }

        boolean listed = filteredMaterials.contains(item.getType().name());
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
}
