package mezz.jei.gui.chat;

import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import mezz.jei.common.chat.JeiChatItemLinkHover;
import mezz.jei.common.chat.JeiChatItemLinks;
import mezz.jei.common.gui.IngredientTooltipComponent;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.util.SafeIngredientUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public final class ChatIngredientTooltip {
   private ChatIngredientTooltip() {
   }

   public static boolean setTooltipForHoveredText(GuiGraphics guiGraphics, @Nullable Style hoveredStyle, int mouseX, int mouseY) {
      Optional<ChatIngredientTooltip.IngredientTooltipData<?>> optionalTooltipData = getTooltipForHoveredText(hoveredStyle);
      if (optionalTooltipData.isEmpty()) {
         return false;
      } else {
         ChatIngredientTooltip.IngredientTooltipData<?> tooltipData = optionalTooltipData.get();
         tooltipData.draw(guiGraphics, mouseX, mouseY);
         return true;
      }
   }

   public static Optional<ChatIngredientTooltip.IngredientTooltipData<?>> getTooltipForHoveredChatLink(@Nullable Screen screen, double mouseX, double mouseY) {
      return screen == null
         ? Optional.empty()
         : JeiChatItemLinkHover.getHoveredStyle(screen, mouseX, mouseY).flatMap(ChatIngredientTooltip::getTooltipForHoveredText);
   }

   public static Optional<ChatIngredientTooltip.IngredientTooltipData<?>> getTooltipForHoveredText(@Nullable Style hoveredStyle) {
      Optional<JeiChatItemLinks.IngredientLink> optionalLink = JeiChatItemLinkHover.getIngredientLink(hoveredStyle);
      if (optionalLink.isEmpty()) {
         return Optional.empty();
      } else {
         Optional<IJeiRuntime> optionalRuntime = Internal.getOptionalJeiRuntime();
         if (optionalRuntime.isEmpty()) {
            return Optional.empty();
         } else {
            IJeiRuntime jeiRuntime = optionalRuntime.get();
            IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
            JeiChatItemLinks.IngredientLink link = optionalLink.get();
            Optional<ITypedIngredient<?>> optionalTypedIngredient = JeiChatItemLinks.resolveTypedIngredient(link, ingredientManager);
            if (optionalTypedIngredient.isEmpty()) {
               return Optional.empty();
            } else {
               ITypedIngredient<?> typedIngredient = optionalTypedIngredient.get();
               ChatIngredientTooltip.IngredientTooltipData<?> tooltipData = createTooltipData(typedIngredient, ingredientManager);
               return Optional.of(tooltipData);
            }
         }
      }
   }

   private static <T> ChatIngredientTooltip.IngredientTooltipData<T> createTooltipData(
      ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager
   ) {
      IIngredientRenderer<T> ingredientRenderer = ingredientManager.getIngredientRenderer(typedIngredient.getType());
      JeiTooltip tooltip = new JeiTooltip();
      tooltip.add(new IngredientTooltipComponent<>(typedIngredient, ingredientRenderer));
      SafeIngredientUtil.getRichTooltip(tooltip, ingredientManager, ingredientRenderer, typedIngredient);
      return new ChatIngredientTooltip.IngredientTooltipData<>(typedIngredient, ingredientRenderer, ingredientManager, tooltip);
   }

   public record IngredientTooltipData<T>(
      ITypedIngredient<T> typedIngredient, IIngredientRenderer<T> ingredientRenderer, IIngredientManager ingredientManager, JeiTooltip tooltip
   ) {
      public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY) {
         this.tooltip.draw(guiGraphics, mouseX, mouseY, this.typedIngredient, this.ingredientRenderer, this.ingredientManager);
      }
   }
}
