package net.mcreator.undeadrevamp.block;

import net.mcreator.undeadrevamp.procedures.ArapholiaClientDisplayRandomTickProcedure;
import net.mcreator.undeadrevamp.procedures.ArapholiaEntityWalksOnTheBlockProcedure;
import net.mcreator.undeadrevamp.procedures.ArapholiaUpdateTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ArapholiaBlock extends FlowerBlock {
   public ArapholiaBlock() {
      super(
         MobEffects.GLOWING,
         450.0F,
         Properties.of()
            .mapColor(MapColor.PLANT)
            .randomTicks()
            .sound(SoundType.GRASS)
            .instabreak()
            .lightLevel(s -> 2)
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

   public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
      ArapholiaUpdateTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
      super.animateTick(blockstate, world, pos, random);
      ArapholiaClientDisplayRandomTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }

   public void stepOn(Level world, BlockPos pos, BlockState blockstate, Entity entity) {
      super.stepOn(world, pos, blockstate, entity);
      ArapholiaEntityWalksOnTheBlockProcedure.execute(entity);
   }
}
