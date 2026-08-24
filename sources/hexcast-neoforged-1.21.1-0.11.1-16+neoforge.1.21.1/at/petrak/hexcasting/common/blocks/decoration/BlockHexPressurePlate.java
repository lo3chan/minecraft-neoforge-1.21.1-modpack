package at.petrak.hexcasting.common.blocks.decoration;

import at.petrak.hexcasting.common.lib.HexBlockSetTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class BlockHexPressurePlate extends PressurePlateBlock {
   public BlockHexPressurePlate(BlockSetType $$0, Properties $$1) {
      super(HexBlockSetTypes.EDIFIED_WOOD, $$1);
   }

   public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return true;
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 20;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 5;
   }
}
