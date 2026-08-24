package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.cosmetica.gui.GuiUtils;
import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.Context;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.SizedElement;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.util.Mth;

public class SlideToggle extends Component {
   private final State<Boolean> state;
   private final Text leftLabel;
   private final Text rightLabel;
   private float ease = 1.0F;
   private long time = System.currentTimeMillis();
   private static final int KNOB_WIDTH = 18;
   private static final int BUTTON_WIDTH = 38;

   public SlideToggle(State<Boolean> state, Text leftLabel, Text rightLabel) {
      this.state = state;
      this.leftLabel = leftLabel;
      this.rightLabel = rightLabel;
   }

   public Dimensions minimumSize(List<? extends SizedElement> children, Margins padding, int vw, int vh) {
      return new Dimensions(38, 20);
   }

   public Dimensions intrinsicSize(List<? extends SizedElement> children, Margins padding, Context context) {
      return new Dimensions(38, 20);
   }

   public List<Component> build() {
      return ImmutableList.of();
   }

   public void mouseClicked(Element target, double x, double y, int button) {
      if (target.getComponent() == this) {
         GuiUtils.playClick();
         this.state.set(!(Boolean)this.state.peek());
         this.ease = 1.0F - this.ease;
      }
   }

   protected void paint(Canvas canvas, Region region, int mouseX, int mouseY) {
      int centreX = region.getX() + region.getWidth() / 2;
      int centreY = region.getY() + region.getHeight() / 2;
      int y = region.getY();
      int x0 = centreX - 19;
      int lengthLeft = canvas.getDrawingContext().getWidth(this.leftLabel);
      int textHeight = canvas.getDrawingContext().getLineHeight();
      canvas.drawText(this.leftLabel, x0 - lengthLeft - 1, centreY - textHeight / 2, 16777215);
      canvas.drawText(this.rightLabel, centreX + 19 + 2, centreY - textHeight / 2, 16777215);
      float btn = 0.44F;
      float highlight = 0.65F;
      float shade = 0.34F;
      canvas.drawRect(x0, y, 38, region.getHeight(), 0.0F, 0.0F, 0.0F, 0.0F);
      boolean b = (Boolean)this.state.peek();
      int miniBoxX = x0 + 1;
      if (b) {
         miniBoxX += (int)Mth.lerp(this.ease, 0.0F, 18.0F);
      } else {
         miniBoxX += (int)Mth.lerp(this.ease, 18.0F, 0.0F);
      }

      canvas.drawRect(miniBoxX, y + 1, 18, region.getHeight() - 2, 0.0F, 0.44F, 0.44F, 0.44F);
      canvas.drawRect(miniBoxX, y + 1, 17, 1, 0.0F, 0.65F, 0.65F, 0.65F);
      canvas.drawRect(miniBoxX, y + 1, 1, region.getHeight() - 3, 0.0F, 0.65F, 0.65F, 0.65F);
      canvas.drawRect(miniBoxX + 1, region.getFinalY() - 1, 17, 1, 0.0F, 0.34F, 0.34F, 0.34F);
      canvas.drawRect(miniBoxX + 18 - 1, y + 2, 1, region.getHeight() - 3, 0.0F, 0.34F, 0.34F, 0.34F);
      long newTime = System.currentTimeMillis();
      if (this.ease < 1.0F) {
         this.ease = Math.min(1.0F, this.ease + (float)(newTime - this.time) * 0.001F / 0.1F);
      }

      this.time = newTime;
   }
}
