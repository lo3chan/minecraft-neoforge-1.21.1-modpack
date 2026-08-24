package net.mehvahdjukaar.amendments.integration;

import java.util.List;
import java.util.function.Function;
import net.mehvahdjukaar.amendments.common.block.CeilingBannerBlock;
import net.mehvahdjukaar.amendments.common.tile.LiquidCauldronBlockTile;
import net.mehvahdjukaar.amendments.events.behaviors.CauldronConversion;
import net.mehvahdjukaar.moonlight.api.fluids.FluidOffer;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidTank;
import net.mehvahdjukaar.supplementaries.common.block.IRopeConnection;
import net.mehvahdjukaar.supplementaries.common.block.blocks.CandleHolderBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RopeBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.SconceBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.SconceLeverBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.SconceWallBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.StickBlock;
import net.mehvahdjukaar.supplementaries.common.block.faucet.FaucetBehaviorsManager;
import net.mehvahdjukaar.supplementaries.common.block.faucet.FaucetTarget.BlState;
import net.mehvahdjukaar.supplementaries.common.misc.explosion.GunpowderExplosion;
import net.mehvahdjukaar.supplementaries.common.utils.MiscUtils;
import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.mehvahdjukaar.supplementaries.reg.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class SuppCompat {
   public static void setup() {
      FaucetBehaviorsManager.addRegisterFaucetInteractions(event -> event.registerInteraction(new SuppCompat.FaucetCauldronConversion()));
   }

   public static boolean canBannerAttachToRope(BlockState state, BlockState above) {
      if (above.getBlock() instanceof RopeBlock && !(Boolean)above.getValue(RopeBlock.DOWN)) {
         Direction dir = (Direction)state.getValue(CeilingBannerBlock.FACING);
         return (Boolean)above.getValue((Property)RopeBlock.FACING_TO_PROPERTY_MAP.get(dir.getClockWise()))
            && (Boolean)above.getValue((Property)RopeBlock.FACING_TO_PROPERTY_MAP.get(dir.getCounterClockWise()));
      } else {
         return false;
      }
   }

   public static boolean isVerticalStick(BlockState state, Direction facing) {
      return state.getBlock() instanceof StickBlock
         && (facing.getAxis() == Axis.X ? !(Boolean)state.getValue(StickBlock.AXIS_X) : !(Boolean)state.getValue(StickBlock.AXIS_Z));
   }

   public static boolean isRope(Block block) {
      return block instanceof RopeBlock;
   }

   public static void spawnCakeParticles(Level level, BlockPos pos, RandomSource rand) {
      if (MiscUtils.getFestivity().isStValentine() && rand.nextFloat() > 0.8) {
         double d0 = pos.getX() + 0.5 + (rand.nextFloat() - 0.5);
         double d1 = pos.getY() + 0.5 + (rand.nextFloat() - 0.5);
         double d2 = pos.getZ() + 0.5 + (rand.nextFloat() - 0.5);
         level.addParticle(ParticleTypes.HEART, d0, d1, d2, 0.0, 0.0, 0.0);
      }
   }

   public static float getSignColorMult() {
      return ClientConfigs.getSignColorMult();
   }

   public static boolean isSupportingCeiling(BlockState upState, BlockPos pos, LevelReader world) {
      return IRopeConnection.isSupportingCeiling(upState, pos, world);
   }

   public static void createMiniExplosion(ServerLevel level, BlockPos pos, boolean b) {
      GunpowderExplosion.explode(level, pos);
   }

   public static boolean canConnectDown(BlockState neighborState) {
      return IRopeConnection.canConnectDown(neighborState);
   }

   public static boolean isSconce(Block block) {
      return block instanceof SconceLeverBlock ? true : block instanceof SconceBlock && !(block instanceof SconceWallBlock);
   }

   public static boolean isCandleHolder(Block block) {
      return block instanceof CandleHolderBlock;
   }

   public static EntityType<? extends Entity> getSlimeBall() {
      return (EntityType<? extends Entity>)ModEntities.THROWABLE_SLIMEBALL.get();
   }

   public static Vec3 getCandleHolderParticleOffset(BlockState state) {
      if (state.getBlock() instanceof CandleHolderBlock cb) {
         try {
            Function<BlockState, List<Vec3>> offsets = cb.particleOffsets;
            List<Vec3> particleOffsets = offsets.apply(state);
            if (!particleOffsets.isEmpty()) {
               return ((Vec3)particleOffsets.getFirst()).subtract(0.5, 0.5, 0.5);
            }
         } catch (Exception var4) {
         }
      }

      return Vec3.ZERO;
   }

   public static class FaucetCauldronConversion implements BlState {
      public Integer fill(Level level, BlockPos pos, BlockState target, FluidOffer offer) {
         if (target.is(Blocks.CAULDRON)) {
            SoftFluidStack fluid = offer.fluid();
            int minAmount = offer.minAmount();
            BlockState newState = CauldronConversion.getNewState(pos, level, fluid);
            if (newState != null) {
               level.setBlockAndUpdate(pos, newState);
               if (level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
                  SoftFluidTank tank = te.getSoftFluidTank();
                  tank.setFluid(fluid.copyWithCount(minAmount));
                  return minAmount;
               }
            }
         }

         return null;
      }
   }
}
