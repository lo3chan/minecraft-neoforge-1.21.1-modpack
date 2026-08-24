package vectorwing.farmersdelight.common.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.Block;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class PlaceableItem extends BlockItem {
   public PlaceableItem(Block block, Properties properties) {
      super(block, properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
      tooltip.add(TextUtils.PLACEABLE);
   }
}
