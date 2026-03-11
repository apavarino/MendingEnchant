package me.crylonz.mendingenchant.localization;

import me.crylonz.mendingenchant.support.MendingEnchantTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MendingEnchantLocalizationTest extends MendingEnchantTestBase {

    @Test
    @DisplayName("Missing locale should fall back to en_US")
    public void missingLocaleShouldFallBackToDefault() {
        plugin.getConfig().set("localization.locale", "zz_ZZ");
        plugin.saveConfig();

        plugin.reloadPluginConfiguration();

        String message = plugin.messages.get("commands.no-permission");
        Assertions.assertTrue(message.contains("do not have permission"));
    }

    @Test
    @DisplayName("Localization should replace positional arguments")
    public void localizationShouldReplaceArguments() {
        String message = plugin.messages.get("commands.usage", "mendingenchant");
        Assertions.assertTrue(message.contains("/mendingenchant <reload|info>"));
    }
}
