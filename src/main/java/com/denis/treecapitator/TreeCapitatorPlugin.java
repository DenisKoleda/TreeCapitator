package com.denis.treecapitator;

import com.denis.treecapitator.command.TreeCapitatorCommand;
import com.denis.treecapitator.listener.TreeBreakListener;
import com.denis.treecapitator.message.MessageService;
import java.io.File;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class TreeCapitatorPlugin extends JavaPlugin {
    private TreeSettings treeSettings;
    private MessageService messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        File messagesFile = getDataFolder().toPath().resolve("messages.yml").toFile();
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messages = new MessageService(this);
        reloadTreeSettings();

        TreeBreakListener listener = new TreeBreakListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

        PluginCommand command = getCommand("treecapitator");
        if (command != null) {
            TreeCapitatorCommand executor = new TreeCapitatorCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        getLogger().info("TreeCapitator enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TreeCapitator disabled.");
    }

    public TreeSettings settings() {
        return treeSettings;
    }

    public void reloadTreeSettings() {
        reloadConfig();
        messages.reload();
        treeSettings = TreeSettings.fromConfig(this);
    }

    public MessageService messages() {
        return messages;
    }
}
