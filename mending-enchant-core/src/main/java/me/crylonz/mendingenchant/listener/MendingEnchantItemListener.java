package me.crylonz.mendingenchant.listener;

import me.crylonz.mendingenchant.MendingEnchant;
import me.crylonz.mendingenchant.service.MendingEnchantFilterService;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class MendingEnchantItemListener implements Listener {
    private final MendingEnchant plugin;
    private final MendingEnchantFilterService filterService;

    public MendingEnchantItemListener(MendingEnchant plugin, MendingEnchantFilterService filterService) {
        this.plugin = plugin;
        this.filterService = filterService;
    }

    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent event) {
        if (!event.getEnchanter().hasPermission("mendingenchant.use")
                || !filterService.isWorldAllowed(event.getEnchanter(), event.getEnchanter().getLocation())
                || !filterService.isItemAllowed(event.getEnchanter(), event.getItem())) {
            return;
        }

        double randomValue = Math.random() * 100;
        double div;
        if (event.getEnchanter().hasPermission("mendingenchant.custom1")) {
            div = 30 / plugin.config.getDouble("enchanting.probabilities.custom-permission-1");
        } else if (event.getEnchanter().hasPermission("mendingenchant.custom2")) {
            div = 30 / plugin.config.getDouble("enchanting.probabilities.custom-permission-2");
        } else if (event.getEnchanter().hasPermission("mendingenchant.custom3")) {
            div = 30 / plugin.config.getDouble("enchanting.probabilities.custom-permission-3");
        } else {
            div = 30 / plugin.config.getDouble("enchanting.probabilities.default");
        }

        randomValue += event.getExpLevelCost() / div;
        if (randomValue > 100 && event.getEnchantsToAdd().get(Enchantment.ARROW_INFINITE) == null) {
            event.getEnchantsToAdd().put(Enchantment.MENDING, 1);
            ItemStack item = event.getItem();
            String itemName = item == null ? "item" : item.getType().name();
            plugin.messages.sendMendingObtained(event.getEnchanter(), "notifications.enchanting-success", itemName);
        }
    }
}
