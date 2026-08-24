package com.anthonyhilyard.legendarytooltips.compat;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public final class RelicsHandler {
   public static boolean hasTooltipDecor(ItemStack itemStack, LocalPlayer player) {
      return itemStack.getItem() instanceof IRelicItem relic && ((TooltipData)relic.getStyleData().getTooltip().apply(player, itemStack)).isTextured();
   }
}
