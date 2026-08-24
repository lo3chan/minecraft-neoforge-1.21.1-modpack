package codx.codxlib.api.ui;

public final class HudPosition {
   public HudAnchor anchor = HudAnchor.TOP_LEFT;
   public int offsetX = 0;
   public int offsetY = 0;

   public HudPosition() {
   }

   public HudPosition(HudAnchor anchor, int offsetX, int offsetY) {
      this.anchor = anchor;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
   }

   public int x(int screenWidth, int elementWidth) {
      return Math.round(screenWidth * this.anchor.fx - elementWidth * this.anchor.fx) + this.offsetX;
   }

   public int y(int screenHeight, int elementHeight) {
      return Math.round(screenHeight * this.anchor.fy - elementHeight * this.anchor.fy) + this.offsetY;
   }

   public void setFromTopLeft(int x, int y, int screenWidth, int screenHeight, int elementWidth, int elementHeight, HudAnchor anchor) {
      this.anchor = anchor;
      this.offsetX = x - Math.round(screenWidth * anchor.fx - elementWidth * anchor.fx);
      this.offsetY = y - Math.round(screenHeight * anchor.fy - elementHeight * anchor.fy);
   }
}
