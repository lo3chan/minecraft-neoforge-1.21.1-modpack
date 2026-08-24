package net.irisshaders.iris.api.v0.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public interface IrisItemLightProvider {
   Vector3f DEFAULT_LIGHT_COLOR = new Vector3f(1.0F, 1.0F, 1.0F);

   default int getLightEmission(Player player, ItemStack stack) {
      if (stack.getItem() instanceof BlockItem item) {
         BlockState blockState = item.getBlock().defaultBlockState();
         BlockItemStateProperties itemBlockState = (BlockItemStateProperties)stack.getComponents().get(DataComponents.BLOCK_STATE);
         if (itemBlockState != null) {
            blockState = itemBlockState.apply(blockState);
         }

         return blockState.getLightEmission();
      } else {
         return 0;
      }
   }

   default Vector3f getLightColor(Player player, ItemStack stack) {
      return DEFAULT_LIGHT_COLOR;
   }
}
