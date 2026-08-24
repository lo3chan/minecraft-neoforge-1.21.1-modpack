package mezz.jei.api.gui.widgets;

import java.util.List;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.placement.IPlaceable;
import net.minecraft.network.chat.FormattedText;

public interface IRecipeExtrasBuilder {
   IRecipeSlotDrawablesView getRecipeSlots();

   void addDrawable(IDrawable var1, int var2, int var3);

   IPlaceable<?> addDrawable(IDrawable var1);

   void addWidget(IRecipeWidget var1);

   void addSlottedWidget(ISlottedRecipeWidget var1, List<IRecipeSlotDrawable> var2);

   void addInputHandler(IJeiInputHandler var1);

   void addGuiEventListener(IJeiGuiEventListener var1);

   IScrollBoxWidget addScrollBoxWidget(int var1, int var2, int var3, int var4);

   IScrollGridWidget addScrollGridWidget(List<IRecipeSlotDrawable> var1, int var2, int var3);

   @Deprecated(
      since = "19.19.1",
      forRemoval = true
   )
   default void addRecipeArrow(int xPos, int yPos) {
      this.addRecipeArrow().setPosition(xPos, yPos);
   }

   IPlaceable<?> addRecipeArrow();

   @Deprecated(
      since = "19.19.1",
      forRemoval = true
   )
   default void addRecipePlusSign(int xPos, int yPos) {
      this.addRecipePlusSign().setPosition(xPos, yPos);
   }

   IPlaceable<?> addRecipePlusSign();

   @Deprecated(
      since = "19.19.1",
      forRemoval = true
   )
   default void addAnimatedRecipeArrow(int ticksPerCycle, int xPos, int yPos) {
      this.addAnimatedRecipeArrow(ticksPerCycle).setPosition(xPos, yPos);
   }

   IPlaceable<?> addAnimatedRecipeArrow(int var1);

   @Deprecated(
      since = "19.19.1",
      forRemoval = true
   )
   default void addAnimatedRecipeFlame(int cookTime, int xPos, int yPos) {
      this.addAnimatedRecipeFlame(cookTime).setPosition(xPos, yPos);
   }

   IPlaceable<?> addAnimatedRecipeFlame(int var1);

   default ITextWidget addText(FormattedText text, int maxWidth, int maxHeight) {
      return this.addText(List.of(text), maxWidth, maxHeight);
   }

   ITextWidget addText(List<FormattedText> var1, int var2, int var3);

   @Deprecated(
      since = "19.19.1",
      forRemoval = true
   )
   default ITextWidget addText(FormattedText text, int xPos, int yPos, int maxWidth, int maxHeight) {
      return this.addText(List.of(text), maxWidth, maxHeight).setPosition(xPos, yPos);
   }

   @Deprecated(
      since = "19.19.1",
      forRemoval = true
   )
   default ITextWidget addText(List<FormattedText> text, int xPos, int yPos, int maxWidth, int maxHeight) {
      return this.addText(text, maxWidth, maxHeight).setPosition(xPos, yPos);
   }
}
