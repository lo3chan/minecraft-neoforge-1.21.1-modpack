package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(
   value = {Direction.class},
   priority = 1001
)
public abstract class DirectionMixin {
   @Shadow
   @RemapForJS("getX")
   public abstract int getStepX();

   @Shadow
   @RemapForJS("getY")
   public abstract int getStepY();

   @Shadow
   @RemapForJS("getZ")
   public abstract int getStepZ();

   @Shadow
   @RemapForJS("getIndex")
   public abstract int get3DDataValue();

   @Shadow
   @RemapForJS("getHorizontalIndex")
   public abstract int get2DDataValue();

   @Shadow
   @RemapForJS("getYaw")
   public abstract float toYRot();

   @Unique
   @RemapForJS("getPitch")
   public float kjs$getPitch() {
      return this == Direction.UP ? 180.0F : (this == Direction.DOWN ? 0.0F : 90.0F);
   }
}
