package net.blay09.mods.balm.platform.compatibility.recipeviewer;

public interface RecipeViewerDisplaySlotsBuilder {
   RecipeViewerDisplaySlotBuilder inputSlot(int var1, int var2);

   RecipeViewerDisplaySlotBuilder outputSlot(int var1, int var2);

   RecipeViewerDisplaySlotBuilder craftingStationSlot(int var1, int var2);

   RecipeViewerDisplaySlotBuilder renderOnlySlot(int var1, int var2);
}
