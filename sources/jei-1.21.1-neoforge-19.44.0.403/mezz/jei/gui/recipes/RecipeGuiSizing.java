package mezz.jei.gui.recipes;

final class RecipeGuiSizing {
   private RecipeGuiSizing() {
   }

   static RecipeGuiSizing.Size calculateInitialSize(int screenHeight, boolean centerSearchBarEnabled, int maxHeight) {
      int ySize;
      if (centerSearchBarEnabled) {
         ySize = screenHeight - 76;
      } else {
         ySize = screenHeight - 58;
      }

      if (ySize < 175) {
         ySize = 175;
      }

      int extraSpace = 0;
      if (ySize > maxHeight) {
         extraSpace = ySize - maxHeight;
         ySize = maxHeight;
      }

      return new RecipeGuiSizing.Size(ySize, extraSpace);
   }

   record Size(int ySize, int extraSpace) {
   }
}
