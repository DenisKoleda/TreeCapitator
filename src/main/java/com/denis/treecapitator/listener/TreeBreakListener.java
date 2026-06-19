package com.denis.treecapitator.listener;

import com.denis.treecapitator.TreeCapitatorPlugin;
import com.denis.treecapitator.TreeSettings;
import com.denis.treecapitator.block.MaterialUtil;
import com.denis.treecapitator.breaking.TreeBreakTask;
import com.denis.treecapitator.detect.TreeDetection;
import com.denis.treecapitator.detect.TreeDetector;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class TreeBreakListener implements Listener {
    private final TreeCapitatorPlugin plugin;
    private final Set<UUID> choppingPlayers = new HashSet<>();
    private final ThreadLocal<Boolean> syntheticBreak = ThreadLocal.withInitial(() -> false);

    public TreeBreakListener(TreeCapitatorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (syntheticBreak.get()) {
            return;
        }

        TreeSettings settings = plugin.settings();
        Player player = event.getPlayer();
        if (!settings.enabled()) {
            return;
        }
        if (!player.hasPermission("treecapitator.use")) {
            return;
        }
        if (settings.sneakDisables() && player.isSneaking()) {
            return;
        }
        if (!MaterialUtil.isLog(event.getBlock().getType())) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();
        if (settings.requireAxe() && !isAxe(tool)) {
            return;
        }
        if (choppingPlayers.contains(player.getUniqueId())) {
            if (settings.warnAlreadyChopping()) {
                plugin.messages().send(player, "alreadyChopping");
            }
            event.setCancelled(true);
            return;
        }

        TreeDetection detection = new TreeDetector(settings).detect(event.getBlock());
        if (!detection.valid()) {
            if ("too-large".equals(detection.reason()) && settings.warnTooLarge()) {
                plugin.messages().send(player, "tooLarge");
            }
            return;
        }

        int durabilityCost = durabilityCost(settings, detection);
        if (TreeBreakTask.wouldBreakTool(tool, durabilityCost) && settings.abortIfToolWouldBreak()) {
            if (settings.warnToolWouldBreak()) {
                plugin.messages().send(player, "toolWouldBreak");
            }
            return;
        }

        event.setCancelled(true);
        choppingPlayers.add(player.getUniqueId());

        new TreeBreakTask(plugin, player, tool, event.getBlock(), detection, syntheticBreak, () -> {
            choppingPlayers.remove(player.getUniqueId());
            if (settings.showSuccess()) {
                plugin.messages().send(player, "success", Map.of(
                        "logs", Integer.toString(detection.logs().size()),
                        "leaves", Integer.toString(detection.leaves().size())
                ));
            }
        }).runTaskTimer(plugin, 1L, 1L);
    }

    private boolean isAxe(ItemStack tool) {
        if (tool == null) {
            return false;
        }
        Material material = tool.getType();
        return material != Material.AIR && MaterialUtil.isAxe(material);
    }

    private int durabilityCost(TreeSettings settings, TreeDetection detection) {
        if (!settings.durabilityEnabled()) {
            return 0;
        }
        if (settings.durabilityMode() == TreeSettings.DurabilityMode.ALL_BLOCKS) {
            return detection.logs().size() + detection.leaves().size();
        }
        return detection.logs().size();
    }
}
