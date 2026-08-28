/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.diebuddies.physics.vines;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigVines;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.VineSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class VineHelper {
    public static volatile BlockPos playerPos = new BlockPos(0, 0, 0);
    private static Map<Block, DynamicSetting> dynamicSettings = new Reference2ObjectOpenHashMap();

    public static void initFromConfigSettings() {
        dynamicSettings = new Reference2ObjectOpenHashMap(ConfigVines.configSettings);
        for (Map.Entry<Block, DynamicSetting> entry : ConfigVines.configSettings.entrySet()) {
            DynamicSetting setting = entry.getValue();
            if (!(setting instanceof VineSetting)) continue;
            VineSetting vsetting = (VineSetting)setting;
            if (vsetting.link == null) continue;
            dynamicSettings.put(vsetting.link, new VineSetting(vsetting.bottomFixed, vsetting.waterPhysics, vsetting.sideConnection, vsetting.hitboxScale, vsetting.stiffness, vsetting.damping, vsetting.linkedPhysics, entry.getKey()));
        }
    }

    public static boolean isChunkInRange(int chunkX, int chunkZ) {
        int dz;
        int bx = SectionPos.sectionToBlockCoord((int)chunkX, (int)8);
        int bz = SectionPos.sectionToBlockCoord((int)chunkZ, (int)8);
        int dx = playerPos.getX() - bx;
        return (double)(dx * dx + (dz = playerPos.getZ() - bz) * dz) < ConfigClient.vineRange * ConfigClient.vineRange;
    }

    public static boolean isChunkInRange(BlockPos pos) {
        return VineHelper.isChunkInRange(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getZ()));
    }

    public static DynamicSetting getSetting(BlockState state) {
        return dynamicSettings.get(state.getBlock());
    }

    static {
        ConfigVines.init();
    }
}

