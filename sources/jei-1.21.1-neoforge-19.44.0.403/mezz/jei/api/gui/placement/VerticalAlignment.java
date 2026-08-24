package mezz.jei.api.gui.placement;

public enum VerticalAlignment {
   TOP {
      @Override
      public int getYPos(int availableHeight, int elementHeight) {
         return 0;
      }
   },
   CENTER {
      @Override
      public int getYPos(int availableHeight, int elementHeight) {
         return Math.round((availableHeight - elementHeight) / 2.0F);
      }
   },
   BOTTOM {
      @Override
      public int getYPos(int availableHeight, int elementHeight) {
         return availableHeight - elementHeight;
      }
   };

   public abstract int getYPos(int var1, int var2);
}
