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
import org.junit.jupiter.api.*;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

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

    @Test
    @DisplayName("Fishing probability should be set at 100% for the tests")
    public void checkFishingProbability(){
        double fishingProba = plugin.config.getDouble("FishingProbability");
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
         ItemStack is = null;           // Not what is tested here - so set to null
         int level = 100;               // Not what is tested here - so set to 10
         Enchantment hint = null;       // Not what is tested here - so set to null
         int levelHint = 10;            // Not what is tested here - so set to 10
         int i = 1;                     // Not what is tested here - so set to 1

         EnchantItemEvent e = new EnchantItemEvent(player, view, table, is, level, enchants, hint, levelHint, i);
         server.getPluginManager().callEvent(e);

         Assertions.assertNotNull(e.getEnchantsToAdd().get(Enchantment.MENDING));
     }
}
