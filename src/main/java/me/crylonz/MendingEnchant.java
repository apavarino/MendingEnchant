package me.crylonz;

import org.bstats.bukkit.Metrics;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class MendingEnchant extends JavaPlugin implements Listener {

    public final Logger log = Logger.getLogger("Minecraft");
    private final double defaultRate = 6;
    private double rateAtLevel30 = defaultRate;
    private double custom1 = defaultRate * 2;
    private double custom2 = defaultRate * 3;
    private double custom3 = defaultRate * 4;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        Metrics metrics = new Metrics(this, 16292);
        this.log.info("[MendingEnchant] is enabled !");

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {

            getConfig().options().header("DefaultProbability : Probability to have mending in percentage between 0% and 100% at enchant level 30.\n" +
                    "0% = No chance to have Mending, 100% = Mending on all enchant\n" +
                    "Other enchant levels adapt to this value (if rate is set to 10% you have 5% chance to have it at level 15)\n \n" +
                    "You can also setup 3 customs probabilities : CustomProbabilty1 will be apply to player with permission mendingenchant.custom1\n" +
                    "Same logic for CustomProbabilty2 and CustomProbabilty3\n" +
                    "More information on project page : https://dev.bukkit.org/projects/mendingenchant\n" +
                    "YOU NEED TO RELOAD/RESTART AFTER ANY CHANGE");

            getConfig().set("DefaultProbability", defaultRate);
            getConfig().set("DefaultProbability", defaultRate);
            getConfig().set("CustomProbabilty1", custom1);
            getConfig().set("CustomProbabilty2", custom2);
            getConfig().set("CustomProbabilty3", custom3);
            saveConfig();
        } else {
            rateAtLevel30 = getConfig().getInt("DefaultProbability");
            custom1 = getConfig().getInt("CustomProbabilty1");
            custom2 = getConfig().getInt("CustomProbabilty2");
            custom3 = getConfig().getInt("CustomProbabilty3");
        }
    }

    public void onDisable() {
        this.log.info("[MendingEnchant] is disabled !");
    }

    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent e) {

        if (e.getEnchanter().hasPermission("mendingenchant.use")) {
            double randomValue = Math.random() * 100;

            double div;
            if (e.getEnchanter().hasPermission("mendingenchant.custom1"))
                div = 30 / custom1;
            else if (e.getEnchanter().hasPermission("mendingenchant.custom2"))
                div = 30 / custom2;
            else if (e.getEnchanter().hasPermission("mendingenchant.custom3"))
                div = 30 / custom3;
            else
                div = 30 / rateAtLevel30;

            randomValue += e.getExpLevelCost() / div;

            if (randomValue > 100) {
                e.getEnchantsToAdd().put(Enchantment.MENDING, 1);
            }
        }
    }
} 