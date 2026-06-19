package com.denis.treecapitator.command;

import com.denis.treecapitator.TreeCapitatorPlugin;
import com.denis.treecapitator.block.MaterialUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TreeCapitatorCommand implements CommandExecutor, TabCompleter {
    private final TreeCapitatorPlugin plugin;

    public TreeCapitatorCommand(TreeCapitatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 0) {
            return help(sender);
        }

        switch (args[0].toLowerCase()) {
            case "status" -> {
                return status(sender);
            }
            case "reload" -> {
                return reload(sender);
            }
            case "selftest" -> {
                return selftest(sender);
            }
            case "help" -> {
                return help(sender);
            }
            default -> {
                plugin.messages().send(sender, "usage");
                return true;
            }
        }
    }

    private boolean status(CommandSender sender) {
        sender.sendMessage(plugin.messages().raw("statusHeader"));
        sender.sendMessage(net.kyori.adventure.text.Component.text("enabled: " + plugin.settings().enabled()));
        sender.sendMessage(net.kyori.adventure.text.Component.text("maxLogs: " + plugin.settings().maxLogs()));
        sender.sendMessage(net.kyori.adventure.text.Component.text("maxLeaves: " + plugin.settings().maxLeaves()));
        sender.sendMessage(net.kyori.adventure.text.Component.text("logs/tick: " + plugin.settings().breakLogsPerTick()));
        sender.sendMessage(net.kyori.adventure.text.Component.text("leaves/tick: " + plugin.settings().breakLeavesPerTick()));
        sender.sendMessage(net.kyori.adventure.text.Component.text("showSuccess: " + plugin.settings().showSuccess()));
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!sender.hasPermission("treecapitator.reload")) {
            plugin.messages().send(sender, "noPermission");
            return true;
        }
        plugin.reloadTreeSettings();
        plugin.messages().send(sender, "reloaded");
        return true;
    }

    private boolean selftest(CommandSender sender) {
        if (!sender.hasPermission("treecapitator.selftest")) {
            plugin.messages().send(sender, "noPermission");
            return true;
        }
        boolean pass = MaterialUtil.isAxe(org.bukkit.Material.DIAMOND_AXE)
                && !MaterialUtil.isAxe(org.bukkit.Material.DIAMOND_PICKAXE)
                && "OAK".equals(MaterialUtil.woodFamily(org.bukkit.Material.STRIPPED_OAK_WOOD))
                && plugin.settings().maxLogs() > 0
                && plugin.settings().breakLogsPerTick() > 0
                && plugin.settings().breakLeavesPerTick() > 0;
        plugin.messages().send(sender, pass ? "selftestPass" : "selftestFail");
        return true;
    }

    private boolean help(CommandSender sender) {
        sender.sendMessage(plugin.messages().raw("helpHeader"));
        sender.sendMessage(net.kyori.adventure.text.Component.text("/treecapitator status"));
        sender.sendMessage(net.kyori.adventure.text.Component.text("/treecapitator reload"));
        sender.sendMessage(net.kyori.adventure.text.Component.text("/treecapitator selftest"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (args.length != 1) {
            return List.of();
        }
        List<String> options = new ArrayList<>();
        options.add("status");
        options.add("help");
        if (sender.hasPermission("treecapitator.reload")) {
            options.add("reload");
        }
        if (sender.hasPermission("treecapitator.selftest")) {
            options.add("selftest");
        }
        String prefix = args[0].toLowerCase();
        return options.stream().filter(option -> option.startsWith(prefix)).toList();
    }
}
