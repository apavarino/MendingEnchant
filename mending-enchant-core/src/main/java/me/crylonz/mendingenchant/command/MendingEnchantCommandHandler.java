package me.crylonz.mendingenchant.command;

import me.crylonz.mendingenchant.MendingEnchant;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MendingEnchantCommandHandler implements TabCompleter {
    public static final String RELOAD_PERMISSION = "mendingenchant.admin.reload";
    public static final String INFO_PERMISSION = "mendingenchant.admin.info";

    private final MendingEnchant plugin;

    public MendingEnchantCommandHandler(MendingEnchant plugin) {
        this.plugin = plugin;
    }

    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mendingenchant")) {
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(RELOAD_PERMISSION)) {
                plugin.messages.send(sender, "commands.no-permission");
                return true;
            }

            plugin.reloadPluginConfiguration();
            plugin.messages.send(sender, "commands.reload-success");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission(INFO_PERMISSION)) {
                plugin.messages.send(sender, "commands.no-permission");
                return true;
            }

            sendInfo(sender);
            return true;
        }

        plugin.messages.send(sender, "commands.usage", label);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("mendingenchant") || args.length != 1) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();
        if (sender.hasPermission(RELOAD_PERMISSION)) {
            completions.add("reload");
        }
        if (sender.hasPermission(INFO_PERMISSION)) {
            completions.add("info");
        }

        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> filtered = new ArrayList<>();
        for (String completion : completions) {
            if (completion.startsWith(prefix)) {
                filtered.add(completion);
            }
        }
        return filtered;
    }

    private void sendInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "MendingEnchant " + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Updater: " + ChatColor.WHITE + plugin.config.getBoolean("updater.enabled"));
        sender.sendMessage(ChatColor.YELLOW + "Enchant probabilities: " + ChatColor.WHITE
                + "default=" + plugin.config.getDouble("enchanting.probabilities.default")
                + ", custom1=" + plugin.config.getDouble("enchanting.probabilities.custom-permission-1")
                + ", custom2=" + plugin.config.getDouble("enchanting.probabilities.custom-permission-2")
                + ", custom3=" + plugin.config.getDouble("enchanting.probabilities.custom-permission-3"));
        sender.sendMessage(ChatColor.YELLOW + "Fishing probability: " + ChatColor.WHITE + plugin.config.getDouble("fishing.probability"));
        sender.sendMessage(ChatColor.YELLOW + "Item filter: " + ChatColor.WHITE
                + plugin.config.getString("enchanting.item-filter.mode")
                + " " + formatList(plugin.config.getStringList("enchanting.item-filter.materials")));
        sender.sendMessage(ChatColor.YELLOW + "World filter: " + ChatColor.WHITE
                + plugin.config.getString("world-filter.mode")
                + " " + formatList(plugin.config.getStringList("world-filter.worlds")));
    }

    private String formatList(List<String> values) {
        return values.isEmpty() ? "(none)" : values.toString();
    }
}
