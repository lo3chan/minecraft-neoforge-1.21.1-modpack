package net.mehvahdjukaar.moonlight.api.util.math;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record EntityAngles(float pitch, float yaw) {
   private static final Codec<Vector2f> VEC2 = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         list -> Util.fixedSize(list, 2).map(listx -> new Vector2f((Float)listx.getFirst(), (Float)listx.get(1))),
         vector3f -> List.of(vector3f.x(), vector3f.y())
      );
   private static final Codec<EntityAngles> CODEC = VEC2.xmap(
      vector2f -> new EntityAngles(vector2f.x, vector2f.y), eulerAnglesYX -> new Vector2f(eulerAnglesYX.pitch, eulerAnglesYX.yaw)
   );

   public static EntityAngles fromRadians(float pitchRad, float yawRad) {
      return new EntityAngles((float)Math.toDegrees(pitchRad), (float)Math.toDegrees(yawRad));
   }

   public static EntityAngles fromDirection(Direction dir) {
      return switch (dir) {
         case SOUTH -> new EntityAngles(0.0F, 0.0F);
         case WEST -> new EntityAngles(0.0F, 90.0F);
         case NORTH -> new EntityAngles(0.0F, 180.0F);
         case EAST -> new EntityAngles(0.0F, -90.0F);
         case UP -> new EntityAngles(-90.0F, 0.0F);
         case DOWN -> new EntityAngles(90.0F, 0.0F);
         default -> throw new MatchException(null, null);
      };
   }

   public Direction closestDirection() {
      if (this.pitch <= -45.0F) {
         return Direction.UP;
      } else if (this.pitch >= 45.0F) {
         return Direction.DOWN;
      } else {
         float y = Mth.wrapDegrees(this.yaw);
         if (y >= -45.0F && y < 45.0F) {
            return Direction.SOUTH;
         } else if (y >= 45.0F && y < 135.0F) {
            return Direction.WEST;
         } else {
            return y >= -135.0F && y < -45.0F ? Direction.EAST : Direction.NORTH;
         }
      }
   }

   public static EntityAngles fromQuaternion(Quaternionf q) {
      Vector3f forward = new Vector3f(0.0F, 0.0F, 1.0F);
      q.transform(forward);
      forward.normalize();
      float yawRad = (float)(-Mth.atan2(forward.x, forward.z));
      float pitchRad = (float)(-Mth.atan2(forward.y, Mth.sqrt(forward.x * forward.x + forward.z * forward.z)));
      return fromRadians(pitchRad, yawRad);
   }

   public static EntityAngles of(float pitch, float yaw) {
      return new EntityAngles(pitch, yaw);
   }

   public Quaternionf toQuaternion() {
      float pitchRad = (float)Math.toRadians(this.pitch);
      float yawRad = (float)Math.toRadians(this.yaw);
      return new Quaternionf().rotateY(-yawRad).rotateX(pitchRad);
   }

   public float yawRad() {
      return (float)Math.toRadians(this.yaw);
   }

   public float pitchRad() {
      return (float)Math.toRadians(this.pitch);
   }

   public EntityAngles withYaw(float yaw) {
      return new EntityAngles(this.pitchRad(), yaw);
   }

   public EntityAngles withPitch(float pitch) {
      return new EntityAngles(pitch, this.yawRad());
   }

   public EntityAngles clamped(float minPitch, float maxPitch, float minYaw, float maxYaw) {
      return of(Mth.clamp(this.pitch, minPitch, maxPitch), Mth.clamp(this.yaw, minYaw, maxYaw));
   }

   @Override
   public String toString() {
      return "[pitch=" + this.pitch + ", yaw=" + this.yaw + "]";
   }
}
