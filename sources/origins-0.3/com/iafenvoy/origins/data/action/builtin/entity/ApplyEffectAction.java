package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record ApplyEffectAction(List<MobEffectInstance> effect) implements EntityAction {
   public static final MapCodec<ApplyEffectAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(CombinedCodecs.MOB_EFFECT_INSTANCE.fieldOf("effect").forGetter(ApplyEffectAction::effect)).apply(i, ApplyEffectAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Entity source) {
      if (source instanceof LivingEntity living) {
         this.effect.stream().map(MobEffectInstance::new).forEach(living::addEffect);
      }
   }
}
