package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.PointerEvents;
import cc.cosmetica.kupe.api.gui.SizedElement;
import cc.cosmetica.kupe.api.gui.TextBox;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.RootStylesheet;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import cc.cosmetica.kupe.impl.MinecraftBuiltinComponent;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;

public class SliderWidget extends MinecraftBuiltinComponent {
   private final State<Float> value;
   private final Function<Float, Text> textFunction;
   private final float precision;
   private AbstractSliderButton cache;
   private boolean drag = false;
   int prevMouseX = 0;
   private static final Dimensions DEFAULT_DIMENSIONS = new Dimensions(200, 20);

   public SliderWidget(State<Float> value, float precision, Function<Float, Text> textFunction) {
      this.value = value;
      this.textFunction = textFunction;
      this.precision = precision;
   }

   private float snapToPrecision(float f) {
      return this.precision == 0.0F ? f : Math.round(f / this.precision) * this.precision;
   }

   public AbstractWidget createMinecraftWidget(Region region, Context context) {
      float f = (Float)this.value.acquire(this);
      return this.drag
         ? this.cache
         : (
            this.cache = new AbstractSliderButton(
               region.getX(), region.getY(), region.getWidth(), region.getHeight(), this.textFunction.apply(this.snapToPrecision(f)).toMinecraftComponent(), f
            ) {
               protected void updateMessage() {
                  float nf = (Float)SliderWidget.this.value.peek();
                  this.setMessage(SliderWidget.this.textFunction.apply(nf).toMinecraftComponent());
               }

               protected void applyValue() {
                  float newValue = SliderWidget.this.snapToPrecision((float)this.value);
                  if ((Float)SliderWidget.this.value.peek() != newValue) {
                     SliderWidget.this.value.set(newValue);
                  }
               }
            }
         );
   }

   public SliderWidget setDisabled(boolean disabled) {
      return (SliderWidget)super.setDisabled(disabled);
   }

   public void mouseClicked(Element target, double x, double y, int button) {
      super.mouseClicked(target, x, y, button);
      if (target.getComponent() == this) {
         this.drag = true;
      }
   }

   public void render(Canvas canvas, Region region, Margins padding, int mouseX, int mouseY) {
      super.render(canvas, region, padding, mouseX, mouseY);
      if (this.drag && mouseX != this.prevMouseX) {
         this.minecraftWidget.mouseDragged(mouseX, mouseY, 0, 0.0, 0.0);
      }

      this.prevMouseX = mouseX;
   }

   public void mouseReleased(double x, double y, int button) {
      super.mouseReleased(x, y, button);
      this.drag = false;
   }

   public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
      return this.tryFixed(DEFAULT_DIMENSIONS, padding, context);
   }

   static {
      RootStylesheet.setDefaultOverrides(TextBox.class, Style.create().set(CommonProperties.POINTER_EVENTS, PointerEvents.ALL));
   }
}
