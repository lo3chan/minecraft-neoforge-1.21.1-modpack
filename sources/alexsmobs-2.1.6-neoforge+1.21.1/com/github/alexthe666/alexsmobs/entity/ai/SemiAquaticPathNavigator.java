package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class SemiAquaticPathNavigator extends WaterBoundPathNavigation {
   public SemiAquaticPathNavigator(Mob entitylivingIn, Level worldIn) {
      super(entitylivingIn, worldIn);
   }

   protected PathFinder createPathFinder(int p_179679_1_) {
      this.nodeEvaluator = new AmphibiousNodeEvaluator(true);
      return new PathFinder(this.nodeEvaluator, p_179679_1_);
   }

   protected boolean canUpdatePath() {
      return true;
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
   }

   protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
      Vec3 vector3d = new Vec3(posVec32.x, posVec32.y + this.mob.getBbHeight() * 0.5, posVec32.z);
      return this.level.clip(new ClipContext(posVec31, vector3d, Block.COLLIDER, Fluid.NONE, this.mob)).getType() == Type.MISS;
   }

   public boolean isStableDestination(BlockPos pos) {
      return !this.level.getBlockState(pos.below()).isAir();
   }

   public void setCanFloat(boolean canSwim) {
   }
}
