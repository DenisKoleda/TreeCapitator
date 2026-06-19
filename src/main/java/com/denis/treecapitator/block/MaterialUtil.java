package com.denis.treecapitator.block;

import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.Tag;

public final class MaterialUtil {
    private MaterialUtil() {
    }

    public static boolean isLog(Material material) {
        return Tag.LOGS.isTagged(material);
    }

    public static boolean isLeaf(Material material) {
        return Tag.LEAVES.isTagged(material);
    }

    public static boolean isAxe(Material material) {
        return material.name().endsWith("_AXE");
    }

    public static boolean isNetherStemFamily(String family) {
        return "CRIMSON".equals(family) || "WARPED".equals(family);
    }

    public static String woodFamily(Material material) {
        String name = material.name().toUpperCase(Locale.ROOT);
        if (name.startsWith("STRIPPED_")) {
            name = name.substring("STRIPPED_".length());
        }
        for (String suffix : new String[]{"_LOG", "_WOOD", "_STEM", "_HYPHAE"}) {
            if (name.endsWith(suffix)) {
                return name.substring(0, name.length() - suffix.length());
            }
        }
        return name;
    }
}
