package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.SizedElement;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class IconButton extends Button {
   private final ResourceKey texture;
   private final IconButton.MouseMotionListener onMouseMoved;
   private static final Dimensions DEFAULT_DIMENSIONS = new Dimensions(20, 20);

   public IconButton(ResourceKey texture, Runnable onClicked) {
      this(texture, onClicked, null);
   }

   public IconButton(ResourceKey texture, Runnable onClicked, @Nullable IconButton.MouseMotionListener onMouseMoved) {
      super(Text.literal(""), onClicked);
      this.texture = texture;
      this.onMouseMoved = onMouseMoved;
   }

   public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
      return this.tryFixed(DEFAULT_DIMENSIONS, padding, context);
   }

   public void mouseMoved(Region region, double x, double y) {
      if (this.onMouseMoved != null) {
         this.onMouseMoved.accept(region, x, y);
      }
   }

   public void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
      super.paint(canvas, region, mouseX, mouseY);
      canvas.setTransparency(this.disabled ? 0.8F : 1.0F);
      canvas.drawTexture(region.getX(), region.getY(), region.getWidth(), region.getHeight(), 0.0F, this.texture);
      canvas.disableTransparency();
   }

   @FunctionalInterface
   public interface MouseMotionListener {
      void accept(Region var1, double var2, double var4);
   }
}
