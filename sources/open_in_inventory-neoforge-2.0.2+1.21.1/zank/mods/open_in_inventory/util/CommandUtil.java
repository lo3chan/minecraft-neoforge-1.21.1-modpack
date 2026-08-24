package zank.mods.open_in_inventory.util;

import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import java.util.function.Supplier;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;

public abstract class CommandUtil {
   public static Style clickToCopy(String value) {
      return Style.EMPTY
         .withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, value))
         .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")));
   }

   public static Style hover(Component value) {
      return Style.EMPTY.withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, value));
   }

   public static void sendSuccess(ClientCommandSourceStack source, Supplier<Component> message, boolean notifyAdmin) {
      source.arch$sendSuccess(message, notifyAdmin);
   }

   public static void sendSuccess(ClientCommandSourceStack source, Supplier<Component> message) {
      sendSuccess(source, message, false);
   }

   public static void sendSuccess(CommandContext<ClientCommandSourceStack> cx, Supplier<Component> message) {
      sendSuccess((ClientCommandSourceStack)cx.getSource(), message, false);
   }
}
