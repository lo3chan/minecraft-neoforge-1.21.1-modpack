package mezz.jei.gui.recipes;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.input.ClickableIngredientInternal;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import mezz.jei.gui.input.handlers.NullInputHandler;
import mezz.jei.gui.input.handlers.ProxyInputHandler;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.elements.IngredientElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class RecipeGuiLayouts {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<IRecipeLayoutWithButtons<?>> recipeLayoutsWithButtons = new ArrayList<>();
   @Nullable
   private IUserInputHandler cachedInputHandler = NullInputHandler.INSTANCE;

   public void updateLayout(ImmutableRect2i recipeLayoutsArea, int recipesPerPage) {
      if (!this.recipeLayoutsWithButtons.isEmpty()) {
         IRecipeLayoutWithButtons<?> firstLayout = (IRecipeLayoutWithButtons<?>)this.recipeLayoutsWithButtons.getFirst();
         ImmutableRect2i layoutAreaWithBorder = new ImmutableRect2i(firstLayout.getRecipeLayout().getRectWithBorder());
         int recipeXOffset = this.getRecipeXOffset(layoutAreaWithBorder, recipeLayoutsArea);
         int recipeHeight = layoutAreaWithBorder.getHeight();
         int availableHeight = Math.max(recipeLayoutsArea.getHeight(), recipeHeight);
         int remainingHeight = availableHeight - recipesPerPage * recipeHeight;
         int recipeSpacing = remainingHeight / (recipesPerPage + 1);
         int spacingY = recipeHeight + recipeSpacing;
         int recipeYOffset = recipeLayoutsArea.getY() + recipeSpacing;

         for (IRecipeLayoutWithButtons<?> recipeLayoutWithButtons : this.recipeLayoutsWithButtons) {
            recipeLayoutWithButtons.updateBounds(recipeXOffset, recipeYOffset);
            recipeYOffset += spacingY;
         }
      }
   }

   private int getRecipeXOffset(ImmutableRect2i layoutRect, ImmutableRect2i layoutsArea) {
      if (this.recipeLayoutsWithButtons.isEmpty()) {
         return layoutsArea.getX();
      } else {
         int recipeWidth = layoutRect.getWidth();
         int recipeWidthWithButtons = ((IRecipeLayoutWithButtons)this.recipeLayoutsWithButtons.getFirst()).totalWidth();
         int buttonSpace = recipeWidthWithButtons - recipeWidth;
         int availableArea = layoutsArea.getWidth();
         return availableArea > recipeWidth + 2 * buttonSpace
            ? layoutsArea.getX() + (layoutsArea.getWidth() - recipeWidth) / 2
            : layoutsArea.getX() + (layoutsArea.getWidth() - recipeWidthWithButtons) / 2;
      }
   }

   public IUserInputHandler createInputHandler() {
      return new ProxyInputHandler(() -> {
         if (this.cachedInputHandler == null) {
            List<IUserInputHandler> handlers = this.recipeLayoutsWithButtons.stream().map(IRecipeLayoutWithButtons::createUserInputHandler).toList();
            this.cachedInputHandler = new CombinedInputHandler("RecipeGuiLayouts", handlers);
         }

         return this.cachedInputHandler;
      });
   }

   public void tick() {
      this.safeCallOnRecipeLayouts(IRecipeLayoutWithButtons::tick);
   }

   public void setRecipeLayoutsWithButtons(List<IRecipeLayoutWithButtons<?>> recipeLayoutsWithButtons) {
      this.recipeLayoutsWithButtons.clear();
      this.recipeLayoutsWithButtons.addAll(recipeLayoutsWithButtons);
      this.cachedInputHandler = null;
   }

   public Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double mouseX, double mouseY) {
      return this.recipeLayoutsWithButtons
         .stream()
         .map(IRecipeLayoutWithButtons::getRecipeLayout)
         .map(recipeLayout -> recipeLayout.getSlotUnderMouse(mouseX, mouseY))
         .flatMap(Optional::stream)
         .map(RecipeGuiLayouts::getClickedIngredient)
         .flatMap(Optional::stream);
   }

   public Optional<IRecipeLayoutWithButtons<?>> getRecipeLayoutUnderMouse(double mouseX, double mouseY) {
      for (IRecipeLayoutWithButtons<?> recipeLayoutWithButtons : this.recipeLayoutsWithButtons) {
         IRecipeLayoutDrawable<?> recipeLayout = recipeLayoutWithButtons.getRecipeLayout();
         if (recipeLayout.isMouseOver(mouseX, mouseY)) {
            return Optional.of(recipeLayoutWithButtons);
         }
      }

      return Optional.empty();
   }

   private static Optional<IClickableIngredientInternal<?>> getClickedIngredient(RecipeSlotUnderMouse slotUnderMouse) {
      return slotUnderMouse.slot().getDisplayedIngredient().map(displayedIngredient -> {
         IElement<?> element = new IngredientElement<>((ITypedIngredient<?>)displayedIngredient);
         return new ClickableIngredientInternal<>(element, slotUnderMouse::isMouseOver, false, true);
      });
   }

   public boolean mouseDragged(double mouseX, double mouseY, Key input, double dragX, double dragY) {
      for (IRecipeLayoutWithButtons<?> recipeLayoutWithButtons : this.recipeLayoutsWithButtons) {
         IRecipeLayoutDrawable<?> recipeLayout = recipeLayoutWithButtons.getRecipeLayout();
         if (this.mouseDragged(recipeLayout, mouseX, mouseY, input, dragX, dragY)) {
            return true;
         }
      }

      return false;
   }

   private <R> boolean mouseDragged(IRecipeLayoutDrawable<R> recipeLayout, double mouseX, double mouseY, Key input, double dragX, double dragY) {
      if (recipeLayout.isMouseOver(mouseX, mouseY)) {
         IJeiInputHandler inputHandler = recipeLayout.getInputHandler();
         return inputHandler.handleMouseDragged(mouseX, mouseY, input, dragX, dragY);
      } else {
         return false;
      }
   }

   public void mouseMoved(double mouseX, double mouseY) {
      for (IRecipeLayoutWithButtons<?> recipeLayoutWithButtons : this.recipeLayoutsWithButtons) {
         IRecipeLayoutDrawable<?> recipeLayout = recipeLayoutWithButtons.getRecipeLayout();
         if (recipeLayout.isMouseOver(mouseX, mouseY)) {
            IJeiInputHandler inputHandler = recipeLayout.getInputHandler();
            inputHandler.handleMouseMoved(mouseX, mouseY);
         }
      }
   }

   public Optional<IRecipeLayoutDrawable<?>> draw(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      IRecipeLayoutDrawable<?> hoveredLayout = null;
      Minecraft minecraft = Minecraft.getInstance();
      DeltaTracker deltaTracker = minecraft.getTimer();
      float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
      this.safeCallOnRecipeLayouts(r -> r.draw(guiGraphics, mouseX, mouseY, partialTicks));

      for (IRecipeLayoutWithButtons<?> recipeLayoutWithButtons : this.recipeLayoutsWithButtons) {
         IRecipeLayoutDrawable<?> recipeLayout = recipeLayoutWithButtons.getRecipeLayout();
         if (recipeLayout.isMouseOver(mouseX, mouseY)) {
            hoveredLayout = recipeLayout;
            break;
         }
      }

      RenderSystem.disableBlend();
      return Optional.ofNullable(hoveredLayout);
   }

   private void safeCallOnRecipeLayouts(Consumer<IRecipeLayoutWithButtons<?>> consumer) {
      for (int i = 0; i < this.recipeLayoutsWithButtons.size(); i++) {
         IRecipeLayoutWithButtons<?> recipeLayoutWithButtons = this.recipeLayoutsWithButtons.get(i);
         IRecipeLayoutDrawable<?> recipeLayout = recipeLayoutWithButtons.getRecipeLayout();

         try {
            consumer.accept(recipeLayoutWithButtons);
         } catch (RuntimeException var7) {
            String recipeInfo = ErrorUtil.getRecipeInfo(recipeLayout);
            LOGGER.error("Recipe crashed:\n{}", recipeInfo, var7);
            this.recipeLayoutsWithButtons.set(i, new RecipeLayoutWithButtonsErrored<>(recipeLayout));
         }
      }
   }

   public void drawTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      this.safeCallOnRecipeLayouts(r -> r.drawTooltips(guiGraphics, mouseX, mouseY));
   }

   public int getWidth() {
      if (this.recipeLayoutsWithButtons.isEmpty()) {
         return 0;
      } else {
         IRecipeLayoutWithButtons<?> first = (IRecipeLayoutWithButtons<?>)this.recipeLayoutsWithButtons.getFirst();
         return first.totalWidth();
      }
   }
}
