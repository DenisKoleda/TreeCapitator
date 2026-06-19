package com.denis.treecapitator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.denis.treecapitator.block.MaterialUtil;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

final class MaterialUtilTest {
    @Test
    void detectsAxes() {
        assertTrue(MaterialUtil.isAxe(Material.DIAMOND_AXE));
        assertFalse(MaterialUtil.isAxe(Material.DIAMOND_PICKAXE));
    }

    @Test
    void normalizesWoodFamilies() {
        assertEquals("OAK", MaterialUtil.woodFamily(Material.OAK_LOG));
        assertEquals("OAK", MaterialUtil.woodFamily(Material.STRIPPED_OAK_WOOD));
        assertEquals("CRIMSON", MaterialUtil.woodFamily(Material.CRIMSON_STEM));
        assertTrue(MaterialUtil.isNetherStemFamily("WARPED"));
    }
}
