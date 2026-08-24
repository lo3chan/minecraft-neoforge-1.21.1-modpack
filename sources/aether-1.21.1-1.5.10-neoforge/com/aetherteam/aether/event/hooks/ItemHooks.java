package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.AetherItems;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemHooks {
   public static void addDungeonTooltips(List<Component> components, ItemStack stack, TooltipFlag flag) {
      if (flag.isCreative()) {
         int position = components.size();
         Component itemName = stack.getItem().getName(stack);

         for (int i = 0; i < position; i++) {
            Component component = components.get(i);
            if (component.getString().equals(itemName.getString())) {
               position = i + 1;
               break;
            }
         }

         if (stack.is(AetherTags.Items.BRONZE_DUNGEON_LOOT)) {
            components.add(position, AetherItems.BRONZE_DUNGEON_TOOLTIP);
         }

         if (stack.is(AetherTags.Items.SILVER_DUNGEON_LOOT)) {
            components.add(position, AetherItems.SILVER_DUNGEON_TOOLTIP);
         }

         if (stack.is(AetherTags.Items.GOLD_DUNGEON_LOOT)) {
            components.add(position, AetherItems.GOLD_DUNGEON_TOOLTIP);
         }
      }
   }
}
