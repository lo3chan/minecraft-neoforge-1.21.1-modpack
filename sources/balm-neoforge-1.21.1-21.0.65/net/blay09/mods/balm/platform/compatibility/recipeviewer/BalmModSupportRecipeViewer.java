package net.blay09.mods.balm.platform.compatibility.recipeviewer;

import net.minecraft.resources.ResourceLocation;

public interface BalmModSupportRecipeViewer {
   void register(ResourceLocation var1, RecipeViewerInfoProvider var2);

   boolean hasKeyboardFocus();
}
