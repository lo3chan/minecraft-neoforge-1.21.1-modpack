package cn.foggyhillside.ends_delight.block;

import cn.foggyhillside.ends_delight.utility.Utils;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import vectorwing.farmersdelight.common.block.PieBlock;

public class ChorusFruitPieBlock extends PieBlock {
   public ChorusFruitPieBlock(Properties properties, Supplier<Item> pieSlice) {
      super(properties, pieSlice);
   }

   protected InteractionResult consumeBite(Level level, BlockPos pos, BlockState state, Player playerIn) {
      if (!playerIn.canEat(false)) {
         return InteractionResult.PASS;
      } else {
         if (!level.isClientSide && playerIn.isShiftKeyDown()) {
            Utils.BlockChorusFruitTeleport(level, playerIn);
         }

         return super.consumeBite(level, pos, state, playerIn);
      }
   }
}
