package com.iafenvoy.origins.data.condition.builtin.damage;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;

public record TypeCondition(Holder<DamageType> damageType) implements DamageCondition {
   public static final MapCodec<TypeCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(DamageType.CODEC.fieldOf("damage_type").forGetter(TypeCondition::damageType)).apply(i, TypeCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends DamageCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull DamageSource source, float amount) {
      return this.damageType.unwrapKey().<Boolean>map(source::is).orElse(false);
   }
}
