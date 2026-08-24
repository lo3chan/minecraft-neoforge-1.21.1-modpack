package top.theillusivec4.curios.api.extensions;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;

public interface ICurioSlotExtension {
   ICurioSlotExtension DEFAULT = new ICurioSlotExtension() {};

   static ICurioSlotExtension from(String id) {
      return CuriosExtensions.SLOT_EXTENSIONS.getOrDefault(id, DEFAULT);
   }

   default ItemStack getDisplayStack(SlotContext slotContext, ItemStack defaultStack) {
      return defaultStack;
   }

   default ItemStack getCloneStack(SlotContext slotContext, ItemStack defaultStack) {
      return defaultStack;
   }

   default List<Component> getSlotTooltip(SlotContext slotContext, TooltipFlag tooltipFlag) {
      return List.of();
   }
}
