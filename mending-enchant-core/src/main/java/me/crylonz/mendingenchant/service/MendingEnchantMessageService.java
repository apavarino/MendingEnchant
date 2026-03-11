package me.crylonz.mendingenchant.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MendingEnchantMessageService {
    private static final String DEFAULT_LOCALE = "en_US";

    private final Plugin plugin;
    private JSONObject messages;

    public MendingEnchantMessageService(Plugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        messages = loadLocale(resolveLocale());
        if (messages == null) {
            plugin.getLogger().warning("Could not load locale '" + resolveLocale() + "'. Falling back to '" + DEFAULT_LOCALE + "'.");
            messages = loadLocale(DEFAULT_LOCALE);
        }
    }

    public void send(CommandSender sender, String path) {
        send(sender, path, new String[0]);
    }

    public void send(CommandSender sender, String path, String... arguments) {
        String message = get(path, arguments);
        if (message == null || message.isEmpty()) {
            return;
        }

        sender.sendMessage(message);
    }

    public String get(String path, String... arguments) {
        if (messages == null) {
            reload();
        }

        String message = resolveMessage(path);
        if (message == null) {
            return "";
        }

        for (int index = 0; index < arguments.length; index++) {
            message = message.replace("{" + index + "}", arguments[index]);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMendingObtained(Player player, String path, String itemName) {
        send(player, path, itemName);
    }

    private String resolveLocale() {
        return plugin.getConfig().getString("localization.locale", DEFAULT_LOCALE);
    }

    private JSONObject loadLocale(String locale) {
        File localizationFolder = new File(plugin.getDataFolder(), "localization");
        if (!localizationFolder.exists() && !localizationFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create localization folder at " + localizationFolder.getAbsolutePath());
        }

        String resourcePath = "localization/" + locale + ".json";
        File localeFile = new File(localizationFolder, locale + ".json");
        if (!localeFile.exists()) {
            try {
                plugin.saveResource(resourcePath, false);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        try (FileReader reader = new FileReader(localeFile)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (IOException | ParseException | ClassCastException exception) {
            plugin.getLogger().warning("Could not read locale file '" + localeFile.getName() + "': " + exception.getMessage());
            return null;
        }
    }

    private String resolveMessage(String path) {
        Object value = messages.get(path);
        return value instanceof String ? (String) value : null;
    }
}
