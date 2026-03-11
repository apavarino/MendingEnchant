package me.crylonz.mendingenchant;

import me.crylonz.mendingenchant.command.MendingEnchantCommandHandler;
import me.crylonz.mendingenchant.config.MendingEnchantConfigValidator;
import me.crylonz.mendingenchant.listener.MendingEnchantFishingListener;
import me.crylonz.mendingenchant.listener.MendingEnchantItemListener;
import me.crylonz.mendingenchant.service.MendingEnchantFilterService;
import me.crylonz.mendingenchant.utils.MendingEnchantConfig;
import me.crylonz.mendingenchant.utils.MendingEnchantUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class MendingEnchant extends JavaPlugin {
    public static boolean allowMetrics = true;

    public final Logger log = Logger.getLogger("Minecraft");
    public final MendingEnchantConfig config = new MendingEnchantConfig(this);

    private final MendingEnchantFilterService filterService = new MendingEnchantFilterService(config);
    private final MendingEnchantConfigValidator configValidator = new MendingEnchantConfigValidator(this, config);
    private final MendingEnchantCommandHandler commandHandler = new MendingEnchantCommandHandler(this);

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MendingEnchantItemListener(this, filterService), this);
        pluginManager.registerEvents(new MendingEnchantFishingListener(this, filterService), this);

        if (allowMetrics) {
            new Metrics(this, 16292);
        }

        log.info("[MendingEnchant] is enabled !");

        registerConfig();
        reloadPluginConfiguration();

        if (config.getBoolean("updater.enabled")) {
            new MendingEnchantUpdater(this, 322356, this.getFile(), MendingEnchantUpdater.UpdateType.DEFAULT, true);
        }
    }

    @Override
    public void onDisable() {
        log.info("[MendingEnchant] is disabled !");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandHandler.handleCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return commandHandler.onTabComplete(sender, command, alias, args);
    }

    public void registerConfig() {
        config.register("updater.enabled", "auto-update", true);
        config.register("enchanting.probabilities.default", "DefaultProbability", 6.0);
        config.register("enchanting.probabilities.custom-permission-1", "CustomProbability1", 12.0);
        config.register("enchanting.probabilities.custom-permission-2", "CustomProbability2", 18.0);
        config.register("enchanting.probabilities.custom-permission-3", "CustomProbability3", 24.0);
        config.register("enchanting.item-filter.mode", "disabled");
        config.register("enchanting.item-filter.materials", Collections.emptyList());
        config.register("world-filter.mode", "disabled");
        config.register("world-filter.worlds", Collections.emptyList());
        config.register("fishing.probability", "FishingProbability", 5.0);
    }

    public void reloadPluginConfiguration() {
        reloadConfig();
        saveDefaultConfig();
        config.updateConfig();
        configValidator.validate();
    }
}
