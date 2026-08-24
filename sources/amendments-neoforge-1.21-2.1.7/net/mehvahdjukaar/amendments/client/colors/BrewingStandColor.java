package net.mehvahdjukaar.amendments.client.colors;

import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BrewingStandColor implements BlockColor {
   public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tint) {
      if (tint >= 1 && tint <= 3) {
         if (world != null && pos != null && world.getBlockEntity(pos) instanceof BrewingStandBlockEntity br) {
            ItemStack item = br.getItem(tint - 1);
            PotionContents contents = (PotionContents)item.get(DataComponents.POTION_CONTENTS);
            if (contents == null) {
               return -1;
            } else {
               return !ClientConfigs.COLORED_BREWING_STAND.get() ? 16725044 : contents.getColor();
            }
         } else {
            return 16777215;
         }
      } else {
         return -1;
      }
   }
}
