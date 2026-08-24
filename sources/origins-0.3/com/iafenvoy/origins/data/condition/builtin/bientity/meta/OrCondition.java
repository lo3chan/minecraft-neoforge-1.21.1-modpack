package com.iafenvoy.origins.data.condition.builtin.bientity.meta;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record OrCondition(List<BiEntityCondition> conditions) implements BiEntityCondition {
   public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(BiEntityCondition.CODEC.listOf().fieldOf("conditions").forGetter(OrCondition::conditions)).apply(i, OrCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BiEntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity source, @NotNull Entity target) {
      return this.conditions.stream().anyMatch(x -> x.test(source, target));
   }
}
