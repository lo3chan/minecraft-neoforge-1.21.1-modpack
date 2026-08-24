package codx.codxlib.api.ui;

public enum HudAnchor {
   TOP_LEFT(0.0F, 0.0F),
   TOP_CENTER(0.5F, 0.0F),
   TOP_RIGHT(1.0F, 0.0F),
   MIDDLE_LEFT(0.0F, 0.5F),
   CENTER(0.5F, 0.5F),
   MIDDLE_RIGHT(1.0F, 0.5F),
   BOTTOM_LEFT(0.0F, 1.0F),
   BOTTOM_CENTER(0.5F, 1.0F),
   BOTTOM_RIGHT(1.0F, 1.0F);

   public final float fx;
   public final float fy;

   private HudAnchor(float fx, float fy) {
      this.fx = fx;
      this.fy = fy;
   }
}
