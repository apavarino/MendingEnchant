package me.crylonz.mendingenchant.filter;

import be.seeseemelk.mockbukkit.entity.FishHookMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.crylonz.mendingenchant.support.MendingEnchantTestBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MendingEnchantFilterIntegrationTest extends MendingEnchantTestBase {

    @Test
    @DisplayName("Fishing probability should be set at 100% for the tests")
    public void checkFishingProbability() {
        Assertions.assertEquals(100.0, plugin.config.getDouble("fishing.probability"));
    }

    @Test
    @DisplayName("Fishing should give mending book")
    public void checkFishingResults() {
        PlayerMock player = server.addPlayer();
        plugin.random.setRandomSupplier(() -> 0.0);
        server.getPluginManager().callEvent(new PlayerFishEvent(player, null, new FishHookMock(server, new UUID(1, 2)), PlayerFishEvent.State.CAUGHT_FISH));

        ItemStack item = player.getInventory().getItem(0);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(Material.ENCHANTED_BOOK, item.getType());
        Assertions.assertTrue(((EnchantmentStorageMeta) item.getItemMeta()).hasStoredEnchant(Enchantment.MENDING));
        Assertions.assertTrue(player.nextMessage().contains("Mending book"));
    }

    @Test
    @DisplayName("Enchanting should give the mending enchant to the tool")
    public void enchantingForMending() {
        PlayerMock player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);
        plugin.random.setRandomSupplier(() -> 95.0);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        EnchantItemEvent event = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 100, enchants, null, 10, 1);
        server.getPluginManager().callEvent(event);

        Assertions.assertNotNull(event.getEnchantsToAdd().get(Enchantment.MENDING));
        Assertions.assertTrue(player.nextMessage().contains("DIAMOND_PICKAXE"));
    }

    @Test
    @DisplayName("Mending should not be in the enchant list if there is already Infinity")
    public void noMendingIfInfinity() {
        Player player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);
        plugin.random.setRandomSupplier(() -> 95.0);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.ARROW_INFINITE, 1);

        EnchantItemEvent event = new EnchantItemEvent(player, null, null, new ItemStack(Material.BOW), 100, enchants, null, 10, 1);
        server.getPluginManager().callEvent(event);

        Assertions.assertNull(event.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Mending should not be added to blacklisted items")
    public void noMendingForBlacklistedItem() {
        plugin.getConfig().set("enchanting.item-filter.mode", "blacklist");
        plugin.getConfig().set("enchanting.item-filter.materials", Collections.singletonList("DIAMOND_PICKAXE"));
        plugin.saveConfig();

        Player player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        EnchantItemEvent event = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 100, enchants, null, 10, 1);
        server.getPluginManager().callEvent(event);

        Assertions.assertNull(event.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Mending should only be added to whitelisted items")
    public void mendingOnlyForWhitelistedItem() {
        plugin.getConfig().set("enchanting.item-filter.mode", "whitelist");
        plugin.getConfig().set("enchanting.item-filter.materials", Collections.singletonList("DIAMOND_PICKAXE"));
        plugin.saveConfig();

        Player player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);
        plugin.random.setRandomSupplier(() -> 95.0);

        Map<Enchantment, Integer> allowedEnchants = new HashMap<>();
        EnchantItemEvent allowedEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 100, allowedEnchants, null, 10, 1);
        server.getPluginManager().callEvent(allowedEvent);

        Map<Enchantment, Integer> blockedEnchants = new HashMap<>();
        EnchantItemEvent blockedEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_SWORD), 100, blockedEnchants, null, 10, 1);
        server.getPluginManager().callEvent(blockedEvent);

        Assertions.assertNotNull(allowedEvent.getEnchantsToAdd().get(Enchantment.MENDING));
        Assertions.assertNull(blockedEvent.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Mending should not be added in blacklisted worlds")
    public void noMendingInBlacklistedWorld() {
        World blockedWorld = server.addSimpleWorld("blocked_world");
        Player player = server.addPlayer();
        player.teleport(new Location(blockedWorld, 0, 64, 0));

        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);

        plugin.getConfig().set("world-filter.mode", "blacklist");
        plugin.getConfig().set("world-filter.worlds", Collections.singletonList("blocked_world"));
        plugin.saveConfig();

        Map<Enchantment, Integer> enchants = new HashMap<>();
        EnchantItemEvent event = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 100, enchants, null, 10, 1);
        server.getPluginManager().callEvent(event);

        Assertions.assertNull(event.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Fishing should only give mending books in whitelisted worlds")
    public void fishingOnlyInWhitelistedWorlds() {
        World allowedWorld = server.getWorlds().get(0);
        World blockedWorld = server.addSimpleWorld("blocked_world");
        plugin.random.setRandomSupplier(() -> 0.0);

        Player blockedPlayer = server.addPlayer("blocked");
        blockedPlayer.teleport(new Location(blockedWorld, 0, 64, 0));

        Player allowedPlayer = server.addPlayer("allowed");
        allowedPlayer.teleport(new Location(allowedWorld, 0, 64, 0));

        plugin.getConfig().set("world-filter.mode", "whitelist");
        plugin.getConfig().set("world-filter.worlds", Collections.singletonList(allowedWorld.getName()));
        plugin.saveConfig();

        server.getPluginManager().callEvent(new PlayerFishEvent(blockedPlayer, null, new FishHookMock(server, new UUID(3, 4)), PlayerFishEvent.State.CAUGHT_FISH));
        server.getPluginManager().callEvent(new PlayerFishEvent(allowedPlayer, null, new FishHookMock(server, new UUID(5, 6)), PlayerFishEvent.State.CAUGHT_FISH));

        Assertions.assertNull(blockedPlayer.getInventory().getItem(0));
        Assertions.assertNotNull(allowedPlayer.getInventory().getItem(0));
    }

    @Test
    @DisplayName("Item filter bypass permission should ignore blacklisted items")
    public void itemFilterBypassShouldAllowMending() {
        plugin.getConfig().set("enchanting.item-filter.mode", "blacklist");
        plugin.getConfig().set("enchanting.item-filter.materials", Collections.singletonList("DIAMOND_PICKAXE"));
        plugin.saveConfig();

        Player player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);
        attachment.setPermission("mendingenchant.bypass.itemfilter", true);
        plugin.random.setRandomSupplier(() -> 95.0);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        EnchantItemEvent event = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 100, enchants, null, 10, 1);
        server.getPluginManager().callEvent(event);

        Assertions.assertNotNull(event.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("World filter bypass permission should ignore blacklisted worlds")
    public void worldFilterBypassShouldAllowMending() {
        World blockedWorld = server.addSimpleWorld("blocked_world");
        Player player = server.addPlayer();
        player.teleport(new Location(blockedWorld, 0, 64, 0));

        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);
        attachment.setPermission("mendingenchant.bypass.worldfilter", true);
        plugin.random.setRandomSupplier(() -> 95.0);

        plugin.getConfig().set("world-filter.mode", "blacklist");
        plugin.getConfig().set("world-filter.worlds", Collections.singletonList("blocked_world"));
        plugin.saveConfig();

        Map<Enchantment, Integer> enchants = new HashMap<>();
        EnchantItemEvent event = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 100, enchants, null, 10, 1);
        server.getPluginManager().callEvent(event);

        Assertions.assertNotNull(event.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Pity system should increase chance after failures")
    public void pitySystemShouldIncreaseChanceAfterFailures() {
        PlayerMock player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);

        plugin.getConfig().set("enchanting.probabilities.default", 6.0);
        plugin.getConfig().set("enchanting.pity.enabled", true);
        plugin.getConfig().set("enchanting.pity.bonus-per-failure", 20.0);
        plugin.getConfig().set("enchanting.pity.max-bonus", 40.0);
        plugin.saveConfig();
        plugin.random.setRandomSupplier(() -> 85.0);

        EnchantItemEvent firstEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1);
        server.getPluginManager().callEvent(firstEvent);

        EnchantItemEvent secondEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1);
        server.getPluginManager().callEvent(secondEvent);

        Assertions.assertNull(firstEvent.getEnchantsToAdd().get(Enchantment.MENDING));
        Assertions.assertNotNull(secondEvent.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Pity system should reset after a success")
    public void pitySystemShouldResetAfterSuccess() {
        Player player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);

        plugin.getConfig().set("enchanting.probabilities.default", 6.0);
        plugin.getConfig().set("enchanting.pity.enabled", true);
        plugin.getConfig().set("enchanting.pity.bonus-per-failure", 20.0);
        plugin.getConfig().set("enchanting.pity.max-bonus", 40.0);
        plugin.saveConfig();
        plugin.random.setRandomSupplier(() -> 85.0);

        server.getPluginManager().callEvent(new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1));
        server.getPluginManager().callEvent(new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1));

        Assertions.assertEquals(0, plugin.pity.getFailures(player.getUniqueId()));

        EnchantItemEvent thirdEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1);
        server.getPluginManager().callEvent(thirdEvent);

        Assertions.assertNull(thirdEvent.getEnchantsToAdd().get(Enchantment.MENDING));
        Assertions.assertEquals(1, plugin.pity.getFailures(player.getUniqueId()));
    }

    @Test
    @DisplayName("Pity system should do nothing when disabled")
    public void pitySystemShouldDoNothingWhenDisabled() {
        Player player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);

        plugin.getConfig().set("enchanting.probabilities.default", 6.0);
        plugin.getConfig().set("enchanting.pity.enabled", false);
        plugin.saveConfig();
        plugin.random.setRandomSupplier(() -> 85.0);

        EnchantItemEvent firstEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1);
        server.getPluginManager().callEvent(firstEvent);

        EnchantItemEvent secondEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1);
        server.getPluginManager().callEvent(secondEvent);

        Assertions.assertNull(firstEvent.getEnchantsToAdd().get(Enchantment.MENDING));
        Assertions.assertNull(secondEvent.getEnchantsToAdd().get(Enchantment.MENDING));
        Assertions.assertEquals(0, plugin.pity.getFailures(player.getUniqueId()));
    }

    @Test
    @DisplayName("Pity system should cap the accumulated bonus at max bonus")
    public void pitySystemShouldCapAtMaxBonus() {
        Player player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);

        plugin.getConfig().set("enchanting.probabilities.default", 6.0);
        plugin.getConfig().set("enchanting.pity.enabled", true);
        plugin.getConfig().set("enchanting.pity.bonus-per-failure", 10.0);
        plugin.getConfig().set("enchanting.pity.max-bonus", 30.0);
        plugin.saveConfig();
        plugin.random.setRandomSupplier(() -> 69.0);

        server.getPluginManager().callEvent(new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1));
        server.getPluginManager().callEvent(new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1));
        server.getPluginManager().callEvent(new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1));

        Assertions.assertEquals(30.0, plugin.pity.getBonus(player.getUniqueId()));

        EnchantItemEvent fourthEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1);
        server.getPluginManager().callEvent(fourthEvent);

        Assertions.assertNotNull(fourthEvent.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Pity system should track failures independently per player")
    public void pitySystemShouldTrackPlayersIndependently() {
        Player firstPlayer = server.addPlayer("first");
        Player secondPlayer = server.addPlayer("second");

        PermissionAttachment firstAttachment = firstPlayer.addAttachment(plugin);
        firstAttachment.setPermission("mendingenchant.use", true);

        PermissionAttachment secondAttachment = secondPlayer.addAttachment(plugin);
        secondAttachment.setPermission("mendingenchant.use", true);

        plugin.getConfig().set("enchanting.probabilities.default", 6.0);
        plugin.getConfig().set("enchanting.pity.enabled", true);
        plugin.getConfig().set("enchanting.pity.bonus-per-failure", 20.0);
        plugin.getConfig().set("enchanting.pity.max-bonus", 40.0);
        plugin.saveConfig();
        plugin.random.setRandomSupplier(() -> 85.0);

        server.getPluginManager().callEvent(new EnchantItemEvent(firstPlayer, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1));

        Assertions.assertEquals(1, plugin.pity.getFailures(firstPlayer.getUniqueId()));
        Assertions.assertEquals(0, plugin.pity.getFailures(secondPlayer.getUniqueId()));

        EnchantItemEvent secondPlayerEvent = new EnchantItemEvent(secondPlayer, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1);
        server.getPluginManager().callEvent(secondPlayerEvent);

        Assertions.assertNull(secondPlayerEvent.getEnchantsToAdd().get(Enchantment.MENDING));
        Assertions.assertEquals(1, plugin.pity.getFailures(secondPlayer.getUniqueId()));
    }

    @Test
    @DisplayName("Pity system state should reset on plugin reload")
    public void pitySystemStateShouldResetOnReload() {
        Player player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission("mendingenchant.use", true);

        plugin.getConfig().set("enchanting.probabilities.default", 6.0);
        plugin.getConfig().set("enchanting.pity.enabled", true);
        plugin.getConfig().set("enchanting.pity.bonus-per-failure", 20.0);
        plugin.getConfig().set("enchanting.pity.max-bonus", 40.0);
        plugin.saveConfig();
        plugin.random.setRandomSupplier(() -> 85.0);

        server.getPluginManager().callEvent(new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 30, new HashMap<>(), null, 10, 1));
        Assertions.assertEquals(1, plugin.pity.getFailures(player.getUniqueId()));

        plugin.reloadPluginConfiguration();

        Assertions.assertEquals(0, plugin.pity.getFailures(player.getUniqueId()));
    }
}
