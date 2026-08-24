package mezz.jei.api.gui.placement;

public enum HorizontalAlignment {
   LEFT {
      @Override
      public int getXPos(int availableWidth, int elementWidth) {
         return 0;
      }
   },
   CENTER {
      @Override
      public int getXPos(int availableWidth, int elementWidth) {
         return Math.round((availableWidth - elementWidth) / 2.0F);
      }
   },
   RIGHT {
      @Override
      public int getXPos(int availableWidth, int elementWidth) {
         return availableWidth - elementWidth;
      }
   };

   public abstract int getXPos(int var1, int var2);
}
