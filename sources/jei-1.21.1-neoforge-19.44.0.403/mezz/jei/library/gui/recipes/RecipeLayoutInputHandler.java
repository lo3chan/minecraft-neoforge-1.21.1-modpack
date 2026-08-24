package mezz.jei.library.gui.recipes;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.util.MathUtil;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.Rect2i;

public class RecipeLayoutInputHandler<T> implements IJeiInputHandler {
   private final RecipeLayout<T> recipeLayout;
   private final List<IJeiInputHandler> inputHandlers;
   private final List<IJeiGuiEventListener> guiEventListeners;

   public RecipeLayoutInputHandler(RecipeLayout<T> recipeLayout) {
      this.recipeLayout = recipeLayout;
      this.inputHandlers = new ArrayList<>();
      this.guiEventListeners = new ArrayList<>();
   }

   @Override
   public ScreenRectangle getArea() {
      Rect2i area = this.recipeLayout.getRect();
      return new ScreenRectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
   }

   @Override
   public boolean handleInput(double mouseX, double mouseY, IJeiUserInput userInput) {
      if (!this.recipeLayout.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
         Rect2i area = this.recipeLayout.getRect();
         double recipeMouseX = mouseX - area.getX();
         double recipeMouseY = mouseY - area.getY();

         for (IJeiInputHandler inputHandler : this.inputHandlers) {
            ScreenRectangle widgetArea = inputHandler.getArea();
            if (MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) {
               ScreenPosition position = widgetArea.position();
               double relativeMouseX = recipeMouseX - position.x();
               double relativeMouseY = recipeMouseY - position.y();
               if (inputHandler.handleInput(relativeMouseX, relativeMouseY, userInput)) {
                  return true;
               }
            }
         }

         for (IJeiGuiEventListener guiEventListener : this.guiEventListeners) {
            ScreenRectangle widgetArea = guiEventListener.getArea();
            if (MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) {
               ScreenPosition position = widgetArea.position();
               double relativeMouseX = recipeMouseX - position.x();
               double relativeMouseY = recipeMouseY - position.y();
               if (handleInput(guiEventListener, relativeMouseX, relativeMouseY, userInput)) {
                  return true;
               }
            }
         }

         if (userInput.isSimulate()) {
            return true;
         } else {
            IRecipeCategory<T> recipeCategory = this.recipeLayout.getRecipeCategory();
            T recipe = this.recipeLayout.getRecipe();
            return recipeCategory.handleInput(recipe, recipeMouseX, recipeMouseY, userInput.getKey());
         }
      }
   }

   private static boolean handleInput(IJeiGuiEventListener guiEventListener, double relativeMouseX, double relativeMouseY, IJeiUserInput userInput) {
      Key key = userInput.getKey();
      switch (key.getType()) {
         case MOUSE:
            if (userInput.isSimulate()) {
               return guiEventListener.mouseClicked(relativeMouseX, relativeMouseY, key.getValue());
            }

            return guiEventListener.mouseReleased(relativeMouseX, relativeMouseY, key.getValue());
         case KEYSYM:
            if (!userInput.isSimulate()) {
               return guiEventListener.keyPressed(relativeMouseX, relativeMouseY, key.getValue(), 0, userInput.getModifiers());
            }

            return false;
         default:
            return false;
      }
   }

   @Override
   public boolean handleMouseDragged(double mouseX, double mouseY, Key mouseKey, double dragX, double dragY) {
      if (!this.recipeLayout.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
         Rect2i area = this.recipeLayout.getRect();
         double recipeMouseX = mouseX - area.getX();
         double recipeMouseY = mouseY - area.getY();

         for (IJeiInputHandler inputHandler : this.inputHandlers) {
            ScreenRectangle widgetArea = inputHandler.getArea();
            if (MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) {
               ScreenPosition position = widgetArea.position();
               double relativeMouseX = recipeMouseX - position.x();
               double relativeMouseY = recipeMouseY - position.y();
               if (inputHandler.handleMouseDragged(relativeMouseX, relativeMouseY, mouseKey, dragX, dragY)) {
                  return true;
               }
            }
         }

         for (IJeiGuiEventListener guiEventListener : this.guiEventListeners) {
            ScreenRectangle widgetArea = guiEventListener.getArea();
            if (MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) {
               ScreenPosition position = widgetArea.position();
               double relativeMouseX = recipeMouseX - position.x();
               double relativeMouseY = recipeMouseY - position.y();
               if (guiEventListener.mouseDragged(relativeMouseX, relativeMouseY, mouseKey.getValue(), dragX, dragY)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
      if (!this.recipeLayout.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
         Rect2i area = this.recipeLayout.getRect();
         double recipeMouseX = mouseX - area.getX();
         double recipeMouseY = mouseY - area.getY();

         for (IJeiInputHandler inputHandler : this.inputHandlers) {
            ScreenRectangle widgetArea = inputHandler.getArea();
            if (MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) {
               ScreenPosition position = widgetArea.position();
               double relativeMouseX = recipeMouseX - position.x();
               double relativeMouseY = recipeMouseY - position.y();
               if (inputHandler.handleMouseScrolled(relativeMouseX, relativeMouseY, scrollDeltaX, scrollDeltaY)) {
                  return true;
               }
            }
         }

         for (IJeiGuiEventListener guiEventListener : this.guiEventListeners) {
            ScreenRectangle widgetArea = guiEventListener.getArea();
            if (MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) {
               ScreenPosition position = widgetArea.position();
               double relativeMouseX = recipeMouseX - position.x();
               double relativeMouseY = recipeMouseY - position.y();
               if (guiEventListener.mouseScrolled(relativeMouseX, relativeMouseY, scrollDeltaX, scrollDeltaY)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public void handleMouseMoved(double mouseX, double mouseY) {
      if (this.recipeLayout.isMouseOver(mouseX, mouseY)) {
         Rect2i area = this.recipeLayout.getRect();
         double recipeMouseX = mouseX - area.getX();
         double recipeMouseY = mouseY - area.getY();

         for (IJeiInputHandler inputHandler : this.inputHandlers) {
            ScreenRectangle widgetArea = inputHandler.getArea();
            if (MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) {
               ScreenPosition position = widgetArea.position();
               double relativeMouseX = recipeMouseX - position.x();
               double relativeMouseY = recipeMouseY - position.y();
               inputHandler.handleMouseMoved(relativeMouseX, relativeMouseY);
            }
         }

         for (IJeiGuiEventListener guiEventListener : this.guiEventListeners) {
            ScreenRectangle widgetArea = guiEventListener.getArea();
            if (MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) {
               ScreenPosition position = widgetArea.position();
               double relativeMouseX = recipeMouseX - position.x();
               double relativeMouseY = recipeMouseY - position.y();
               guiEventListener.mouseMoved(relativeMouseX, relativeMouseY);
            }
         }
      }
   }

   public void addInputHandler(IJeiInputHandler inputHandler) {
      this.inputHandlers.add(inputHandler);
   }

   public void addGuiEventListener(IJeiGuiEventListener guiEventListener) {
      this.guiEventListeners.add(guiEventListener);
   }
}
