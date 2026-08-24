package mezz.jei.neoforge.chat;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mezz.jei.common.chat.JeiChatItemLinkRecipeLookup;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

public final class JeiInternalShowCommand {
   private JeiInternalShowCommand() {
   }

   public static void register(PermanentEventSubscriptions subscriptions) {
      subscriptions.register(RegisterClientCommandsEvent.class, JeiInternalShowCommand::onRegisterClientCommands);
   }

   private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)Commands.literal("jei_internal_show")
               .then(Commands.argument("link", StringArgumentType.greedyString()).executes(context -> {
                  String link = StringArgumentType.getString(context, "link");
                  return JeiChatItemLinkRecipeLookup.executeShowRecipeCommand(link);
               }))
         );
   }
}
