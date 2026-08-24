package vectorwing.farmersdelight.common.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;
import vectorwing.farmersdelight.common.block.state.CanvasSign;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

public class CeilingHangingCanvasSignBlock extends CeilingHangingSignBlock implements CanvasSign {
   private final DyeColor backgroundColor;

   public CeilingHangingCanvasSignBlock(@Nullable DyeColor backgroundColor) {
      super(WoodType.SPRUCE, Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
      this.backgroundColor = backgroundColor;
   }

   @Nullable
   @Override
   public DyeColor getBackgroundColor() {
      return this.backgroundColor;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlockEntityTypes.HANGING_CANVAS_SIGN.get().create(pos, state);
   }
}
