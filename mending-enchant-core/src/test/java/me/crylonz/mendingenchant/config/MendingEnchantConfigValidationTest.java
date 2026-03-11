package me.crylonz.mendingenchant.config;

import me.crylonz.mendingenchant.support.MendingEnchantTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class MendingEnchantConfigValidationTest extends MendingEnchantTestBase {

    @Test
    @DisplayName("Reload should sanitize invalid filter configuration")
    public void reloadShouldSanitizeInvalidConfiguration() {
        server.addSimpleWorld("valid_world");

        plugin.getConfig().set("enchanting.item-filter.mode", "invalid-mode");
        plugin.getConfig().set("enchanting.item-filter.materials", Arrays.asList("diamond_pickaxe", "NOT_A_MATERIAL", "DIAMOND_PICKAXE"));
        plugin.getConfig().set("world-filter.mode", "wrong");
        plugin.getConfig().set("world-filter.worlds", Arrays.asList("valid_world", "missing_world", "valid_world"));
        plugin.saveConfig();

        plugin.reloadPluginConfiguration();

        Assertions.assertEquals("disabled", plugin.config.getString("enchanting.item-filter.mode"));
        Assertions.assertEquals(Collections.singletonList("DIAMOND_PICKAXE"), plugin.config.getStringList("enchanting.item-filter.materials"));
        Assertions.assertEquals("disabled", plugin.config.getString("world-filter.mode"));
        Assertions.assertEquals(Collections.singletonList("valid_world"), plugin.config.getStringList("world-filter.worlds"));
    }
}
