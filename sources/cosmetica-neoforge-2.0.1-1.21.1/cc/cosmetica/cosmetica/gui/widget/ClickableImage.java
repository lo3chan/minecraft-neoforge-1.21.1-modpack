package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.PolyBuilder;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.PolyBuilder.Mode;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.maths.Region;

public class ClickableImage extends Image {
   private final ResourceKey texture;
   private final Runnable onClick;
   private boolean disabled = false;
   private float opacity = 1.0F;

   public ClickableImage(ResourceKey texture, Runnable onClick) {
      super(texture);
      this.texture = texture;
      this.onClick = onClick;
   }

   protected boolean canDrawDelete() {
      return true;
   }

   public ClickableImage setDisabled(boolean disabled) {
      this.disabled = disabled;
      return this;
   }

   public Image setTransparent(float opacity) {
      this.opacity = opacity;
      return super.setTransparent(opacity);
   }

   public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
      if (this.disabled) {
         canvas.setTransparency(1.0F);
         canvas.setTexture(this.texture);
         PolyBuilder builder = canvas.drawQuads(Mode.POSITION_COLOUR_TEXTURE);
         float shade = 0.4F;
         builder.vertex(region.getX(), region.getEndY(), 0.0).colour(0.4F, 0.4F, 0.4F, this.opacity).uv(0.0F, 1.0F).endVertex();
         builder.vertex(region.getEndX(), region.getEndY(), 0.0).colour(0.4F, 0.4F, 0.4F, this.opacity).uv(1.0F, 1.0F).endVertex();
         builder.vertex(region.getEndX(), region.getY(), 0.0).colour(0.4F, 0.4F, 0.4F, this.opacity).uv(1.0F, 0.0F).endVertex();
         builder.vertex(region.getX(), region.getY(), 0.0).colour(0.4F, 0.4F, 0.4F, this.opacity).uv(0.0F, 0.0F).endVertex();
         builder.build();
      } else if (region.contains(mouseX, mouseY)) {
         if (this.canDrawDelete()) {
            canvas.setTransparency(1.0F);
            canvas.setTexture(this.texture);
            PolyBuilder builder = canvas.drawQuads(Mode.POSITION_COLOUR_TEXTURE);
            builder.vertex(region.getX(), region.getEndY(), 0.0).colour(1.0F, 0.2F, 0.2F, this.opacity).uv(0.0F, 1.0F).endVertex();
            builder.vertex(region.getEndX(), region.getEndY(), 0.0).colour(1.0F, 0.2F, 0.2F, this.opacity).uv(1.0F, 1.0F).endVertex();
            builder.vertex(region.getEndX(), region.getY(), 0.0).colour(1.0F, 0.2F, 0.2F, this.opacity).uv(1.0F, 0.0F).endVertex();
            builder.vertex(region.getX(), region.getY(), 0.0).colour(1.0F, 0.2F, 0.2F, this.opacity).uv(0.0F, 0.0F).endVertex();
            builder.build();
         }
      } else {
         super.paint(canvas, region, mouseX, mouseY);
      }
   }

   public void mouseClicked(Element target, double x, double y, int button) {
      if (target.getComponent() == this && !this.disabled) {
         this.onClick.run();
      }
   }
}
