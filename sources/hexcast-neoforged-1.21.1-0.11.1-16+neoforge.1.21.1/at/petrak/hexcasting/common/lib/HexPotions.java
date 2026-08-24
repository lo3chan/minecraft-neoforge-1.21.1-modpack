package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public class HexPotions {
   private static final Map<ResourceLocation, Potion> POTIONS = new LinkedHashMap<>();
   public static final Potion ENLARGE_GRID = make(
      "enlarge_grid",
      new Potion("enlarge_grid", new MobEffectInstance[]{new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(HexMobEffects.ENLARGE_GRID), 3600)})
   );
   public static final Potion ENLARGE_GRID_LONG = make(
      "enlarge_grid_long",
      new Potion(
         "enlarge_grid_long", new MobEffectInstance[]{new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(HexMobEffects.ENLARGE_GRID), 9600)}
      )
   );
   public static final Potion ENLARGE_GRID_STRONG = make(
      "enlarge_grid_strong",
      new Potion(
         "enlarge_grid_strong", new MobEffectInstance[]{new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(HexMobEffects.ENLARGE_GRID), 1800, 1)}
      )
   );

   public static void register(BiConsumer<Potion, ResourceLocation> r) {
      for (Entry<ResourceLocation, Potion> e : POTIONS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   public static void addRecipes() {
   }

   private static <T extends Potion> T make(String id, T potion) {
      Potion old = POTIONS.put(HexAPI.modLoc(id), potion);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + id);
      } else {
         return potion;
      }
   }
}
