package mezz.jei.api.recipe.transfer;

import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface IRecipeTransferError {
   IRecipeTransferError.Type getType();

   default int getButtonHighlightColor() {
      return -2130729728;
   }

   default void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView recipeSlotsView, int recipeX, int recipeY) {
   }

   @Deprecated(
      since = "19.5.4",
      forRemoval = true
   )
   default List<Component> getTooltip() {
      return List.of();
   }

   default void getTooltip(ITooltipBuilder tooltip) {
      tooltip.addAll(this.getTooltip());
   }

   default int getMissingCountHint() {
      return -1;
   }

   public static enum Type {
      INTERNAL(false),
      USER_FACING(false),
      COSMETIC(true);

      public final boolean allowsTransfer;

      private Type(boolean allowsTransfer) {
         this.allowsTransfer = allowsTransfer;
      }
   }
}
