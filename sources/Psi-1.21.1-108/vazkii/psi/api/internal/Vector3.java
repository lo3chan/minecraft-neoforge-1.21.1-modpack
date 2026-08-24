package vazkii.psi.api.internal;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Vector3 {
   public static final Vector3 zero = new Vector3();
   public static final Codec<Vector3> CODEC = Codec.DOUBLE
      .listOf()
      .comapFlatMap(
         to -> Util.fixedSize(to, 3).map(list -> new Vector3((Double)list.getFirst(), (Double)list.get(1), (Double)list.get(2))),
         from -> List.of(from.x, from.y, from.z)
      );
   public static final StreamCodec<ByteBuf, Vector3> STREAM_CODEC = new StreamCodec<ByteBuf, Vector3>() {
      @NotNull
      public Vector3 decode(ByteBuf buffer) {
         return new Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
      }

      public void encode(ByteBuf buffer, Vector3 vector3) {
         buffer.writeDouble(vector3.x);
         buffer.writeDouble(vector3.y);
         buffer.writeDouble(vector3.z);
      }
   };
   public double x;
   public double y;
   public double z;

   public Vector3() {
   }

   public Vector3(double d, double d1, double d2) {
      this.x = d;
      this.y = d1;
      this.z = d2;
   }

   public Vector3(Vector3 vec) {
      this.x = vec.x;
      this.y = vec.y;
      this.z = vec.z;
   }

   public Vector3(Vec3 vec) {
      this.x = vec.x;
      this.y = vec.y;
      this.z = vec.z;
   }

   public static Vector3 fromEntity(Entity e) {
      return new Vector3(e.getX(), e.getY(), e.getZ());
   }

   public static Vector3 fromBlockPos(BlockPos pos) {
      return new Vector3(pos.getX(), pos.getY(), pos.getZ());
   }

   public static Vector3 fromVec3d(Vec3 vec3d) {
      return new Vector3(vec3d.x(), vec3d.y(), vec3d.z());
   }

   public static Vector3 fromDirection(Direction direction) {
      return new Vector3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
   }

   public Vector3 copy() {
      return new Vector3(this);
   }

   public Vector3 set(double d, double d1, double d2) {
      this.x = d;
      this.y = d1;
      this.z = d2;
      return this;
   }

   public Vector3 set(Vector3 vec) {
      this.x = vec.x;
      this.y = vec.y;
      this.z = vec.z;
      return this;
   }

   public double dotProduct(Vector3 vec) {
      double d = vec.x * this.x + vec.y * this.y + vec.z * this.z;
      if (d > 1.0 && d < 1.00001) {
         d = 1.0;
      } else if (d < -1.0 && d > -1.00001) {
         d = -1.0;
      }

      return d;
   }

   public Vector3 crossProduct(Vector3 vec) {
      double d = this.y * vec.z - this.z * vec.y;
      double d1 = this.z * vec.x - this.x * vec.z;
      double d2 = this.x * vec.y - this.y * vec.x;
      this.x = d;
      this.y = d1;
      this.z = d2;
      return this;
   }

   public Vector3 add(double d, double d1, double d2) {
      this.x += d;
      this.y += d1;
      this.z += d2;
      return this;
   }

   public Vector3 add(Vector3 vec) {
      this.x = this.x + vec.x;
      this.y = this.y + vec.y;
      this.z = this.z + vec.z;
      return this;
   }

   public Vector3 sub(Vector3 vec) {
      return this.subtract(vec);
   }

   public Vector3 subtract(Vector3 vec) {
      this.x = this.x - vec.x;
      this.y = this.y - vec.y;
      this.z = this.z - vec.z;
      return this;
   }

   public Vector3 multiply(double d) {
      this.x *= d;
      this.y *= d;
      this.z *= d;
      return this;
   }

   public double mag() {
      return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public double magSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public Vector3 normalize() {
      double d = this.mag();
      if (d != 0.0) {
         this.multiply(1.0 / d);
      }

      return this;
   }

   @Override
   public String toString() {
      MathContext cont = new MathContext(5, RoundingMode.HALF_UP);
      return "Vector[" + new BigDecimal(this.x, cont) + ", " + new BigDecimal(this.y, cont) + ", " + new BigDecimal(this.z, cont) + "]";
   }

   public Vec3 toVec3D() {
      return new Vec3(this.x, this.y, this.z);
   }

   public Vec3i toVec3i() {
      return new Vec3i((int)Math.round(this.x), (int)Math.round(this.y), (int)Math.round(this.z));
   }

   public BlockPos toBlockPos() {
      return new BlockPos(this.toVec3i());
   }

   public boolean isZero() {
      return this.x == 0.0 && this.y == 0.0 && this.z == 0.0;
   }

   public boolean isAxial() {
      return this.x == 0.0 ? this.y == 0.0 || this.z == 0.0 : this.y == 0.0 && this.z == 0.0;
   }

   public Vector3 negate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      return this;
   }

   public Vector3 project(Vector3 b) {
      double l = b.magSquared();
      if (l == 0.0) {
         this.set(0.0, 0.0, 0.0);
         return this;
      } else {
         double m = this.dotProduct(b) / l;
         this.set(b).multiply(m);
         return this;
      }
   }

   public Vector3 rotate(double angle, Vector3 axis) {
      Quat.aroundAxis(axis.copy().normalize(), angle).rotate(this);
      return this;
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof Vector3 v) ? false : this.x == v.x && this.y == v.y && this.z == v.z;
   }

   @Override
   public int hashCode() {
      return 31 * (31 * (31 + Double.hashCode(this.x)) + Double.hashCode(this.y)) + Double.hashCode(this.z);
   }
}
