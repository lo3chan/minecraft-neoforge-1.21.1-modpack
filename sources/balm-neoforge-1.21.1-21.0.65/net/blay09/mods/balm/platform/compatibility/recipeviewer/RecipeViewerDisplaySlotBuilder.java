package net.blay09.mods.balm.platform.compatibility.recipeviewer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public interface RecipeViewerDisplaySlotBuilder {
   RecipeViewerDisplaySlotBuilder add(Ingredient var1);

   RecipeViewerDisplaySlotBuilder add(ItemStack var1);

   RecipeViewerDisplaySlotBuilder add(ItemLike var1);

   RecipeViewerDisplaySlotBuilder withSlotBackground();

   RecipeViewerDisplaySlotBuilder withOutputSlotBackground();
}
