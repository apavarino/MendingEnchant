package me.crylonz.mendingenchant.listener;

import me.crylonz.mendingenchant.MendingEnchant;
import me.crylonz.mendingenchant.service.MendingEnchantFilterService;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import static org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_FISH;

public class MendingEnchantFishingListener implements Listener {
    private final MendingEnchant plugin;
    private final MendingEnchantFilterService filterService;

    public MendingEnchantFishingListener(MendingEnchant plugin, MendingEnchantFilterService filterService) {
        this.plugin = plugin;
        this.filterService = filterService;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != CAUGHT_FISH || !filterService.isWorldAllowed(event.getPlayer(), event.getPlayer().getLocation())) {
            return;
        }

        double randomValue = Math.random() * 100;
        if (randomValue <= plugin.config.getDouble("fishing.probability")) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(Enchantment.MENDING, 1, true);
            book.setItemMeta(meta);

            event.getPlayer().getInventory().addItem(book);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
            plugin.messages.sendMendingObtained(event.getPlayer(), "notifications.fishing-success", "ENCHANTED_BOOK");
        }
    }
}
