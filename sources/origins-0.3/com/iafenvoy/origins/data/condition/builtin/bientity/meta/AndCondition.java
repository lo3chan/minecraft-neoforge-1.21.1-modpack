package com.iafenvoy.origins.data.condition.builtin.bientity.meta;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record AndCondition(List<BiEntityCondition> conditions) implements BiEntityCondition {
   public static final MapCodec<AndCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(BiEntityCondition.CODEC.listOf().fieldOf("conditions").forGetter(AndCondition::conditions)).apply(i, AndCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BiEntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity source, @NotNull Entity target) {
      return this.conditions.stream().allMatch(x -> x.test(source, target));
   }
}
