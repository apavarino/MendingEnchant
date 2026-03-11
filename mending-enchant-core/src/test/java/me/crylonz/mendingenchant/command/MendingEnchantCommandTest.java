package me.crylonz.mendingenchant.command;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.crylonz.mendingenchant.support.MendingEnchantTestBase;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class MendingEnchantCommandTest extends MendingEnchantTestBase {

    @Test
    @DisplayName("Reload command should reload the configuration for admins")
    public void reloadCommandShouldReloadConfig() {
        PlayerMock player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission(MendingEnchantCommandHandler.RELOAD_PERMISSION, true);

        plugin.getConfig().set("fishing.probability", 25.0);
        plugin.saveConfig();
        plugin.getConfig().set("fishing.probability", 100.0);

        PluginCommand command = createPluginCommand("mendingenchant");
        boolean handled = plugin.onCommand(player, command, "mendingenchant", new String[]{"reload"});

        Assertions.assertTrue(handled);
        Assertions.assertEquals(25.0, plugin.config.getDouble("fishing.probability"));
    }

    @Test
    @DisplayName("Info command should show the active configuration summary")
    public void infoCommandShouldShowConfigurationSummary() {
        PlayerMock player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission(MendingEnchantCommandHandler.INFO_PERMISSION, true);

        plugin.getConfig().set("enchanting.item-filter.mode", "blacklist");
        plugin.getConfig().set("enchanting.item-filter.materials", Collections.singletonList("BOW"));
        plugin.getConfig().set("world-filter.mode", "whitelist");
        plugin.getConfig().set("world-filter.worlds", Collections.singletonList("world"));
        plugin.getConfig().set("fishing.probability", 42.0);
        plugin.saveConfig();

        PluginCommand command = createPluginCommand("mendingenchant");
        boolean handled = plugin.onCommand(player, command, "mendingenchant", new String[]{"info"});

        Assertions.assertTrue(handled);
        Assertions.assertTrue(player.nextMessage().contains("MendingEnchant"));
        Assertions.assertTrue(player.nextMessage().contains("Updater:"));
        Assertions.assertTrue(player.nextMessage().contains("Enchant probabilities:"));
        Assertions.assertTrue(player.nextMessage().contains("Fishing probability:"));
        Assertions.assertTrue(player.nextMessage().contains("blacklist"));
        Assertions.assertTrue(player.nextMessage().contains("whitelist"));
    }

    @Test
    @DisplayName("Tab completion should suggest available subcommands")
    public void tabCompletionShouldSuggestSubcommands() {
        PlayerMock player = server.addPlayer();
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission(MendingEnchantCommandHandler.RELOAD_PERMISSION, true);
        attachment.setPermission(MendingEnchantCommandHandler.INFO_PERMISSION, true);

        PluginCommand command = createPluginCommand("mendingenchant");
        List<String> completions = plugin.onTabComplete(player, command, "mendingenchant", new String[]{""});

        Assertions.assertTrue(completions.contains("reload"));
        Assertions.assertTrue(completions.contains("info"));
    }
}
