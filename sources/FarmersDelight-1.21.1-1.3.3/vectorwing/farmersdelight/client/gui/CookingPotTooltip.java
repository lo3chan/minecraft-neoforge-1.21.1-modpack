package vectorwing.farmersdelight.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class CookingPotTooltip implements ClientTooltipComponent {
   private static final int ITEM_SIZE = 16;
   private static final int MARGIN = 4;
   private final int textSpacing = 9 + 1;
   private final ItemStack mealStack;

   public CookingPotTooltip(CookingPotTooltip.CookingPotTooltipComponent tooltip) {
      this.mealStack = tooltip.mealStack;
   }

   public int getHeight() {
      return this.mealStack.isEmpty() ? this.textSpacing : this.textSpacing + 16;
   }

   public int getWidth(Font font) {
      if (!this.mealStack.isEmpty()) {
         MutableComponent textServingsOf = this.mealStack.getCount() == 1
            ? TextUtils.tooltip("cooking_pot.single_serving")
            : TextUtils.tooltip("cooking_pot.many_servings", this.mealStack.getCount());
         return Math.max(font.width(textServingsOf), font.width(this.mealStack.getHoverName()) + 20);
      } else {
         return font.width(TextUtils.tooltip("cooking_pot.empty"));
      }
   }

   public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics gui) {
      if (!this.mealStack.isEmpty()) {
         gui.renderItem(this.mealStack, mouseX, mouseY + this.textSpacing, 0);
      }
   }

   public void renderText(Font font, int x, int y, Matrix4f matrix4f, BufferSource bufferSource) {
      Integer color = ChatFormatting.GRAY.getColor();
      int gray = color == null ? -1 : color;
      if (!this.mealStack.isEmpty()) {
         MutableComponent textServingsOf = this.mealStack.getCount() == 1
            ? TextUtils.tooltip("cooking_pot.single_serving")
            : TextUtils.tooltip("cooking_pot.many_servings", this.mealStack.getCount());
         font.drawInBatch(textServingsOf, x, y, gray, true, matrix4f, bufferSource, DisplayMode.NORMAL, 0, 15728880);
         font.drawInBatch(
            this.mealStack.getHoverName(), x + 16 + 4, y + this.textSpacing + 4, -1, true, matrix4f, bufferSource, DisplayMode.NORMAL, 0, 15728880
         );
      } else {
         MutableComponent textEmpty = TextUtils.tooltip("cooking_pot.empty");
         font.drawInBatch(textEmpty, x, y, gray, true, matrix4f, bufferSource, DisplayMode.NORMAL, 0, 15728880);
      }
   }

   public record CookingPotTooltipComponent(ItemStack mealStack) implements TooltipComponent {
   }
}
