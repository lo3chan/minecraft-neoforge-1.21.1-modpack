package net.mehvahdjukaar.amendments.common;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public abstract class SwingAnimation {
   private final Function<BlockState, Vector3f> axisGetter;
   protected float angle = 0.0F;
   protected float prevAngle = 0.0F;
   public static SwingAnimation EMPTY = new SwingAnimation(null) {
      @Override
      public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
      }

      @Override
      public boolean hitByEntity(Entity entity, BlockState state, BlockPos pos) {
         return false;
      }

      @Override
      public float getAngle(float partialTicks) {
         return 0.0F;
      }

      @Override
      public void setAngle(float angle) {
      }

      @Override
      public void reset() {
      }
   };

   protected SwingAnimation(Function<BlockState, Vector3f> axisGetter) {
      this.axisGetter = axisGetter;
   }

   protected Vector3f getRotationAxis(BlockState state) {
      return this.axisGetter.apply(state);
   }

   public abstract void tick(Level var1, BlockPos var2, BlockState var3);

   public abstract boolean hitByEntity(Entity var1, BlockState var2, BlockPos var3);

   public abstract float getAngle(float var1);

   public abstract void setAngle(float var1);

   public abstract void reset();
}
