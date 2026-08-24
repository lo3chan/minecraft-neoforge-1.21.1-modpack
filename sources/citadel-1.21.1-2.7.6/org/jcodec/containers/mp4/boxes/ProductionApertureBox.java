package org.jcodec.containers.mp4.boxes;

public class ProductionApertureBox extends ClearApertureBox {
   public static final String PROF = "prof";

   public static ProductionApertureBox createProductionApertureBox(int width, int height) {
      ProductionApertureBox prof = new ProductionApertureBox(new Header("prof"));
      prof.width = width;
      prof.height = height;
      return prof;
   }

   public ProductionApertureBox(Header atom) {
      super(atom);
   }
}
