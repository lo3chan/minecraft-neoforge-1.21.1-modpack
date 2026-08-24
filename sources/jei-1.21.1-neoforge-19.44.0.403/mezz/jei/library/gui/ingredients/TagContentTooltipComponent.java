package mezz.jei.library.gui.ingredients;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.common.util.MathUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class TagContentTooltipComponent<T> implements ClientTooltipComponent, TooltipComponent {
   private static final int MAX_PER_LINE = 10;
   private static final int MAX_LINES = 3;
   private static final int MAX_INGREDIENTS = 30;
   private static final int INGREDIENT_SIZE = 18;
   private static final int INGREDIENT_PADDING = 1;
   private final IIngredientRenderer<T> renderer;
   private final List<T> ingredients;

   public TagContentTooltipComponent(IIngredientRenderer<T> renderer, List<T> ingredients) {
      this.renderer = renderer;
      this.ingredients = ingredients;
   }

   public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
      if (this.ingredients.size() <= 30) {
         this.drawIngredients(guiGraphics, x, y, this.ingredients.size());
      } else {
         int drawCount = 29;
         this.drawIngredients(guiGraphics, x, y, 29);
         if (this.ingredients.size() > 30) {
            int remainingCount = Math.min(this.ingredients.size() - 29, 99);
            String countString = "+" + remainingCount;
            int textHeight = 9 - 1;
            int textWidth = font.width(countString);
            int textCenterX = x + 162 + (18 - textWidth) / 2;
            int textCenterY = y + 36 + (18 - textHeight) / 2;
            guiGraphics.drawString(font, countString, textCenterX, textCenterY, -5592406);
         }
      }
   }

   private void drawIngredients(GuiGraphics guiGraphics, int x, int y, int maxIngredients) {
      int maxPerLine = MathUtil.divideCeil(maxIngredients, this.getLineCount());

      for (int i = 0; i < this.ingredients.size() && i < maxIngredients; i++) {
         int column = i % maxPerLine;
         int row = i / maxPerLine;
         PoseStack poseStack = guiGraphics.pose();
         poseStack.pushPose();
         poseStack.translate(x + column * 18 + 1, y + row * 18 + 1, 0.0);
         this.renderer.render(guiGraphics, this.ingredients.get(i));
         poseStack.popPose();
      }
   }

   private int getLineCount() {
      int lineCount = MathUtil.divideCeil(this.ingredients.size(), 10);
      return Math.min(lineCount, 3);
   }

   private int getMaxPerLine() {
      int perLine = MathUtil.divideCeil(this.ingredients.size(), this.getLineCount());
      return Math.min(perLine, 10);
   }

   public int getHeight() {
      return this.getLineCount() * 18 + 2;
   }

   public int getWidth(Font font) {
      return this.getMaxPerLine() * 18 + 2;
   }
}
