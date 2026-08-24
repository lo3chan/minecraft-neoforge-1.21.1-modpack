package at.petrak.hexcasting.common.entities;

import at.petrak.hexcasting.api.HexAPI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;

public class HexEntities {
   private static final Map<ResourceLocation, EntityType<?>> ENTITIES = new LinkedHashMap<>();
   public static final EntityType<EntityWallScroll> WALL_SCROLL = register(
      "wall_scroll",
      Builder.of(EntityWallScroll::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(2147483647).build("hexcasting:wall_scroll")
   );

   public static void registerEntities(BiConsumer<EntityType<?>, ResourceLocation> r) {
      for (Entry<ResourceLocation, EntityType<?>> e : ENTITIES.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static <T extends Entity> EntityType<T> register(String id, EntityType<T> type) {
      EntityType<?> old = ENTITIES.put(HexAPI.modLoc(id), type);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + id);
      } else {
         return type;
      }
   }
}
