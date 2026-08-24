package io.wispforest.owo.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import io.wispforest.owo.ops.TextOps;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class OwoConfigCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("owo-config")
            .then(Commands.argument("config_id", new OwoConfigCommand.ConfigScreenArgumentType()).executes(context -> {
               ConfigScreen screen = (ConfigScreen)context.getArgument("config_id", ConfigScreen.class);
               Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(screen));
               return 0;
            }))
      );
   }

   private static class ConfigScreenArgumentType implements ArgumentType<Screen> {
      private static final SimpleCommandExceptionType NO_SUCH_CONFIG_SCREEN = new SimpleCommandExceptionType(
         TextOps.concat(Owo.PREFIX, Component.literal("no config screen with that id"))
      );

      public Screen parse(StringReader reader) throws CommandSyntaxException {
         Function<Screen, ? extends Screen> provider = ConfigScreenProviders.get(reader.readString());
         if (provider == null) {
            throw NO_SUCH_CONFIG_SCREEN.create();
         } else {
            return provider.apply(null);
         }
      }

      public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
         ArrayList<String> configNames = new ArrayList<>();
         ConfigScreenProviders.forEach((s, screenFunction) -> configNames.add(s));
         return SharedSuggestionProvider.suggest(configNames, builder);
      }
   }
}
