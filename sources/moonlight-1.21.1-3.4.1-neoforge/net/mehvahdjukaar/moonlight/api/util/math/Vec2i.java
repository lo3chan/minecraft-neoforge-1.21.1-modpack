package net.mehvahdjukaar.moonlight.api.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;

public record Vec2i(int x, int y) {
   public static final Codec<Vec2i> CODEC = Codec.INT
      .listOf()
      .comapFlatMap(
         list -> list.size() != 2
            ? DataResult.error(() -> "Expected list of size 2 for Vec2i")
            : DataResult.success(new Vec2i((Integer)list.get(0), (Integer)list.get(1))),
         vec -> List.of(vec.x, vec.y)
      );
   public static final Vec2i ZERO = new Vec2i(0, 0);
   public static final Vec2i ONE = new Vec2i(1, 1);

   public Vec2i add(Vec2i vec2i) {
      return new Vec2i(this.x + vec2i.x, this.y + vec2i.y);
   }

   public Vec2i subtract(Vec2i vec2i) {
      return new Vec2i(this.x - vec2i.x, this.y - vec2i.y);
   }

   public Vec2i multiply(int scalar) {
      return new Vec2i(this.x * scalar, this.y * scalar);
   }

   public Vec2i scale(int scalar) {
      return new Vec2i(this.x * scalar, this.y * scalar);
   }

   public Vec2i offset(Direction2D direction2D) {
      return this.add(direction2D.getStep());
   }

   public float length() {
      return (float)Math.sqrt(this.x * this.x + this.y * this.y);
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y;
   }

   public int manhattanLength() {
      return Math.abs(this.x) + Math.abs(this.y);
   }

   public int manhattanDistance(Vec2i other) {
      return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
   }

   public int max() {
      return Math.max(this.x, this.y);
   }

   public int min() {
      return Math.min(this.x, this.y);
   }

   @Override
   public String toString() {
      return "[" + this.x + ", " + this.y + "]";
   }
}
