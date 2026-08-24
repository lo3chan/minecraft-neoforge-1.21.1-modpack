package com.aetherteam.aether.block.natural;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public class AetherDoubleDropsOreBlock extends DropExperienceBlock {
   public AetherDoubleDropsOreBlock(UniformInt xpRange, Properties properties) {
      super(xpRange, properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AetherBlockStateProperties.DOUBLE_DROPS});
   }
}
