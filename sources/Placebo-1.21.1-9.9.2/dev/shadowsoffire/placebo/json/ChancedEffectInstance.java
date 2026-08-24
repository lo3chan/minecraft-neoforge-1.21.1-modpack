package dev.shadowsoffire.placebo.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public record ChancedEffectInstance(float chance, Holder<MobEffect> effect, StepFunction amplifier, boolean ambient, boolean visible) {
   public static Codec<ChancedEffectInstance> CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 1.0F).forGetter(ChancedEffectInstance::chance),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(ChancedEffectInstance::effect),
            StepFunction.CODEC.optionalFieldOf("amplifier", StepFunction.constant(0.0F)).forGetter(ChancedEffectInstance::amplifier),
            Codec.BOOL.optionalFieldOf("ambient", true).forGetter(ChancedEffectInstance::ambient),
            Codec.BOOL.optionalFieldOf("visible", false).forGetter(ChancedEffectInstance::visible)
         )
         .apply(inst, ChancedEffectInstance::new)
   );
   public static Codec<ChancedEffectInstance> CONSTANT_CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            Codec.unit(1.0F).optionalFieldOf("chance", 1.0F).forGetter(a -> 1.0F),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(ChancedEffectInstance::effect),
            Codec.intRange(0, 255)
               .optionalFieldOf("amplifier", 0)
               .xmap(StepFunction::constant, sf -> (int)sf.min())
               .forGetter(ChancedEffectInstance::amplifier),
            Codec.BOOL.optionalFieldOf("ambient", true).forGetter(ChancedEffectInstance::ambient),
            Codec.BOOL.optionalFieldOf("visible", false).forGetter(ChancedEffectInstance::visible)
         )
         .apply(inst, ChancedEffectInstance::new)
   );

   public MobEffectInstance create(RandomSource rand, int duration) {
      return new MobEffectInstance(this.effect, duration, this.amplifier.getInt(rand.nextFloat()), this.ambient, this.visible);
   }

   public MobEffectInstance createDeterministic(int duration) {
      return new MobEffectInstance(this.effect, duration, this.amplifier.getInt(0.0F), this.ambient, this.visible);
   }
}
