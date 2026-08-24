package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record RidingRecursiveCondition(BiEntityCondition biEntityCondition, Comparison comparison) implements EntityCondition {
   public static final MapCodec<RidingRecursiveCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(RidingRecursiveCondition::biEntityCondition),
            Comparison.CODEC.forGetter(RidingRecursiveCondition::comparison)
         )
         .apply(i, RidingRecursiveCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity entity) {
      Entity vehicle = entity.getVehicle();

      int matches;
      for (matches = 0; vehicle != null; vehicle = vehicle.getVehicle()) {
         if (this.biEntityCondition.test(entity, vehicle)) {
            matches++;
         }
      }

      return this.comparison.compare(matches);
   }
}
