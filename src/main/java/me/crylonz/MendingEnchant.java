package me.crylonz;

import me.crylonz.utils.MendingEnchantConfig;
import me.crylonz.utils.MendingEnchantUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import java.util.logging.Logger;

import static org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_FISH;

public class MendingEnchant extends JavaPlugin implements Listener {
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

        File configFile = new File(getDataFolder(), "config.yml");

        registerConfig();

        if (!configFile.exists()) {
            saveDefaultConfig();
        } else {
            config.updateConfig();
        }

        if (config.getBoolean("auto-update")) {
            MendingEnchantUpdater updater = new MendingEnchantUpdater(this, 322356, this.getFile(), MendingEnchantUpdater.UpdateType.DEFAULT, true);
        }

    }

    public void onDisable() {
        this.log.info("[MendingEnchant] is disabled !");
    }

    public void registerConfig() {
        config.register("auto-update", true);
        config.register("DefaultProbability", 6.0);
        config.register("CustomProbability1", 12.0);
        config.register("CustomProbability2", 18.0);
        config.register("CustomProbability3", 24.0);
        config.register("FishingProbability", 5.0);
    }

    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent e) {

        if (e.getEnchanter().hasPermission("mendingenchant.use")) {
            double randomValue = Math.random() * 100;

            double div;
            if (e.getEnchanter().hasPermission("mendingenchant.custom1"))
                div = 30 / config.getDouble("CustomProbability1");
            else if (e.getEnchanter().hasPermission("mendingenchant.custom2"))
                div = 30 / config.getDouble("CustomProbability2");
            else if (e.getEnchanter().hasPermission("mendingenchant.custom3"))
                div = 30 / config.getDouble("CustomProbability3");
            else
                div = 30 / config.getDouble("DefaultProbability");

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
            if (randomValue <= config.getDouble("FishingProbability")) {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta) book.getItemMeta();
                esm.addStoredEnchant(Enchantment.MENDING, 1, false);
                book.setItemMeta(esm);

                e.getPlayer().getInventory().addItem(book);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
            }
        }
    }
} 