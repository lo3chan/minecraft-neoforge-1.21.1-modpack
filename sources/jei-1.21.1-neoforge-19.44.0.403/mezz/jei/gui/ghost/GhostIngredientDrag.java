package mezz.jei.gui.ghost;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec2;

public class GhostIngredientDrag<T> {
   private static final int targetColor = 1075038474;
   private static final int hoverColor = -2142451431;
   private final List<GhostIngredientDrag.HandlerData<T>> handlersData;
   private final IIngredientRenderer<T> ingredientRenderer;
   private final ITypedIngredient<T> ingredient;
   private final double mouseStartX;
   private final double mouseStartY;
   private final ImmutableRect2i origin;
   private final long dragCanStartTime;

   public GhostIngredientDrag(
      List<GhostIngredientDrag.HandlerData<T>> handlersData,
      IIngredientRenderer<T> ingredientRenderer,
      ITypedIngredient<T> ingredient,
      double mouseX,
      double mouseY,
      ImmutableRect2i origin
   ) {
      this.handlersData = handlersData;
      this.ingredientRenderer = ingredientRenderer;
      this.ingredient = ingredient;
      this.origin = origin;
      this.mouseStartX = mouseX;
      this.mouseStartY = mouseY;
      IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
      this.dragCanStartTime = System.currentTimeMillis() + clientConfig.dragDelayMs().getValue().intValue();
   }

   public void drawTargets(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      for (GhostIngredientDrag.HandlerData<T> data : this.handlersData) {
         IGhostIngredientHandler<?> handler = data.handler;
         if (handler.shouldHighlightTargets()) {
            drawTargets(guiGraphics, mouseX, mouseY, data.targetAreas);
         }
      }
   }

   public static boolean canStart(GhostIngredientDrag<?> drag, double mouseX, double mouseY) {
      if (System.currentTimeMillis() < drag.dragCanStartTime) {
         return false;
      } else {
         ImmutableRect2i origin = drag.getOrigin();
         Vec2 center;
         if (origin.isEmpty()) {
            center = new Vec2((float)drag.mouseStartX, (float)drag.mouseStartY);
         } else {
            if (origin.contains(mouseX, mouseY)) {
               return false;
            }

            center = new Vec2(origin.getX() + origin.getWidth() / 2.0F, origin.getY() + origin.getHeight() / 2.0F);
         }

         double mouseXDist = center.x - mouseX;
         double mouseYDist = center.y - mouseY;
         double mouseDistSq = mouseXDist * mouseXDist + mouseYDist * mouseYDist;
         return mouseDistSq > 64.0;
      }
   }

   public void drawItem(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      if (canStart(this, mouseX, mouseY)) {
         SafeIngredientUtil.render(guiGraphics, this.ingredientRenderer, this.ingredient, mouseX - 8, mouseY - 8);
      }
   }

   public static void drawTargets(GuiGraphics guiGraphics, int mouseX, int mouseY, List<Rect2i> targetAreas) {
      RenderSystem.disableDepthTest();

      for (Rect2i area : targetAreas) {
         int color;
         if (MathUtil.contains(area, (double)mouseX, (double)mouseY)) {
            color = -2142451431;
         } else {
            color = 1075038474;
         }

         guiGraphics.fill(RenderType.guiOverlay(), area.getX(), area.getY(), area.getX() + area.getWidth(), area.getY() + area.getHeight(), color);
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean onClick(UserInput input) {
      if (!canStart(this, input.getMouseX(), input.getMouseY())) {
         return false;
      } else {
         for (GhostIngredientDrag.HandlerData<T> data : this.handlersData) {
            for (IGhostIngredientHandler.Target<T> target : data.targets) {
               Rect2i area = target.getArea();
               if (MathUtil.contains(area, input.getMouseX(), input.getMouseY())) {
                  if (!input.isSimulate()) {
                     target.accept(this.ingredient.getIngredient());
                     data.handler.onComplete();
                  }

                  return true;
               }
            }

            if (!input.isSimulate()) {
               data.handler.onComplete();
            }
         }

         return false;
      }
   }

   public void stop() {
      for (GhostIngredientDrag.HandlerData<T> data : this.handlersData) {
         data.handler.onComplete();
      }
   }

   public IIngredientRenderer<T> getIngredientRenderer() {
      return this.ingredientRenderer;
   }

   public ITypedIngredient<T> getIngredient() {
      return this.ingredient;
   }

   public ImmutableRect2i getOrigin() {
      return this.origin;
   }

   public record HandlerData<T>(IGhostIngredientHandler<?> handler, List<IGhostIngredientHandler.Target<T>> targets, List<Rect2i> targetAreas) {
      public HandlerData(IGhostIngredientHandler<?> handler, List<IGhostIngredientHandler.Target<T>> targets) {
         this(handler, targets, targets.stream().map(IGhostIngredientHandler.Target::getArea).toList());
      }
   }
}
