package com.denis.treecapitator.message;

import java.io.File;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MessageService {
    private static final Map<String, String> FALLBACKS = Map.ofEntries(
            Map.entry("prefix", "<gray>[<gold>TreeCapitator</gold>]</gray> "),
            Map.entry("noPermission", "<red>Нет прав."),
            Map.entry("reloaded", "<green>TreeCapitator перезагружен."),
            Map.entry("statusHeader", "<gold>TreeCapitator status"),
            Map.entry("helpHeader", "<gold>Команды TreeCapitator"),
            Map.entry("usage", "<yellow>Использование: <white>/treecapitator status|reload|selftest|help"),
            Map.entry("tooLarge", "<red>Дерево слишком большое для безопасной рубки."),
            Map.entry("alreadyChopping", "<yellow>Ты уже рубишь дерево."),
            Map.entry("toolWouldBreak", "<red>Топор сломается до конца рубки."),
            Map.entry("success", "<green>Срублено: <white>%logs%</white> logs, <white>%leaves%</white> leaves."),
            Map.entry("selftestPass", "<green>TreeCapitator selftest: PASS"),
            Map.entry("selftestFail", "<red>TreeCapitator selftest: FAIL")
    );

    private final JavaPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private FileConfiguration messages;

    public MessageService(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = plugin.getDataFolder().toPath().resolve("messages.yml").toFile();
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, Map.of());
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(prefixed(key, placeholders));
    }

    public Component prefixed(String key, Map<String, String> placeholders) {
        return miniMessage.deserialize(applyPlaceholders(value("prefix") + value(key), placeholders));
    }

    public Component raw(String key) {
        return miniMessage.deserialize(value(key));
    }

    public String plain(String key) {
        return value(key);
    }

    private String value(String key) {
        String fallback = FALLBACKS.getOrDefault(key, "");
        return messages.getString(key, fallback);
    }

    private String applyPlaceholders(String text, Map<String, String> placeholders) {
        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return result;
    }
}
