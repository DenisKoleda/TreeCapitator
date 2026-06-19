package com.denis.treecapitator.detect;

import java.util.List;
import org.bukkit.block.Block;

public record TreeDetection(List<Block> logs, List<Block> leaves, boolean valid, String reason) {
    public static TreeDetection invalid(String reason) {
        return new TreeDetection(List.of(), List.of(), false, reason);
    }
}
