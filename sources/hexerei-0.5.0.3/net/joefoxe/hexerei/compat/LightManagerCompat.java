package net.joefoxe.hexerei.compat;

import com.hollingsworth.arsnouveau.setup.config.Config;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.light.LightManager;
import net.minecraft.world.entity.EntityType;

public class LightManagerCompat {
   public static void fallbackToArs() {
      Map<EntityType<?>, List<Function<?, Integer>>> hexerei = LightManager.getLightRegistry();
      Map<EntityType<?>, List<Function<?, Integer>>> ars = com.hollingsworth.arsnouveau.common.light.LightManager.getLightRegistry();

      for (Entry<EntityType<?>, List<Function<?, Integer>>> entry : hexerei.entrySet()) {
         if (ars.putIfAbsent(entry.getKey(), entry.getValue()) != null) {
            ars.get(entry.getKey()).addAll(entry.getValue());
         }
      }

      Config.ITEM_LIGHTMAP.putAll(HexConfig.ITEM_LIGHTMAP);
      Config.ENTITY_LIGHT_MAP.putAll(HexConfig.ENTITY_LIGHT_MAP);
      HexConfig.DYNAMIC_LIGHT_TOGGLE.set(false);
   }
}
