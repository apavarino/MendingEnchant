package me.crylonz.mendingenchant.support;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.crylonz.mendingenchant.MendingEnchant;
import org.bukkit.command.PluginCommand;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Constructor;
import java.util.Collections;

public abstract class MendingEnchantTestBase {
    protected static ServerMock server;
    protected static MendingEnchant plugin;

    @BeforeAll
    public static void setupPlugin() {
        MendingEnchant.allowMetrics = false;
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MendingEnchant.class);
    }

    @AfterAll
    public static void cleanupPlugin() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void resetConfig() {
        plugin.getConfig().set("enchanting.item-filter.mode", "disabled");
        plugin.getConfig().set("enchanting.item-filter.materials", Collections.emptyList());
        plugin.getConfig().set("enchanting.pity.enabled", false);
        plugin.getConfig().set("enchanting.pity.bonus-per-failure", 2.0);
        plugin.getConfig().set("enchanting.pity.max-bonus", 30.0);
        plugin.getConfig().set("world-filter.mode", "disabled");
        plugin.getConfig().set("world-filter.worlds", Collections.emptyList());
        plugin.getConfig().set("fishing.probability", 100.0);
        plugin.saveConfig();
        plugin.pity.reset();
        plugin.random.reset();
    }

    protected PluginCommand createPluginCommand(String name) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
