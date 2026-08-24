package mezz.jei.api.gui.placement;

public interface IPlaceable<THIS extends IPlaceable<THIS>> {
   THIS setPosition(int var1, int var2);

   default THIS setPosition(int areaX, int areaY, int areaWidth, int areaHeight, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
      int x = areaX + horizontalAlignment.getXPos(areaWidth, this.getWidth());
      int y = areaY + verticalAlignment.getYPos(areaHeight, this.getHeight());
      return this.setPosition(x, y);
   }

   int getWidth();

   int getHeight();
}
