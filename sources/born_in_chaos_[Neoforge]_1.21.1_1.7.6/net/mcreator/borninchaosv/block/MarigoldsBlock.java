package net.mcreator.borninchaosv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class MarigoldsBlock extends FlowerBlock {
   public MarigoldsBlock() {
      super(
         MobEffects.HEALTH_BOOST,
         100.0F,
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
}
