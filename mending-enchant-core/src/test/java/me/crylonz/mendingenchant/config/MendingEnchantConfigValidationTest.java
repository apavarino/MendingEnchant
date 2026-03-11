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

    @Test
    @DisplayName("Reload should sanitize invalid pity and probability values")
    public void reloadShouldSanitizePityAndProbabilities() {
        plugin.getConfig().set("localization.locale", "");
        plugin.getConfig().set("fishing.probability", 150.0);
        plugin.getConfig().set("enchanting.probabilities.default", -10.0);
        plugin.getConfig().set("enchanting.pity.bonus-per-failure", -5.0);
        plugin.getConfig().set("enchanting.pity.max-bonus", 250.0);
        plugin.saveConfig();

        plugin.reloadPluginConfiguration();

        Assertions.assertEquals("en_US", plugin.config.getString("localization.locale"));
        Assertions.assertEquals(100.0, plugin.config.getDouble("fishing.probability"));
        Assertions.assertEquals(0.0, plugin.config.getDouble("enchanting.probabilities.default"));
        Assertions.assertEquals(0.0, plugin.config.getDouble("enchanting.pity.bonus-per-failure"));
        Assertions.assertEquals(100.0, plugin.config.getDouble("enchanting.pity.max-bonus"));
    }
}
