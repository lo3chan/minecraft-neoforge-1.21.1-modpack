package fuzs.visualworkbench.world.level.block.entity;

import net.minecraft.world.item.ItemStack;

public interface WorkbenchVisualsProvider {
   ItemStack getCraftingResult();

   CraftingTableAnimationController getAnimationController();
}
