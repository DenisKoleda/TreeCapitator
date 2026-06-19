package com.denis.treecapitator.block;

import org.bukkit.block.Block;

public record BlockKey(int x, int y, int z) {
    public static BlockKey of(Block block) {
        return new BlockKey(block.getX(), block.getY(), block.getZ());
    }
}
