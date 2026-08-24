package zank.mods.open_in_inventory.impl.handler;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.impl.OpenActionRegistryImpl;
import zank.mods.open_in_inventory.util.CommandUtil;

public class ClientCommand {
   public static void register(CommandDispatcher<ClientCommandSourceStack> dispatcher, CommandBuildContext context) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommandRegistrationEvent.literal(
                           "open_in_inventory".replace('_', '-')
                        )
                        .then(ClientCommandRegistrationEvent.literal("refresh").executes(ClientCommand::refresh)))
                     .then(
                        ((LiteralArgumentBuilder)ClientCommandRegistrationEvent.literal("hand").executes(cx -> CommandAdd.execute(cx, Set.of(CommandAdd.SHOW))))
                           .then(
                              ClientCommandRegistrationEvent.literal("--wildcard")
                                 .executes(cx -> CommandAdd.execute(cx, Set.of(CommandAdd.SHOW, CommandAdd.WILDCARD)))
                           )
                     ))
                  .then(
                     ((LiteralArgumentBuilder)ClientCommandRegistrationEvent.literal("hotbar")
                           .executes(cx -> CommandAdd.execute(cx, Set.of(CommandAdd.HOTBAR, CommandAdd.SHOW))))
                        .then(
                           ClientCommandRegistrationEvent.literal("--wildcard")
                              .executes(cx -> CommandAdd.execute(cx, Set.of(CommandAdd.HOTBAR, CommandAdd.SHOW, CommandAdd.WILDCARD)))
                        )
                  ))
               .then(
                  ClientCommandRegistrationEvent.literal("replaceTemplate")
                     .then(
                        ClientCommandRegistrationEvent.argument("key", StringArgumentType.string())
                           .suggests(ClientCommand::suggestReplaceTemplate)
                           .executes(ClientCommand::replaceTemplate)
                     )
               ))
            .then(
               ClientCommandRegistrationEvent.literal("add")
                  .then(
                     ClientCommandRegistrationEvent.argument("args", StringArgumentType.greedyString())
                        .suggests(CommandAdd::suggest)
                        .executes(CommandAdd::execute)
                  )
            )
      );
   }

   private static int refresh(CommandContext<ClientCommandSourceStack> cx) {
      OpenInInventory.refreshConfig();
      OpenInInventory.COMMON.actionHandler.reset();
      CommandUtil.sendSuccess(cx, () -> Component.translatable("open_in_inventory.command.refresh"));
      return 1;
   }

   private static int replaceTemplate(CommandContext<ClientCommandSourceStack> cx) {
      String key = (String)cx.getArgument("key", String.class);
      Collection<String> replace = OpenInInventory.ACTION_REGISTRY.getReplaceTemplate(key);
      Supplier<Component> message = () -> Component.empty()
         .append(Component.literal(key).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)))
         .append(" -> ")
         .append(OpenInInventory.GSON.toJson(replace));
      CommandUtil.sendSuccess(cx, message);
      return 1;
   }

   private static CompletableFuture<Suggestions> suggestReplaceTemplate(CommandContext<ClientCommandSourceStack> cx, SuggestionsBuilder builder) {
      OpenActionRegistryImpl registry = (OpenActionRegistryImpl)OpenInInventory.ACTION_REGISTRY;
      return SharedSuggestionProvider.suggest(registry.replaceTemplates.keySet(), builder);
   }
}
