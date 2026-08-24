package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.enhancedcelestials2core.util.CodecUtil;
import dev.corgitaco.enhancedcelestials2core.util.Description;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public record MobEffectInstanceBuilder(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) implements Description {
   public static final Codec<MobEffectInstanceBuilder> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            CodecUtil.MOB_EFFECT.fieldOf("effect").forGetter(MobEffectInstanceBuilder::effect),
            Codec.INT.fieldOf("duration_in_ticks").forGetter(MobEffectInstanceBuilder::duration),
            Codec.INT.fieldOf("amplifier").forGetter(MobEffectInstanceBuilder::amplifier),
            Codec.BOOL.fieldOf("ambient").forGetter(MobEffectInstanceBuilder::ambient),
            Codec.BOOL.fieldOf("visible").forGetter(MobEffectInstanceBuilder::visible),
            Codec.BOOL.fieldOf("show_icon").forGetter(MobEffectInstanceBuilder::showIcon)
         )
         .apply(builder, MobEffectInstanceBuilder::new)
   );

   public MobEffectInstance makeInstance() {
      return new MobEffectInstance(
         BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this.effect), this.duration, this.amplifier, this.ambient, this.visible, this.showIcon
      );
   }

   @Override
   public Component description() {
      return Component.translatable(
         "enhancedcelestials2core.mob_effect_instance", new Object[]{Component.translatable(this.effect.getDescriptionId()), this.amplifier, this.duration}
      );
   }
}
