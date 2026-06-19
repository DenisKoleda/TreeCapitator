package com.denis.treecapitator.detect;

import com.denis.treecapitator.TreeSettings;
import com.denis.treecapitator.block.BlockKey;
import com.denis.treecapitator.block.MaterialUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;

public final class TreeDetector {
    private final TreeSettings settings;

    public TreeDetector(TreeSettings settings) {
        this.settings = settings;
    }

    public TreeDetection detect(Block start) {
        Material startMaterial = start.getType();
        if (!MaterialUtil.isLog(startMaterial)) {
            return TreeDetection.invalid("not-log");
        }

        String family = MaterialUtil.woodFamily(startMaterial);
        List<Block> logs = collectLogs(start, family);
        if (logs.isEmpty()) {
            return TreeDetection.invalid("not-tree");
        }
        if (logs.size() > settings.maxLogs()) {
            return TreeDetection.invalid("too-large");
        }

        List<Block> leaves = collectLeaves(logs);
        boolean netherStem = MaterialUtil.isNetherStemFamily(family);
        if (!netherStem && leaves.size() < settings.minNearbyLeaves()) {
            return TreeDetection.invalid("not-tree");
        }
        if (leaves.size() > settings.maxLeaves()) {
            return TreeDetection.invalid("too-large");
        }

        logs.sort(Comparator
                .comparingInt(Block::getY)
                .thenComparingInt(Block::getX)
                .thenComparingInt(Block::getZ));
        leaves.sort(Comparator
                .comparingInt(Block::getY).reversed()
                .thenComparingInt(Block::getX)
                .thenComparingInt(Block::getZ));

        return new TreeDetection(List.copyOf(logs), List.copyOf(leaves), true, "ok");
    }

    private List<Block> collectLogs(Block start, String family) {
        ArrayDeque<Block> queue = new ArrayDeque<>();
        Set<BlockKey> visited = new HashSet<>();
        List<Block> logs = new ArrayList<>();

        queue.add(start);
        visited.add(BlockKey.of(start));

        while (!queue.isEmpty()) {
            Block current = queue.removeFirst();
            if (!isMatchingLog(current, family)) {
                continue;
            }
            logs.add(current);
            if (logs.size() > settings.maxLogs()) {
                return logs;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        Block next = current.getRelative(dx, dy, dz);
                        if (!withinBounds(start, next)) {
                            continue;
                        }
                        BlockKey key = BlockKey.of(next);
                        if (visited.add(key) && isMatchingLog(next, family)) {
                            queue.addLast(next);
                        }
                    }
                }
            }
        }

        return logs;
    }

    private boolean isMatchingLog(Block block, String family) {
        Material material = block.getType();
        if (!MaterialUtil.isLog(material)) {
            return false;
        }
        return !settings.sameWoodFamily() || MaterialUtil.woodFamily(material).equals(family);
    }

    private boolean withinBounds(Block start, Block block) {
        int horizontal = settings.maxHorizontalDistance();
        int vertical = settings.maxVerticalDistance();
        return Math.abs(block.getX() - start.getX()) <= horizontal
                && Math.abs(block.getZ() - start.getZ()) <= horizontal
                && Math.abs(block.getY() - start.getY()) <= vertical;
    }

    private List<Block> collectLeaves(List<Block> logs) {
        Set<BlockKey> seen = new HashSet<>();
        List<Block> leaves = new ArrayList<>();
        int radius = settings.leafRadius();
        int radiusSquared = radius * radius;

        for (Block log : logs) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if ((dx * dx) + (dy * dy) + (dz * dz) > radiusSquared) {
                            continue;
                        }
                        Block block = log.getRelative(dx, dy, dz);
                        if (!MaterialUtil.isLeaf(block.getType())) {
                            continue;
                        }
                        if (settings.onlyNaturalLeaves() && isPersistentLeaf(block)) {
                            continue;
                        }
                        if (seen.add(BlockKey.of(block))) {
                            leaves.add(block);
                            if (leaves.size() > settings.maxLeaves()) {
                                return leaves;
                            }
                        }
                    }
                }
            }
        }

        return leaves;
    }

    private boolean isPersistentLeaf(Block block) {
        BlockData data = block.getBlockData();
        return data instanceof Leaves leaves && leaves.isPersistent();
    }
}
