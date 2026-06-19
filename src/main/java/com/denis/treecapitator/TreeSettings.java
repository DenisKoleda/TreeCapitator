package com.denis.treecapitator;

import org.bukkit.configuration.file.FileConfiguration;

public record TreeSettings(
        boolean enabled,
        boolean requireAxe,
        boolean sneakDisables,
        boolean sameWoodFamily,
        int maxLogs,
        int maxLeaves,
        int maxHorizontalDistance,
        int maxVerticalDistance,
        int minNearbyLeaves,
        int leafRadius,
        boolean onlyNaturalLeaves,
        int breakLogsPerTick,
        int breakLeavesPerTick,
        boolean respectProtectionEvents,
        boolean durabilityEnabled,
        DurabilityMode durabilityMode,
        boolean abortIfToolWouldBreak,
        boolean showSuccess,
        boolean warnTooLarge,
        boolean warnToolWouldBreak,
        boolean warnAlreadyChopping
) {
    public enum DurabilityMode {
        LOGS_ONLY,
        ALL_BLOCKS
    }

    public static TreeSettings fromConfig(TreeCapitatorPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        return new TreeSettings(
                config.getBoolean("enabled", true),
                config.getBoolean("require-axe", true),
                config.getBoolean("sneak-disables", true),
                config.getBoolean("same-wood-family", true),
                Math.max(1, config.getInt("max-logs", 384)),
                Math.max(0, config.getInt("max-leaves", 1024)),
                Math.max(1, config.getInt("max-horizontal-distance", 10)),
                Math.max(1, config.getInt("max-vertical-distance", 40)),
                Math.max(0, config.getInt("min-nearby-leaves", 4)),
                Math.max(1, config.getInt("leaf-radius", 5)),
                config.getBoolean("only-natural-leaves", true),
                Math.max(1, config.getInt("break-logs-per-tick", 14)),
                Math.max(1, config.getInt("break-leaves-per-tick", 36)),
                config.getBoolean("respect-protection-events", true),
                config.getBoolean("durability.enabled", true),
                parseDurabilityMode(config.getString("durability.mode", "LOGS_ONLY")),
                config.getBoolean("durability.abort-if-tool-would-break", true),
                config.getBoolean("feedback.show-success", false),
                config.getBoolean("feedback.warn-too-large", true),
                config.getBoolean("feedback.warn-tool-would-break", true),
                config.getBoolean("feedback.warn-already-chopping", true)
        );
    }

    private static DurabilityMode parseDurabilityMode(String value) {
        if (value == null) {
            return DurabilityMode.LOGS_ONLY;
        }
        try {
            return DurabilityMode.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return DurabilityMode.LOGS_ONLY;
        }
    }
}
