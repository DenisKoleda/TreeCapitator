package com.denis.treecapitator.breaking;

import com.denis.treecapitator.TreeCapitatorPlugin;
import com.denis.treecapitator.TreeSettings;
import com.denis.treecapitator.block.BlockKey;
import com.denis.treecapitator.block.MaterialUtil;
import com.denis.treecapitator.detect.TreeDetection;
import java.util.ArrayDeque;
import java.util.Queue;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public final class TreeBreakTask extends BukkitRunnable {
    private final TreeCapitatorPlugin plugin;
    private final Player player;
    private final ItemStack tool;
    private final BlockKey originalBlock;
    private final Queue<Block> logs;
    private final Queue<Block> leaves;
    private final ThreadLocal<Boolean> syntheticBreak;
    private final Runnable onFinish;
    private int durabilitySpent;

    public TreeBreakTask(
            TreeCapitatorPlugin plugin,
            Player player,
            ItemStack tool,
            Block originalBlock,
            TreeDetection detection,
            ThreadLocal<Boolean> syntheticBreak,
            Runnable onFinish
    ) {
        this.plugin = plugin;
        this.player = player;
        this.tool = tool;
        this.originalBlock = BlockKey.of(originalBlock);
        this.logs = new ArrayDeque<>(detection.logs());
        this.leaves = new ArrayDeque<>(detection.leaves());
        this.syntheticBreak = syntheticBreak;
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            finish();
            return;
        }

        TreeSettings settings = plugin.settings();
        int logBudget = settings.breakLogsPerTick();
        while (logBudget-- > 0 && !logs.isEmpty()) {
            Block block = logs.remove();
            if (MaterialUtil.isLog(block.getType()) && breakBlock(block, false)) {
                spendDurability(1);
            }
        }

        int leafBudget = settings.breakLeavesPerTick();
        while (leafBudget-- > 0 && logs.isEmpty() && !leaves.isEmpty()) {
            Block block = leaves.remove();
            if (MaterialUtil.isLeaf(block.getType()) && breakBlock(block, true)
                    && settings.durabilityMode() == TreeSettings.DurabilityMode.ALL_BLOCKS) {
                spendDurability(1);
            }
        }

        if (logs.isEmpty() && leaves.isEmpty()) {
            finish();
        }
    }

    private boolean breakBlock(Block block, boolean leaf) {
        TreeSettings settings = plugin.settings();
        if (!leaf && !MaterialUtil.isLog(block.getType())) {
            return false;
        }
        if (leaf && !MaterialUtil.isLeaf(block.getType())) {
            return false;
        }

        if (settings.respectProtectionEvents() && !BlockKey.of(block).equals(originalBlock)) {
            BlockBreakEvent syntheticEvent = new BlockBreakEvent(block, player);
            syntheticBreak.set(true);
            try {
                plugin.getServer().getPluginManager().callEvent(syntheticEvent);
            } finally {
                syntheticBreak.set(false);
            }
            if (syntheticEvent.isCancelled()) {
                return false;
            }
        }

        return block.breakNaturally(tool);
    }

    private void spendDurability(int amount) {
        if (!plugin.settings().durabilityEnabled() || amount <= 0) {
            return;
        }
        if (tool == null || tool.getType() == Material.AIR || tool.getType().getMaxDurability() <= 0) {
            return;
        }
        durabilitySpent += amount;
        damageTool(tool, amount);
    }

    private void finish() {
        cancel();
        onFinish.run();
    }

    public static boolean wouldBreakTool(ItemStack tool, int durabilityCost) {
        if (durabilityCost <= 0 || tool == null || tool.getType() == Material.AIR) {
            return false;
        }
        int maxDurability = tool.getType().getMaxDurability();
        if (maxDurability <= 0) {
            return false;
        }
        ItemMeta meta = tool.getItemMeta();
        int currentDamage = meta instanceof Damageable damageable ? damageable.getDamage() : 0;
        return currentDamage + durabilityCost >= maxDurability;
    }

    private static void damageTool(ItemStack tool, int amount) {
        ItemMeta meta = tool.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return;
        }

        int maxDurability = tool.getType().getMaxDurability();
        int newDamage = damageable.getDamage() + amount;
        if (newDamage >= maxDurability) {
            int newAmount = tool.getAmount() - 1;
            if (newAmount <= 0) {
                tool.setAmount(0);
                return;
            }
            tool.setAmount(newAmount);
            damageable.setDamage(0);
            tool.setItemMeta(meta);
            return;
        }

        damageable.setDamage(newDamage);
        tool.setItemMeta(meta);
    }
}
