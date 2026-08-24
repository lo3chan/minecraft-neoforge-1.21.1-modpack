package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.misc.HexMobEffect;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class HexMobEffects {
   private static final Map<ResourceLocation, MobEffect> EFFECTS = new LinkedHashMap<>();
   public static final MobEffect ENLARGE_GRID = make("enlarge_grid", new HexMobEffect(MobEffectCategory.BENEFICIAL, 13137407))
      .addAttributeModifier(
         BuiltInRegistries.ATTRIBUTE.wrapAsHolder(HexAttributes.GRID_ZOOM), HexAPI.modLoc("enlarge_grid"), 0.25, Operation.ADD_MULTIPLIED_TOTAL
      );
   public static final MobEffect SHRINK_GRID = make("shrink_grid", new HexMobEffect(MobEffectCategory.HARMFUL, 15445276))
      .addAttributeModifier(
         BuiltInRegistries.ATTRIBUTE.wrapAsHolder(HexAttributes.GRID_ZOOM), HexAPI.modLoc("shrink_grid"), -0.2, Operation.ADD_MULTIPLIED_TOTAL
      );

   public static void register(BiConsumer<MobEffect, ResourceLocation> r) {
      for (Entry<ResourceLocation, MobEffect> e : EFFECTS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static <T extends MobEffect> T make(String id, T effect) {
      MobEffect old = EFFECTS.put(HexAPI.modLoc(id), effect);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + id);
      } else {
         return effect;
      }
   }
}
