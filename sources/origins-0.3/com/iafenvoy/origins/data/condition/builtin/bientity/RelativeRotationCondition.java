package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record RelativeRotationCondition(
   EnumSet<Axis> axis, RelativeRotationCondition.RotationType actorRotation, RelativeRotationCondition.RotationType targetRotation, Comparison comparison
) implements BiEntityCondition {
   public static final MapCodec<RelativeRotationCondition> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Axis.CODEC
               .listOf()
               .xmap(EnumSet::copyOf, List::copyOf)
               .optionalFieldOf("axis", EnumSet.allOf(Axis.class))
               .forGetter(RelativeRotationCondition::axis),
            RelativeRotationCondition.RotationType.CODEC
               .optionalFieldOf("actor_rotation", RelativeRotationCondition.RotationType.HEAD)
               .forGetter(RelativeRotationCondition::actorRotation),
            RelativeRotationCondition.RotationType.CODEC
               .optionalFieldOf("target_rotation", RelativeRotationCondition.RotationType.BODY)
               .forGetter(RelativeRotationCondition::targetRotation),
            Comparison.CODEC.forGetter(RelativeRotationCondition::comparison)
         )
         .apply(instance, RelativeRotationCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends BiEntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity source, @NotNull Entity target) {
      Vec3 vec0 = this.actorRotation().getRotation(source);
      Vec3 vec1 = this.targetRotation().getRotation(target);
      vec0 = reduceAxes(vec0, this.axis());
      vec1 = reduceAxes(vec1, this.axis());
      double angle = getAngleBetween(vec0, vec1);
      return this.comparison.compare(angle);
   }

   private static double getAngleBetween(Vec3 a, Vec3 b) {
      double dot = a.dot(b);
      return dot / (a.length() * b.length());
   }

   private static Vec3 reduceAxes(Vec3 vector, EnumSet<Axis> axesToKeep) {
      return new Vec3(
         axesToKeep.contains(Axis.X) ? vector.x() : 0.0, axesToKeep.contains(Axis.Y) ? vector.y() : 0.0, axesToKeep.contains(Axis.Z) ? vector.z() : 0.0
      );
   }

   private static Vec3 getRotationVector(float pitch, float yaw) {
      float f = pitch * 0.017453292F;
      float g = -yaw * 0.017453292F;
      float h = Mth.cos(g);
      float i = Mth.sin(g);
      float j = Mth.cos(f);
      float k = Mth.sin(f);
      return new Vec3(i * j, -k, h * j);
   }

   public static enum RotationType implements StringRepresentable {
      HEAD(e -> e.getViewVector(1.0F)),
      BODY(e -> e instanceof LivingEntity l ? RelativeRotationCondition.getRotationVector(0.0F, l.yBodyRot) : e.getViewVector(1.0F));

      public static final Codec<RelativeRotationCondition.RotationType> CODEC = StringRepresentable.fromValues(RelativeRotationCondition.RotationType::values);
      private final Function<Entity, Vec3> function;

      private RotationType(Function<Entity, Vec3> function) {
         this.function = function;
      }

      public Vec3 getRotation(Entity entity) {
         return this.function.apply(entity);
      }

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
