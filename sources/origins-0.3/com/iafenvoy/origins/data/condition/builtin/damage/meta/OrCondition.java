package com.iafenvoy.origins.data.condition.builtin.damage.meta;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public record OrCondition(List<DamageCondition> conditions) implements DamageCondition {
   public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(DamageCondition.CODEC.listOf().fieldOf("conditions").forGetter(OrCondition::conditions)).apply(i, OrCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends DamageCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull DamageSource source, float amount) {
      return this.conditions.stream().anyMatch(x -> x.test(source, amount));
   }
}
