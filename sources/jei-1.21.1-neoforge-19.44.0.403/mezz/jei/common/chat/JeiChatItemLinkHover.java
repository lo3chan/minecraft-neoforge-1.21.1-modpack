package mezz.jei.common.chat;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import org.jetbrains.annotations.Nullable;

public final class JeiChatItemLinkHover {
   private JeiChatItemLinkHover() {
   }

   public static Optional<Style> getHoveredStyle(Screen screen, double mouseX, double mouseY) {
      if (!(screen instanceof ChatScreen)) {
         return Optional.empty();
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         ChatComponent chatComponent = minecraft.gui.getChat();
         Style style = chatComponent.getClickedComponentStyleAt(mouseX, mouseY);
         return Optional.ofNullable(style);
      }
   }

   public static Optional<JeiChatItemLinkHover.HoveredText> getHoveredText(Screen screen, double mouseX, double mouseY) {
      return getHoveredStyle(screen, mouseX, mouseY).map(style -> {
         Rect2i area = new Rect2i((int)mouseX, (int)mouseY, 1, 1);
         return new JeiChatItemLinkHover.HoveredText(style, area);
      });
   }

   public static Optional<JeiChatItemLinks.IngredientLink> getIngredientLink(@Nullable Style style) {
      if (style == null) {
         return Optional.empty();
      } else {
         ClickEvent clickEvent = style.getClickEvent();
         if (clickEvent != null && clickEvent.getAction() == Action.RUN_COMMAND) {
            String command = clickEvent.getValue();
            return JeiChatItemLinks.parseShowRecipeCommand(command);
         } else {
            return Optional.empty();
         }
      }
   }

   public record HoveredText(Style style, Rect2i area) {
   }
}
