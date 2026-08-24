package com.aetherteam.aether.block.natural;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public class AetherDoubleDropBlock extends Block {
   public AetherDoubleDropBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AetherBlockStateProperties.DOUBLE_DROPS});
   }
}
