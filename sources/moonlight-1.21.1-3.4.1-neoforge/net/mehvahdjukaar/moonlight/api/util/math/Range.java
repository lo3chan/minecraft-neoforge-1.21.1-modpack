package net.mehvahdjukaar.moonlight.api.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public record Range(double min, double max) {
   public static final Codec<Range> CODEC = RecordCodecBuilder.create(
      i -> i.group(Codec.DOUBLE.fieldOf("min").forGetter(Range::min), Codec.DOUBLE.fieldOf("max").forGetter(Range::max)).apply(i, Range::new)
   );

   public static Range of(double min, double max) {
      return new Range(min, max);
   }

   public double clamp(double value) {
      return Mth.clamp(value, this.min, this.max);
   }

   public int clampInt(double value) {
      return (int)Math.round(this.clamp(value));
   }

   public boolean contains(double value) {
      return value >= this.min && value <= this.max;
   }

   public boolean contains(Range other) {
      return other.min >= this.min && other.max <= this.max;
   }

   public boolean intersects(Range other) {
      return this.min <= other.max && other.min <= this.max;
   }

   public double size() {
      return this.max - this.min;
   }

   public double mid() {
      return (this.min + this.max) * 0.5;
   }

   public double lerp(double t) {
      return Mth.lerp(t, this.min, this.max);
   }

   public double inverseLerp(double value) {
      double s = this.size();
      return s == 0.0 ? 0.0 : (value - this.min) / s;
   }

   public double random(RandomSource random) {
      return this.min + random.nextDouble() * this.size();
   }

   public int randomInt(RandomSource random) {
      return (int)Math.round(this.random(random));
   }

   public Range normalized() {
      return this.min <= this.max ? this : new Range(this.max, this.min);
   }

   public boolean isValid() {
      return this.min <= this.max;
   }

   public Range withMin(double newMin) {
      return new Range(newMin, this.max);
   }

   public Range withMax(double newMax) {
      return new Range(this.min, newMax);
   }

   @Override
   public String toString() {
      return "[" + this.min + ", " + this.max + "]";
   }
}
