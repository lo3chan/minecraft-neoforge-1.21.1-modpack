package dev.shadowsoffire.placebo.util;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record StepFunction(float min, int steps, float step, float max) implements Float2FloatFunction {
   @Deprecated(
      forRemoval = true,
      since = "9.6.0"
   )
   public static final Codec<StepFunction> STRICT_CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            Codec.FLOAT.fieldOf("min").forGetter(StepFunction::min),
            Codec.intRange(1, 2147483647).fieldOf("steps").forGetter(StepFunction::steps),
            Codec.FLOAT.fieldOf("step").forGetter(StepFunction::step)
         )
         .apply(inst, StepFunction::new)
   );
   public static final Codec<StepFunction> BOUNDS_CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            Codec.FLOAT.fieldOf("min").forGetter(StepFunction::min),
            Codec.FLOAT.fieldOf("max").forGetter(StepFunction::max),
            Codec.FLOAT.optionalFieldOf("step", 0.01F).forGetter(StepFunction::step)
         )
         .apply(inst, StepFunction::fromBounds)
   );
   public static final Codec<StepFunction> TRANSITION_CODEC = Codec.either(STRICT_CODEC, BOUNDS_CODEC).xmap(Either::unwrap, Either::right);
   public static final Codec<StepFunction> CONSTANT_CODEC = Codec.FLOAT.xmap(StepFunction::constant, StepFunction::min);
   public static final Codec<StepFunction> CODEC = Codec.either(CONSTANT_CODEC, TRANSITION_CODEC).xmap(Either::unwrap, StepFunction::toEither);
   public static final StreamCodec<ByteBuf, StepFunction> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.FLOAT, StepFunction::min, ByteBufCodecs.INT, StepFunction::steps, ByteBufCodecs.FLOAT, StepFunction::step, StepFunction::new
   );

   @Deprecated(
      forRemoval = true,
      since = "9.6.0"
   )
   public StepFunction(float min, int steps, float step) {
      this(min, steps, step, min + steps * step);
   }

   public StepFunction(float min, int steps, float step, float max) {
      this.min = min;
      this.steps = steps;
      this.step = step;
      this.max = max;
      Preconditions.checkArgument(steps > 0, "Steps must be a positive integer");
      Preconditions.checkArgument(Math.abs(min + steps * step - max) <= 1.0E-5F, "Max value is out-of-sync with other fields.");
   }

   public float get(float level) {
      return this.min + (int)(this.steps * (level + 0.5F / this.steps)) * this.step;
   }

   public int getInt(float level) {
      return (int)this.get(level);
   }

   public int getStep(float level) {
      return (int)(this.steps * (level + 0.5F / this.steps));
   }

   public float getForStep(int step) {
      return this.min + this.step * step;
   }

   public float getIntForStep(int step) {
      return (int)this.getForStep(step);
   }

   public boolean isConstant() {
      return this.step == 0.0F;
   }

   @Deprecated(
      forRemoval = true,
      since = "9.8.2"
   )
   public void write(FriendlyByteBuf buf) {
      buf.writeFloat(this.min);
      buf.writeInt(this.steps);
      buf.writeFloat(this.step);
   }

   @Deprecated(
      forRemoval = true,
      since = "9.8.2"
   )
   public static StepFunction read(FriendlyByteBuf buf) {
      return new StepFunction(buf.readFloat(), buf.readInt(), buf.readFloat());
   }

   public static StepFunction fromBounds(float min, float max) {
      return fromBounds(min, max, 0.01F);
   }

   public static StepFunction fromBounds(float min, float max, float step) {
      if (min == max) {
         return constant(min);
      } else {
         int steps = Math.round((max - min) / step);
         if (Math.abs(min + step * steps - max) > 1.0E-4F) {
            throw new UnsupportedOperationException(
               "Failed to interpolate step function bounds with min="
                  + min
                  + "; max="
                  + max
                  + "; step="
                  + step
                  + ". The step value must be a multiple of the difference between min and max."
            );
         } else {
            return new StepFunction(min, steps, step, max);
         }
      }
   }

   public static StepFunction constant(float val) {
      return new StepFunction(val, 1, 0.0F, val);
   }

   private static Either<StepFunction, StepFunction> toEither(StepFunction function) {
      return function.isConstant() ? Either.left(function) : Either.right(function);
   }
}
