package net.mehvahdjukaar.amendments.common.entity;

import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.SuppCompat;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.amendments.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.entity.ImprovedFallingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FallingLanternEntity extends ImprovedFallingBlockEntity {
   public FallingLanternEntity(EntityType<FallingLanternEntity> type, Level level) {
      super(type, level);
   }

   public FallingLanternEntity(Level level) {
      super(ModRegistry.FALLING_LANTERN.get(), level);
   }

   public FallingLanternEntity(Level level, BlockPos pos, BlockState blockState, double yOffset) {
      super(ModRegistry.FALLING_LANTERN.get(), level, pos, blockState, false);
      this.yo = pos.getY() + yOffset;
   }

   public static FallingBlockEntity fall(Level level, BlockPos pos, BlockState state, double yOffset) {
      FallingLanternEntity entity = new FallingLanternEntity(level, pos, state, yOffset);
      level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
      level.addFreshEntity(entity);
      return entity;
   }

   public boolean causeFallDamage(float height, float amount, DamageSource source) {
      boolean r = super.causeFallDamage(height, amount, source);
      if (CommonConfigs.FALLING_LANTERNS.get().hasFire() && this.getDeltaMovement().lengthSqr() > 0.16000000000000003) {
         BlockState state = this.getBlockState();
         BlockPos pos = BlockPos.containing(this.getX(), this.getY() + 0.25, this.getZ());
         Level level = this.level();
         level.levelEvent(null, 2001, pos, Block.getId(state));
         if (state.getLightEmission() != 0) {
            if (CompatHandler.SUPPLEMENTARIES && level instanceof ServerLevel l) {
               SuppCompat.createMiniExplosion(l, pos, true);
            } else if (level.getBlockState(pos).isAir() && BaseFireBlock.canBePlacedAt(level, pos, Direction.DOWN)) {
               level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
            }
         } else {
            this.spawnAtLocation(state.getBlock());
         }

         this.setCancelDrop(true);
         this.discard();
      }

      return r;
   }

   public static boolean maybeFall(boolean canSurvive, BlockState state, BlockPos pos, LevelReader worldIn) {
      return !canSurvive
            && worldIn instanceof Level l
            && CommonConfigs.FALLING_LANTERNS.get().isOn()
            && l.getBlockState(pos).is(state.getBlock())
            && !state.is(ModTags.FALLING_LANTERNS_BLACKLIST)
         ? createFallingLantern(state, pos, l)
         : false;
   }

   public static boolean createFallingLantern(BlockState state, BlockPos pos, Level level) {
      if (FallingBlock.isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight() && state.hasProperty(LanternBlock.HANGING)) {
         double maxY = state.getShape(level, pos).bounds().maxY;
         state = (BlockState)state.setValue(LanternBlock.HANGING, false);
         double yOffset = maxY - state.getShape(level, pos).bounds().maxY;
         fall(level, pos, state, yOffset);
         return true;
      } else {
         return false;
      }
   }

   public static enum FallMode {
      ON,
      OFF,
      NO_FIRE;

      public boolean hasFire() {
         return this != NO_FIRE;
      }

      public boolean isOn() {
         return this != OFF;
      }
   }
}
