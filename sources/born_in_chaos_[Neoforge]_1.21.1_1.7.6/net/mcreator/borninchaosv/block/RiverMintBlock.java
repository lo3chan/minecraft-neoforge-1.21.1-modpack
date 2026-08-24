package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.RiverMintPriIspolzovaniiKostnoiMukiProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class RiverMintBlock extends FlowerBlock implements BonemealableBlock {
   public RiverMintBlock() {
      super(
         MobEffects.CONDUIT_POWER,
         800.0F,
         Properties.of()
            .mapColor(MapColor.PLANT)
            .sound(SoundType.AZALEA_LEAVES)
            .instabreak()
            .noCollission()
            .offsetType(OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY)
      );
   }

   public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
      return 100;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
      return 60;
   }

   public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState blockstate) {
      return true;
   }

   public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState blockstate) {
      return true;
   }

   public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState blockstate) {
      RiverMintPriIspolzovaniiKostnoiMukiProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
