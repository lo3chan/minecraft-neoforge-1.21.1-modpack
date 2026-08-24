package fuzs.puzzleslib.neoforge.impl.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.server.command.ModIdArgument;

public class NeoForgeConfigCommand {
   private static final Dynamic2CommandExceptionType ERROR_NO_CONFIG = new Dynamic2CommandExceptionType(
      (modId, type) -> Component.translatable("commands.config.noconfig", new Object[]{modId, type})
   );

   public static <T extends Enum<T> & StringRepresentable, P extends SharedSuggestionProvider> void register(
      CommandDispatcher<P> dispatcher, BiConsumer<P, Component> feedbackSender
   ) {
      dispatcher.register(
         (LiteralArgumentBuilder)LiteralArgumentBuilder.literal("config")
            .then(
               RequiredArgumentBuilder.argument(
                     "mod",
                     new ModIdArgument() {
                        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
                           return SharedSuggestionProvider.suggest(
                              ModList.get().applyForEachModContainer(ModContainer::getModId).filter(NeoForgeConfigCommand::anyModConfigsExist), builder
                           );
                        }
                     }
                  )
                  .then(
                     RequiredArgumentBuilder.argument("type", enumConstant(NeoForgeConfigCommand.Type.class))
                        .executes(
                           commandContext -> showFile(
                              component -> feedbackSender.accept((T)((SharedSuggestionProvider)commandContext.getSource()), component),
                              (String)commandContext.getArgument("mod", String.class),
                              ((NeoForgeConfigCommand.Type)commandContext.getArgument("type", NeoForgeConfigCommand.Type.class)).unwrap()
                           )
                        )
                  )
            )
      );
   }

   public static <T extends Enum<T> & StringRepresentable> StringRepresentableArgument<T> enumConstant(Class<? extends T> enumClazz) {
      return new StringRepresentableArgument<T>(StringRepresentable.fromEnum(enumClazz::getEnumConstants), enumClazz::getEnumConstants) {};
   }

   private static boolean anyModConfigsExist(String modId) {
      return Stream.of(net.neoforged.fml.config.ModConfig.Type.values())
         .flatMap(type -> ModConfigs.getConfigFileNames(modId, type).stream())
         .findAny()
         .isPresent();
   }

   private static int showFile(Consumer<Component> feedbackSender, String modId, net.neoforged.fml.config.ModConfig.Type type) throws CommandSyntaxException {
      List<String> configFileNames = ModConfigs.getConfigFileNames(modId, type);
      if (configFileNames.isEmpty()) {
         throw ERROR_NO_CONFIG.create(modId, type.name().toLowerCase(Locale.ROOT));
      } else {
         configFileNames.stream()
            .map(File::new)
            .map(NeoForgeConfigCommand::fileComponent)
            .forEach(
               component -> feedbackSender.accept(
                  Component.translatable("commands.config.getwithtype", new Object[]{modId, type.name().toLowerCase(Locale.ROOT), component})
               )
            );
         return configFileNames.size();
      }
   }

   private static Component fileComponent(File file) {
      return Component.literal(file.getName())
         .withStyle(ChatFormatting.UNDERLINE)
         .withStyle(style -> style.withClickEvent(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath())));
   }

   private static enum Type implements StringRepresentable {
      COMMON,
      CLIENT,
      SERVER,
      STARTUP;

      public net.neoforged.fml.config.ModConfig.Type unwrap() {
         return net.neoforged.fml.config.ModConfig.Type.valueOf(this.name());
      }

      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
