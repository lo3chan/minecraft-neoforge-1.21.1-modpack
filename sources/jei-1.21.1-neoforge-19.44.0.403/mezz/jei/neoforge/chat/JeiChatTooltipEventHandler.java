package mezz.jei.neoforge.chat;

import java.util.Optional;
import mezz.jei.gui.chat.ChatIngredientTooltip;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.Pre;

public final class JeiChatTooltipEventHandler {
   private static boolean renderingJeiChatTooltip;

   private JeiChatTooltipEventHandler() {
   }

   public static void register(PermanentEventSubscriptions subscriptions) {
      subscriptions.register(Pre.class, JeiChatTooltipEventHandler::onRenderTooltipPre);
   }

   private static void onRenderTooltipPre(Pre event) {
      if (!renderingJeiChatTooltip) {
         Minecraft minecraft = Minecraft.getInstance();
         Screen screen = minecraft.screen;
         Optional<ChatIngredientTooltip.IngredientTooltipData<?>> optionalTooltipData = ChatIngredientTooltip.getTooltipForHoveredChatLink(
            screen, event.getX(), event.getY()
         );
         if (!optionalTooltipData.isEmpty()) {
            ChatIngredientTooltip.IngredientTooltipData<?> tooltipData = optionalTooltipData.get();
            if (renderJeiChatTooltip(event, tooltipData)) {
               event.setCanceled(true);
            }
         }
      }
   }

   private static <T> boolean renderJeiChatTooltip(Pre event, ChatIngredientTooltip.IngredientTooltipData<T> tooltipData) {
      if (tooltipData.tooltip().isEmpty()) {
         return false;
      } else {
         renderingJeiChatTooltip = true;

         try {
            tooltipData.draw(event.getGraphics(), event.getX(), event.getY());
         } finally {
            renderingJeiChatTooltip = false;
         }

         return true;
      }
   }
}
