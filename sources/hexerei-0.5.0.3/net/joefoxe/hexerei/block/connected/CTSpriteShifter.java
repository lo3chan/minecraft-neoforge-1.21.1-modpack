package net.joefoxe.hexerei.block.connected;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class CTSpriteShifter {
   private static final Map<String, SpriteShiftEntry> ENTRY_CACHE = new HashMap<>();

   public static CTSpriteShiftEntry getCT(CTType type, ResourceLocation blockTexture, ResourceLocation connectedTexture) {
      String key = blockTexture + "->" + connectedTexture + "+" + type.getId();
      if (ENTRY_CACHE.containsKey(key)) {
         return (CTSpriteShiftEntry)ENTRY_CACHE.get(key);
      } else {
         CTSpriteShiftEntry entry = new CTSpriteShiftEntry(type);
         if (FMLEnvironment.dist == Dist.CLIENT) {
            entry.set(blockTexture, connectedTexture);
         }

         ENTRY_CACHE.put(key, entry);
         return entry;
      }
   }
}
