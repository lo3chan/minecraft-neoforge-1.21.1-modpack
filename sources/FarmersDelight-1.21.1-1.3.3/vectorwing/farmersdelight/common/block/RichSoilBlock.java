package vectorwing.farmersdelight.common.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.network.PacketDistributor;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.network.payload.RichSoilBoostParticlesPayload;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.tag.ModTags;

public class RichSoilBlock extends Block {
   public RichSoilBlock(Properties properties) {
      super(properties);
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      BlockPos abovePos = pos.above();
      BlockState aboveState = level.getBlockState(abovePos);
      if (!this.convertMushroomToColony(aboveState, abovePos, level)) {
         tryBoostingPlantsAboveAndBelow(level, pos, random);
      }
   }

   public static void tryBoostingPlantsAboveAndBelow(ServerLevel level, BlockPos pos, RandomSource random) {
      if (Configuration.RICH_SOIL_BOOST_CHANCE.get() != 0.0 && !(random.nextFloat() > Configuration.RICH_SOIL_BOOST_CHANCE.get())) {
         BlockPos abovePos = pos.above();
         BlockState aboveState = level.getBlockState(abovePos);
         if (aboveState.is(ModTags.Blocks.PLANTED_FROM_BELOW) || !boostPlant(aboveState, abovePos, level)) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(ModTags.Blocks.PLANTED_FROM_BELOW)) {
               boostPlant(belowState, belowPos, level);
            }
         }
      }
   }

   public static boolean boostPlant(BlockState plantState, BlockPos plantPos, ServerLevel level) {
      if (plantState.is(ModTags.Blocks.UNAFFECTED_BY_RICH_SOIL)) {
         return false;
      } else if (plantState.getBlock() instanceof BonemealableBlock growable
         && growable.isValidBonemealTarget(level, plantPos, plantState)
         && CommonHooks.canCropGrow(level, plantPos, plantState, true)) {
         growable.performBonemeal(level, level.random, plantPos, plantState);
         PacketDistributor.sendToPlayersTrackingChunk(
            level, level.getChunkAt(plantPos).getPos(), new RichSoilBoostParticlesPayload(plantPos), new CustomPacketPayload[0]
         );
         CommonHooks.fireCropGrowPost(level, plantPos, plantState);
         return true;
      } else {
         return false;
      }
   }

   public boolean convertMushroomToColony(BlockState targetState, BlockPos targetPos, ServerLevel level) {
      if (targetState.is(Blocks.BROWN_MUSHROOM)) {
         level.setBlockAndUpdate(targetPos, ModBlocks.BROWN_MUSHROOM_COLONY.get().defaultBlockState());
         return true;
      } else if (targetState.is(Blocks.RED_MUSHROOM)) {
         level.setBlockAndUpdate(targetPos, ModBlocks.RED_MUSHROOM_COLONY.get().defaultBlockState());
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
      return toolAction.equals(ItemAbilities.HOE_TILL) && context.getLevel().getBlockState(context.getClickedPos().above()).isAir()
         ? ModBlocks.RICH_SOIL_FARMLAND.get().defaultBlockState()
         : null;
   }

   public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, BlockState plantState) {
      return TriState.DEFAULT;
   }
}
