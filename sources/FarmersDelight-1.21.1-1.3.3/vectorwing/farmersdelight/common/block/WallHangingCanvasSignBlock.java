package vectorwing.farmersdelight.common.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;
import vectorwing.farmersdelight.common.block.state.CanvasSign;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

public class WallHangingCanvasSignBlock extends WallHangingSignBlock implements CanvasSign {
   private final DyeColor backgroundColor;

   public WallHangingCanvasSignBlock(Properties properties, @Nullable DyeColor backgroundColor) {
      super(WoodType.SPRUCE, properties);
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
