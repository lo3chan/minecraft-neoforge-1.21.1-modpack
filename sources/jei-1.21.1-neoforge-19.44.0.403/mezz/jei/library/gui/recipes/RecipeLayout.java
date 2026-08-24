package mezz.jei.library.gui.recipes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.placement.IPlaceable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.gui.widgets.IScrollBoxWidget;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.gui.elements.DrawableAnimated;
import mezz.jei.common.gui.elements.DrawableCombined;
import mezz.jei.common.gui.elements.OffsetDrawable;
import mezz.jei.common.gui.elements.TextWidget;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.LimitedLogger;
import mezz.jei.common.util.MathUtil;
import mezz.jei.library.gui.ingredients.CycleTicker;
import mezz.jei.library.gui.recipes.layout.builder.RecipeLayoutBuilder;
import mezz.jei.library.gui.widgets.ScrollBoxRecipeWidget;
import mezz.jei.library.gui.widgets.ScrollGridRecipeWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.FormattedText;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class RecipeLayout<R> implements IRecipeLayoutDrawable<R>, IRecipeExtrasBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final LimitedLogger LIMITED_LOGGER = new LimitedLogger(LOGGER, Duration.ofSeconds(10L));
   public static final int RECIPE_BUTTON_SIZE = 13;
   public static final int RECIPE_BUTTON_SPACING = 2;
   private final IRecipeCategory<R> recipeCategory;
   private final Collection<IRecipeCategoryDecorator<R>> recipeCategoryDecorators;
   private final List<IRecipeSlotDrawable> recipeCategorySlots;
   private final IRecipeSlotsView recipeSlotsView;
   private final List<IDrawable> drawables;
   private final List<ISlottedRecipeWidget> slottedWidgets;
   private final CycleTicker cycleTicker;
   private final IFocusGroup focuses;
   private final List<IRecipeWidget> allWidgets;
   private final R recipe;
   private final IScalableDrawable recipeBackground;
   private final int recipeBorderPadding;
   private final ImmutableRect2i recipeTransferButtonArea;
   @Nullable
   private final ShapelessIcon shapelessIcon;
   private final RecipeLayoutInputHandler<R> inputHandler;
   private boolean extrasCreated = false;
   private ImmutableRect2i area;

   public static <T> Optional<IRecipeLayoutDrawable<T>> create(
      IRecipeCategory<T> recipeCategory,
      Collection<IRecipeCategoryDecorator<T>> decorators,
      T recipe,
      IFocusGroup focuses,
      IIngredientManager ingredientManager,
      IScalableDrawable recipeBackground,
      int recipeBorderPadding
   ) {
      RecipeLayoutBuilder<T> builder = new RecipeLayoutBuilder<>(recipeCategory, recipe, ingredientManager);

      try {
         recipeCategory.setRecipe(builder, recipe, focuses);
         RecipeLayout<T> recipeLayout = builder.buildRecipeLayout(focuses, decorators, recipeBackground, recipeBorderPadding);
         return Optional.of(recipeLayout);
      } catch (LinkageError | RuntimeException var10) {
         String recipeInfo = ErrorUtil.getRecipeInfo(recipeCategory, recipe);
         LOGGER.error("Recipe crashed during Recipe Layout creation:\n{}", recipeInfo, var10);
         return Optional.empty();
      }
   }

   public RecipeLayout(
      IRecipeCategory<R> recipeCategory,
      Collection<IRecipeCategoryDecorator<R>> recipeCategoryDecorators,
      R recipe,
      IScalableDrawable recipeBackground,
      int recipeBorderPadding,
      @Nullable ShapelessIcon shapelessIcon,
      ImmutablePoint2i recipeTransferButtonPos,
      List<IRecipeSlotDrawable> recipeCategorySlots,
      List<IRecipeSlotDrawable> allSlots,
      CycleTicker cycleTicker,
      IFocusGroup focuses
   ) {
      this.recipeCategory = recipeCategory;
      this.recipeCategoryDecorators = recipeCategoryDecorators;
      this.drawables = new ArrayList<>();
      this.slottedWidgets = new ArrayList<>();
      this.allWidgets = new ArrayList<>();
      this.cycleTicker = cycleTicker;
      this.focuses = focuses;
      this.inputHandler = new RecipeLayoutInputHandler<>(this);
      this.recipeCategorySlots = recipeCategorySlots;
      this.recipeSlotsView = new RecipeLayout.RecipeSlotsView(Collections.unmodifiableList(allSlots));
      this.recipeBorderPadding = recipeBorderPadding;
      this.area = new ImmutableRect2i(0, 0, recipeCategory.getWidth(), recipeCategory.getHeight());
      this.recipeTransferButtonArea = new ImmutableRect2i(recipeTransferButtonPos.x(), recipeTransferButtonPos.y(), 13, 13);
      this.recipe = recipe;
      this.recipeBackground = recipeBackground;
      this.shapelessIcon = shapelessIcon;
      recipeCategory.onDisplayedIngredientsUpdate(recipe, Collections.unmodifiableList(recipeCategorySlots), focuses);
   }

   public void ensureRecipeExtrasAreCreated() {
      if (!this.extrasCreated) {
         this.extrasCreated = true;
         this.recipeCategory.createRecipeExtras(this, this.recipe, this.focuses);
      }
   }

   @Override
   public void setPosition(int posX, int posY) {
      this.area = this.area.setPosition(posX, posY);
   }

   @Override
   public void drawRecipe(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      this.ensureRecipeExtrasAreCreated();
      IDrawable background = this.recipeCategory.getBackground();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.recipeBackground.draw(guiGraphics, this.getRectWithBorder());
      double recipeMouseX = mouseX - this.area.getX();
      double recipeMouseY = mouseY - this.area.getY();
      IRecipeSlotsView recipeCategorySlotsView = () -> Collections.unmodifiableList(this.recipeCategorySlots);
      RecipeSlotUnderMouse hoveredSlotResult = this.getSlotUnderMouse(mouseX, mouseY).orElse(null);
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.translate(this.area.getX(), this.area.getY(), 0.0F);
      if (background != null) {
         background.draw(guiGraphics);
      }

      poseStack.pushPose();
      this.recipeCategory.draw(this.recipe, recipeCategorySlotsView, guiGraphics, recipeMouseX, recipeMouseY);

      for (IRecipeSlotDrawable slot : this.recipeCategorySlots) {
         boolean hovered = hoveredSlotResult != null && hoveredSlotResult.slot() == slot;
         slot.draw(guiGraphics, hovered);
      }

      for (IRecipeWidget widget : this.allWidgets) {
         ScreenPosition position = widget.getPosition();
         poseStack.pushPose();
         poseStack.translate(position.x(), position.y(), 0.0F);
         widget.drawWidget(guiGraphics, recipeMouseX - position.x(), recipeMouseY - position.y());
         poseStack.popPose();
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.popPose();

      for (IDrawable drawable : this.drawables) {
         poseStack.pushPose();
         drawable.draw(guiGraphics);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         poseStack.popPose();
      }

      for (IRecipeCategoryDecorator<R> decorator : this.recipeCategoryDecorators) {
         poseStack.pushPose();
         decorator.draw(this.recipe, this.recipeCategory, recipeCategorySlotsView, guiGraphics, recipeMouseX, recipeMouseY);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         poseStack.popPose();
      }

      if (this.shapelessIcon != null) {
         this.shapelessIcon.draw(guiGraphics);
      }

      poseStack.popPose();
      RenderSystem.disableBlend();
   }

   @Override
   public void drawOverlays(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      this.ensureRecipeExtrasAreCreated();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int recipeMouseX = mouseX - this.area.getX();
      int recipeMouseY = mouseY - this.area.getY();
      RenderSystem.disableBlend();
      IRecipeSlotsView recipeCategorySlotsView = () -> Collections.unmodifiableList(this.recipeCategorySlots);
      RecipeSlotUnderMouse hoveredSlotResult = this.getSlotUnderMouse(mouseX, mouseY).orElse(null);
      if (hoveredSlotResult != null) {
         IRecipeSlotDrawable hoveredSlot = hoveredSlotResult.slot();
         hoveredSlot.drawTooltip(guiGraphics, mouseX, mouseY);
      } else if (this.isMouseOver(mouseX, mouseY)) {
         JeiTooltip tooltip = new JeiTooltip();

         try {
            this.recipeCategory.getTooltip(tooltip, this.recipe, recipeCategorySlotsView, recipeMouseX, recipeMouseY);

            for (IRecipeCategoryDecorator<R> decorator : this.recipeCategoryDecorators) {
               decorator.decorateTooltips(tooltip, this.recipe, this.recipeCategory, recipeCategorySlotsView, recipeMouseX, recipeMouseY);
            }
         } catch (RuntimeException var12) {
            LIMITED_LOGGER.log(
               Level.ERROR,
               "recipe.category.tooltip.crash",
               logger -> logger.error("Error while getting tooltip from recipe:\n{}", ErrorUtil.getRecipeInfo(this.recipeCategory, this.recipe), var12)
            );
         }

         for (IRecipeWidget widget : this.allWidgets) {
            ScreenPosition position = widget.getPosition();
            widget.getTooltip(tooltip, recipeMouseX - position.x(), recipeMouseY - position.y());
         }

         if (tooltip.isEmpty() && this.shapelessIcon != null && this.shapelessIcon.isMouseOver(recipeMouseX, recipeMouseY)) {
            this.shapelessIcon.addTooltip(tooltip);
         }

         tooltip.draw(guiGraphics, mouseX, mouseY);
      }
   }

   @Override
   public boolean isMouseOver(double mouseX, double mouseY) {
      return MathUtil.contains(this.area, mouseX, mouseY);
   }

   @Override
   public Rect2i getRect() {
      return this.area.toMutable();
   }

   @Override
   public Rect2i getRectWithBorder() {
      return this.area.expandBy(this.recipeBorderPadding).toMutable();
   }

   @Override
   public <T> Optional<T> getIngredientUnderMouse(int mouseX, int mouseY, IIngredientType<T> ingredientType) {
      return this.getSlotUnderMouse(mouseX, mouseY).map(RecipeSlotUnderMouse::slot).flatMap(slot -> slot.getDisplayedIngredient(ingredientType));
   }

   @Override
   public Optional<IRecipeSlotDrawable> getRecipeSlotUnderMouse(double mouseX, double mouseY) {
      return this.getSlotUnderMouse(mouseX, mouseY).map(RecipeSlotUnderMouse::slot);
   }

   @Override
   public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
      this.ensureRecipeExtrasAreCreated();
      double recipeMouseX = mouseX - this.area.getX();
      double recipeMouseY = mouseY - this.area.getY();

      for (ISlottedRecipeWidget widget : this.slottedWidgets) {
         ScreenPosition position = widget.getPosition();
         double relativeMouseX = recipeMouseX - position.x();
         double relativeMouseY = recipeMouseY - position.y();
         Optional<RecipeSlotUnderMouse> slotResult = widget.getSlotUnderMouse(relativeMouseX, relativeMouseY);
         if (slotResult.isPresent()) {
            return slotResult.map(slotx -> slotx.addOffset(this.area.x(), this.area.y()));
         }
      }

      for (IRecipeSlotDrawable slot : this.recipeCategorySlots) {
         if (slot.isMouseOver(recipeMouseX, recipeMouseY)) {
            return Optional.of(new RecipeSlotUnderMouse(slot, this.area.getScreenPosition()));
         }
      }

      return Optional.empty();
   }

   @Override
   public IRecipeCategory<R> getRecipeCategory() {
      return this.recipeCategory;
   }

   @Override
   public Rect2i getSideButtonArea(int buttonIndex) {
      Rect2i buttonArea = this.recipeTransferButtonArea.toMutable();
      if (buttonIndex > 0) {
         int maxRows = (this.getRectWithBorder().getHeight() + 2) / (buttonArea.getHeight() + 2);
         int xIndex = buttonIndex / maxRows;
         int yIndex = buttonIndex % maxRows;
         int xOffset = xIndex * (buttonArea.getWidth() + 2);
         int yOffset = yIndex * (buttonArea.getHeight() + 2);
         buttonArea.setX(buttonArea.getX() + xOffset);
         buttonArea.setY(buttonArea.getY() - yOffset);
      }

      return buttonArea;
   }

   @Override
   public IRecipeSlotsView getRecipeSlotsView() {
      return this.recipeSlotsView;
   }

   @Override
   public IRecipeSlotDrawablesView getRecipeSlots() {
      this.ensureRecipeExtrasAreCreated();
      return () -> Collections.unmodifiableList(this.recipeCategorySlots);
   }

   @Override
   public R getRecipe() {
      return this.recipe;
   }

   @Override
   public IJeiInputHandler getInputHandler() {
      return this.inputHandler;
   }

   @Override
   public void tick() {
      this.ensureRecipeExtrasAreCreated();

      for (IRecipeWidget widget : this.allWidgets) {
         widget.tick();
      }

      if (this.cycleTicker.tick()) {
         for (IRecipeSlotDrawable slot : this.recipeCategorySlots) {
            slot.clearDisplayOverrides();
         }

         this.recipeCategory.onDisplayedIngredientsUpdate(this.recipe, this.recipeCategorySlots, this.focuses);
      }
   }

   @Override
   public void addDrawable(IDrawable drawable, int xPos, int yPos) {
      this.drawables.add(OffsetDrawable.create(drawable, xPos, yPos));
   }

   @Override
   public IPlaceable<?> addDrawable(IDrawable drawable) {
      OffsetDrawable offsetDrawable = new OffsetDrawable(drawable, 0, 0);
      this.drawables.add(offsetDrawable);
      return offsetDrawable;
   }

   @Override
   public void addWidget(IRecipeWidget widget) {
      this.allWidgets.add(widget);
      if (widget instanceof ISlottedRecipeWidget slottedWidget) {
         this.slottedWidgets.add(slottedWidget);
      }
   }

   @Override
   public void addSlottedWidget(ISlottedRecipeWidget widget, List<IRecipeSlotDrawable> slots) {
      this.allWidgets.add(widget);
      this.slottedWidgets.add(widget);
      this.recipeCategorySlots.removeAll(slots);
   }

   @Override
   public void addInputHandler(IJeiInputHandler inputHandler) {
      this.inputHandler.addInputHandler(inputHandler);
   }

   @Override
   public void addGuiEventListener(IJeiGuiEventListener guiEventListener) {
      this.inputHandler.addGuiEventListener(guiEventListener);
   }

   @Override
   public IScrollBoxWidget addScrollBoxWidget(int width, int height, int xPos, int yPos) {
      ScrollBoxRecipeWidget widget = new ScrollBoxRecipeWidget(width, height, xPos, yPos);
      this.addWidget(widget);
      this.addInputHandler(widget);
      return widget;
   }

   @Override
   public IScrollGridWidget addScrollGridWidget(List<IRecipeSlotDrawable> slots, int columns, int visibleRows) {
      ScrollGridRecipeWidget widget = ScrollGridRecipeWidget.create(slots, columns, visibleRows);
      this.addSlottedWidget(widget, slots);
      this.addInputHandler(widget);
      return widget;
   }

   @Override
   public IPlaceable<?> addRecipeArrow() {
      Textures textures = Internal.getTextures();
      IDrawable drawable = textures.getRecipeArrow();
      return this.addDrawable(drawable);
   }

   @Override
   public IPlaceable<?> addRecipePlusSign() {
      Textures textures = Internal.getTextures();
      IDrawable drawable = textures.getRecipePlusSign();
      return this.addDrawable(drawable);
   }

   @Override
   public IPlaceable<?> addAnimatedRecipeArrow(int ticksPerCycle) {
      Textures textures = Internal.getTextures();
      IDrawableStatic recipeArrowFilled = textures.getRecipeArrowFilled();
      IDrawable animatedFill = new DrawableAnimated(recipeArrowFilled, ticksPerCycle, IDrawableAnimated.StartDirection.LEFT, false);
      IDrawable drawableCombined = new DrawableCombined(textures.getRecipeArrow(), animatedFill);
      OffsetDrawable offsetDrawable = new OffsetDrawable(drawableCombined, 0, 0);
      return this.addDrawable(offsetDrawable);
   }

   @Override
   public IPlaceable<?> addAnimatedRecipeFlame(int cookTime) {
      Textures textures = Internal.getTextures();
      IDrawableStatic flameIcon = textures.getFlameIcon();
      IDrawableAnimated animatedFill = new DrawableAnimated(flameIcon, cookTime, IDrawableAnimated.StartDirection.TOP, true);
      IDrawable drawableCombined = new DrawableCombined(textures.getFlameEmptyIcon(), animatedFill);
      OffsetDrawable offsetDrawable = new OffsetDrawable(drawableCombined, 0, 0);
      return this.addDrawable(offsetDrawable);
   }

   @Override
   public ITextWidget addText(List<FormattedText> text, int maxWidth, int maxHeight) {
      TextWidget textWidget = new TextWidget(text, 0, 0, maxWidth, maxHeight);
      this.addWidget(textWidget);
      return textWidget;
   }

   private record RecipeSlotsView(@Unmodifiable List<IRecipeSlotView> allSlots) implements IRecipeSlotsView {
      @Unmodifiable
      @Override
      public List<IRecipeSlotView> getSlotViews() {
         return this.allSlots;
      }
   }
}
