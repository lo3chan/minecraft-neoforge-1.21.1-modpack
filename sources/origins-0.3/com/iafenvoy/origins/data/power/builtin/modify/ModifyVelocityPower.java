package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ModifyVelocityPower extends Power implements ModifierPowerHelper {
   public static final MapCodec<ModifyVelocityPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyVelocityPower::getModifier),
            ExtraEnumCodecs.AXIS.listOf().optionalFieldOf("axis", List.of(Axis.values())).forGetter(e -> new ArrayList<>(e.getAxes()))
         )
         .apply(i, (s, m, e) -> new ModifyVelocityPower(s, m, Set.copyOf(e)))
   );
   private final List<Modifier> modifier;
   private final Set<Axis> axes;

   public ModifyVelocityPower(Power.BaseSettings settings, List<Modifier> modifier, Set<Axis> axes) {
      super(settings);
      this.modifier = modifier;
      this.axes = axes;
   }

   @Override
   public List<Modifier> getModifier() {
      return this.modifier;
   }

   public Set<Axis> getAxes() {
      return this.axes;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   public Vec3 apply(OriginDataHolder holder, Vec3 original) {
      double x = original.x;
      double y = original.y;
      double z = original.z;
      if (this.axes.contains(Axis.X)) {
         x = this.modify(holder, x);
      }

      if (this.axes.contains(Axis.Y)) {
         y = this.modify(holder, y);
      }

      if (this.axes.contains(Axis.Z)) {
         z = this.modify(holder, z);
      }

      return new Vec3(x, y, z);
   }
}
