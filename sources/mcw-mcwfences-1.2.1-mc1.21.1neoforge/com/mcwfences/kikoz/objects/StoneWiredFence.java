package com.mcwfences.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;

public class StoneWiredFence extends FenceBlock {
   public StoneWiredFence(Properties properties) {
      super(Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(1.5F).explosionResistance(2.5F));
   }

   public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
      entityIn.hurt(worldIn.damageSources().cactus(), 4.0F);
   }
}
