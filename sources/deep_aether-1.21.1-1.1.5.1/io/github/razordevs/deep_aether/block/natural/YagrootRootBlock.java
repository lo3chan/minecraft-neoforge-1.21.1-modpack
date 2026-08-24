package io.github.razordevs.deep_aether.block.natural;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MangroveRootsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public class YagrootRootBlock extends MangroveRootsBlock {
   public YagrootRootBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{AetherBlockStateProperties.DOUBLE_DROPS});
   }
}
