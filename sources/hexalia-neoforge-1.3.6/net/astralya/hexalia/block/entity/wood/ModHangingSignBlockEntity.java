package net.astralya.hexalia.block.entity.wood;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModHangingSignBlockEntity extends SignBlockEntity {
   public ModHangingSignBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.MOD_HANGING_SIGN.get(), pos, state);
   }

   public BlockEntityType<?> getType() {
      return (BlockEntityType<?>)ModBlockEntityTypes.MOD_HANGING_SIGN.get();
   }
}
