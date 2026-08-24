package mezz.jei.api.ingredients;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public interface IIngredientRenderer<T> {
   void render(GuiGraphics var1, T var2);

   default void render(GuiGraphics guiGraphics, T ingredient, int posX, int posY) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.translate(posX, posY, 0.0F);
      this.render(guiGraphics, ingredient);
      poseStack.popPose();
   }

   default void renderBatch(GuiGraphics guiGraphics, List<BatchRenderElement<T>> elements) {
      for (BatchRenderElement<T> element : elements) {
         this.render(guiGraphics, element.ingredient(), element.x(), element.y());
      }
   }

   List<Component> getTooltip(T var1, TooltipFlag var2);

   default void getTooltip(ITooltipBuilder tooltip, T ingredient, TooltipFlag tooltipFlag) {
      List<Component> components = this.getTooltip(ingredient, tooltipFlag);
      tooltip.addAll(components);
   }

   default Font getFontRenderer(Minecraft minecraft, T ingredient) {
      return minecraft.font;
   }

   default int getWidth() {
      return 16;
   }

   default int getHeight() {
      return 16;
   }
}
