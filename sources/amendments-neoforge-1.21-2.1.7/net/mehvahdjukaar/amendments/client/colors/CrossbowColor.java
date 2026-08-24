package net.mehvahdjukaar.amendments.client.colors;

import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ChargedProjectiles;

public class CrossbowColor implements ItemColor {
   public int getColor(ItemStack stack, int tint) {
      if (tint == 1 && ClientConfigs.COLORED_ARROWS.get()) {
         ChargedProjectiles projectiles = (ChargedProjectiles)stack.get(DataComponents.CHARGED_PROJECTILES);
         if (projectiles != null && !projectiles.isEmpty()) {
            ItemStack arrow = (ItemStack)projectiles.getItems().get(0);
            Item i = arrow.getItem();
            if (i == Items.TIPPED_ARROW) {
               PotionContents contents = (PotionContents)arrow.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
               return contents.getColor();
            }

            if (i == Items.SPECTRAL_ARROW) {
               return 16755200;
            }
         }
      }

      return -1;
   }
}
