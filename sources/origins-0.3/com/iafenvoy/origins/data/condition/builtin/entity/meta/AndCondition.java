package com.iafenvoy.origins.data.condition.builtin.entity.meta;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record AndCondition(List<EntityCondition> conditions) implements EntityCondition {
   public static final MapCodec<AndCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(EntityCondition.CODEC.listOf().fieldOf("conditions").forGetter(AndCondition::conditions)).apply(i, AndCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity entity) {
      return this.conditions.stream().allMatch(x -> x.test(entity));
   }
}
