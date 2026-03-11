package me.crylonz.mendingenchant;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.FishHookMock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.*;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.reflect.Constructor;

public class MendingEnchantTest {
    private static ServerMock server;
    private static MendingEnchant plugin;

    @BeforeAll
    public static void setup() {
        MendingEnchant.allowMetrics = false;
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MendingEnchant.class);
    }

    @AfterAll
    public static void cleanup() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void resetConfig() {
        plugin.getConfig().set("enchanting.item-filter.mode", "disabled");
        plugin.getConfig().set("enchanting.item-filter.materials", Collections.emptyList());
        plugin.getConfig().set("fishing.probability", 100.0);
        plugin.saveConfig();
    }

    @Test
    @DisplayName("Fishing probability should be set at 100% for the tests")
    public void checkFishingProbability(){
        double fishingProba = plugin.config.getDouble("fishing.probability");
        Assertions.assertEquals(fishingProba, 100.0);
    }

    @Test
    @DisplayName("Fishing should give mending book")
    public void checkFishingResults() {
        Player player = server.addPlayer();
        FishHook fishHook = new FishHookMock(server, new UUID(1,2));
        server.getPluginManager().callEvent(new PlayerFishEvent(player, null, fishHook, PlayerFishEvent.State.CAUGHT_FISH));

        ItemStack item = player.getInventory().getItem(0);

        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getType(), Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta esm = (EnchantmentStorageMeta) item.getItemMeta();
        Assertions.assertTrue(esm.hasStoredEnchant(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Enchanting should give the mending enchant to the tool")
    public void enchantingForMending(){
     Player player = server.addPlayer();
     PermissionAttachment pa = player.addAttachment(plugin);
     pa.setPermission("mendingenchant.use", true);

     Map<Enchantment, Integer> enchants = new HashMap<>(); // Can't be null

     InventoryView view = null;     // Not what is tested here - so set to null
     Block table = null;            // Not what is tested here - so set to null
     ItemStack is = new ItemStack(Material.DIAMOND_PICKAXE);
     int level = 100;               // Not what is tested here - so set to 10
     Enchantment hint = null;       // Not what is tested here - so set to null
     int levelHint = 10;            // Not what is tested here - so set to 10
     int i = 1;                     // Not what is tested here - so set to 1

     EnchantItemEvent e = new EnchantItemEvent(player, view, table, is, level, enchants, hint, levelHint, i);
     server.getPluginManager().callEvent(e);

     Assertions.assertNotNull(e.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Mending should not be in the enchant list if there is already Infinity")
    public void noMendingIfInfinity() {
        Player player = server.addPlayer();
        PermissionAttachment pa = player.addAttachment(plugin);
        pa.setPermission("mendingenchant.use", true);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.ARROW_INFINITE, 1);

        InventoryView view = null;     // Not what is tested here - so set to null
        Block table = null;            // Not what is tested here - so set to null
        ItemStack is = new ItemStack(Material.BOW);
        int level = 100;               // Not what is tested here - so set to 10
        Enchantment hint = null;       // Not what is tested here - so set to null
        int levelHint = 10;            // Not what is tested here - so set to 10
        int i = 1;                     // Not what is tested here - so set to 1

        EnchantItemEvent e = new EnchantItemEvent(player, view, table, is, level, enchants, hint, levelHint, i);
        server.getPluginManager().callEvent(e);

        Assertions.assertNull(e.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Mending should not be added to blacklisted items")
    public void noMendingForBlacklistedItem() {
        plugin.getConfig().set("enchanting.item-filter.mode", "blacklist");
        plugin.getConfig().set("enchanting.item-filter.materials", Collections.singletonList("DIAMOND_PICKAXE"));
        plugin.saveConfig();

        Player player = server.addPlayer();
        PermissionAttachment pa = player.addAttachment(plugin);
        pa.setPermission("mendingenchant.use", true);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        EnchantItemEvent e = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 100, enchants, null, 10, 1);
        server.getPluginManager().callEvent(e);

        Assertions.assertNull(e.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Mending should only be added to whitelisted items")
    public void mendingOnlyForWhitelistedItem() {
        plugin.getConfig().set("enchanting.item-filter.mode", "whitelist");
        plugin.getConfig().set("enchanting.item-filter.materials", Collections.singletonList("DIAMOND_PICKAXE"));
        plugin.saveConfig();

        Player player = server.addPlayer();
        PermissionAttachment pa = player.addAttachment(plugin);
        pa.setPermission("mendingenchant.use", true);

        Map<Enchantment, Integer> pickaxeEnchants = new HashMap<>();
        EnchantItemEvent allowedEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_PICKAXE), 100, pickaxeEnchants, null, 10, 1);
        server.getPluginManager().callEvent(allowedEvent);

        Map<Enchantment, Integer> swordEnchants = new HashMap<>();
        EnchantItemEvent blockedEvent = new EnchantItemEvent(player, null, null, new ItemStack(Material.DIAMOND_SWORD), 100, swordEnchants, null, 10, 1);
        server.getPluginManager().callEvent(blockedEvent);

        Assertions.assertNotNull(allowedEvent.getEnchantsToAdd().get(Enchantment.MENDING));
        Assertions.assertNull(blockedEvent.getEnchantsToAdd().get(Enchantment.MENDING));
    }

    @Test
    @DisplayName("Reload command should reload the configuration for admins")
    public void reloadCommandShouldReloadConfig() {
        Player player = server.addPlayer();
        PermissionAttachment pa = player.addAttachment(plugin);
        pa.setPermission("mendingenchant.admin.reload", true);

        plugin.getConfig().set("fishing.probability", 25.0);
        plugin.saveConfig();

        plugin.getConfig().set("fishing.probability", 100.0);

        PluginCommand command = createPluginCommand("mendingenchant");
        boolean handled = plugin.onCommand(player, command, "mendingenchant", new String[]{"reload"});

        Assertions.assertTrue(handled);
        Assertions.assertEquals(25.0, plugin.config.getDouble("fishing.probability"));
    }

    private PluginCommand createPluginCommand(String name) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
