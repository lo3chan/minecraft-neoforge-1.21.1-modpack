package net.diebuddies.physics.vines;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigVines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class VineHelper {
   public static volatile BlockPos playerPos = new BlockPos(0, 0, 0);
   private static Map<Block, DynamicSetting> dynamicSettings = new Reference2ObjectOpenHashMap();

   public static void initFromConfigSettings() {
      dynamicSettings = new Reference2ObjectOpenHashMap(ConfigVines.configSettings);

      for (Entry<Block, DynamicSetting> entry : ConfigVines.configSettings.entrySet()) {
         DynamicSetting setting = entry.getValue();
         if (setting instanceof VineSetting vsetting && vsetting.link != null) {
            dynamicSettings.put(
               vsetting.link,
               new VineSetting(
                  vsetting.bottomFixed,
                  vsetting.waterPhysics,
                  vsetting.sideConnection,
                  vsetting.hitboxScale,
                  vsetting.stiffness,
                  vsetting.damping,
                  vsetting.linkedPhysics,
                  entry.getKey()
               )
            );
         }
      }
   }

   public static boolean isChunkInRange(int chunkX, int chunkZ) {
      int bx = SectionPos.sectionToBlockCoord(chunkX, 8);
      int bz = SectionPos.sectionToBlockCoord(chunkZ, 8);
      int dx = playerPos.getX() - bx;
      int dz = playerPos.getZ() - bz;
      return dx * dx + dz * dz < ConfigClient.vineRange * ConfigClient.vineRange;
   }

   public static boolean isChunkInRange(BlockPos pos) {
      return isChunkInRange(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
   }

   public static DynamicSetting getSetting(BlockState state) {
      return dynamicSettings.get(state.getBlock());
   }

   static {
      ConfigVines.init();
   }
}
