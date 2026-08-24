package com.iafenvoy.origins.data.condition.builtin.entity.meta;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record OrCondition(List<EntityCondition> conditions) implements EntityCondition {
   public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(EntityCondition.CODEC.listOf().fieldOf("conditions").forGetter(OrCondition::conditions)).apply(i, OrCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity entity) {
      return this.conditions.stream().anyMatch(x -> x.test(entity));
   }
}
