package codx.codxlib.api.settings;

import codx.codxlib.api.ui.menu.CodxSettingsMenu;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class CodxSettingsCommand {
   private static final int PAGE_SIZE = 12;

   private CodxSettingsCommand() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, String root, String sub, CodxSettings spec) {
      register(dispatcher, root, sub, spec, null);
   }

   public static void register(
      CommandDispatcher<CommandSourceStack> dispatcher, String root, String sub, CodxSettings spec, CodxSettingsCommand.ChangeNote note
   ) {
      register(dispatcher, root, sub, spec, note, CodxSettingsCommand::isOperator);
   }

   public static void register(
      CommandDispatcher<CommandSourceStack> dispatcher,
      String root,
      String sub,
      CodxSettings spec,
      CodxSettingsCommand.ChangeNote note,
      Predicate<CommandSourceStack> permission
   ) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(root).requires(permission)).then(node(sub, spec, note)));
   }

   public static LiteralArgumentBuilder<CommandSourceStack> node(String sub, CodxSettings spec, CodxSettingsCommand.ChangeNote note) {
      return node(sub, spec, note, player -> CodxSettingsMenu.open(player, spec, note));
   }

   public static LiteralArgumentBuilder<CommandSourceStack> node(
      String sub, CodxSettings spec, CodxSettingsCommand.ChangeNote note, Consumer<ServerPlayer> menuOpener
   ) {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                    sub
                                 )
                                 .executes(context -> listCategories((CommandSourceStack)context.getSource(), spec, sub)))
                              .then(
                                 ((LiteralArgumentBuilder)Commands.literal("list")
                                       .executes(context -> listCategories((CommandSourceStack)context.getSource(), spec, sub)))
                                    .then(
                                       ((RequiredArgumentBuilder)Commands.argument("category", StringArgumentType.word())
                                             .suggests((context, builder) -> SharedSuggestionProvider.suggest(spec.categories(), builder))
                                             .executes(context -> listCategory(context, spec, sub, 1)))
                                          .then(
                                             Commands.argument("page", IntegerArgumentType.integer(1))
                                                .executes(context -> listCategory(context, spec, sub, IntegerArgumentType.getInteger(context, "page")))
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("search")
                                 .then(Commands.argument("text", StringArgumentType.greedyString()).executes(context -> search(context, spec)))
                           ))
                        .then(Commands.literal("get").then(optionArgument(spec).executes(context -> get(context, spec)))))
                     .then(
                        Commands.literal("set")
                           .then(optionArgument(spec).then(Commands.argument("value", StringArgumentType.greedyString()).suggests((context, builder) -> {
                              CodxSettings.ConfigValue<?> value = lookup(context, spec);
                              return value == null ? builder.buildFuture() : SharedSuggestionProvider.suggest(suggestionsFor(value), builder);
                           }).executes(context -> set(context, spec, note))))
                     ))
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("reset")
                           .then(Commands.literal("everything").executes(context -> resetEverything(context, spec, note))))
                        .then(optionArgument(spec).executes(context -> reset(context, spec, note)))
                  ))
               .then(Commands.literal("reload").executes(context -> reload(context, spec))))
            .then(Commands.literal("save").executes(context -> save(context, spec))))
         .then(Commands.literal("menu").executes(context -> menu(context, menuOpener)));
   }

   public static boolean isOperator(CommandSourceStack source) {
      MinecraftServer server = source.getServer();
      return server != null && server.isSingleplayer() || source.hasPermission(2);
   }

   private static RequiredArgumentBuilder<CommandSourceStack, String> optionArgument(CodxSettings spec) {
      return Commands.argument("option", StringArgumentType.word())
         .suggests((context, builder) -> SharedSuggestionProvider.suggest(spec.values().stream().map(CodxSettings.ConfigValue::name), builder));
   }

   private static int listCategories(CommandSourceStack source, CodxSettings spec, String sub) {
      source.sendSuccess(() -> Component.literal(spec.values().size() + " settings in " + spec.fileName()).withStyle(ChatFormatting.GOLD), false);

      for (String category : spec.categories()) {
         int size = spec.category(category).size();
         source.sendSuccess(
            () -> Component.literal(" " + category)
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(" (" + size + ")").withStyle(ChatFormatting.GRAY)),
            false
         );
      }

      source.sendSuccess(
         () -> Component.literal(sub + " list <category>, " + sub + " search <word>, or " + sub + " menu").withStyle(ChatFormatting.GRAY), false
      );
      return spec.categories().size();
   }

   private static int listCategory(CommandContext<CommandSourceStack> context, CodxSettings spec, String sub, int page) {
      String category = StringArgumentType.getString(context, "category");
      List<CodxSettings.ConfigValue<?>> values = spec.category(category);
      if (values.isEmpty()) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("No settings category called '" + category + "'."));
         return 0;
      } else {
         return sendPage((CommandSourceStack)context.getSource(), values, page, sub + " list " + category);
      }
   }

   private static int search(CommandContext<CommandSourceStack> context, CodxSettings spec) {
      String needle = StringArgumentType.getString(context, "text").toLowerCase(Locale.ROOT);
      List<CodxSettings.ConfigValue<?>> hits = new ArrayList<>();

      for (CodxSettings.ConfigValue<?> value : spec.values()) {
         if (value.name().toLowerCase(Locale.ROOT).contains(needle) || value.comment().toLowerCase(Locale.ROOT).contains(needle)) {
            hits.add(value);
         }
      }

      if (hits.isEmpty()) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Nothing matches '" + needle + "'."));
         return 0;
      } else {
         return sendPage((CommandSourceStack)context.getSource(), hits, 1, null);
      }
   }

   private static int sendPage(CommandSourceStack source, List<CodxSettings.ConfigValue<?>> values, int page, String pageCommand) {
      int pages = Math.max(1, (values.size() + 12 - 1) / 12);
      int clamped = Math.min(page, pages);
      int from = (clamped - 1) * 12;
      int to = Math.min(values.size(), from + 12);
      source.sendSuccess(() -> Component.literal(values.size() + " setting(s) — page " + clamped + "/" + pages).withStyle(ChatFormatting.GOLD), false);

      for (CodxSettings.ConfigValue<?> value : values.subList(from, to)) {
         source.sendSuccess(
            () -> Component.literal(" " + value.name() + " = ")
               .withStyle(ChatFormatting.YELLOW)
               .append(Component.literal(value.asString()).withStyle(value.isDefault() ? ChatFormatting.GRAY : ChatFormatting.GREEN)),
            false
         );
      }

      if (pageCommand != null && clamped < pages) {
         source.sendSuccess(() -> Component.literal(pageCommand + " " + (clamped + 1) + " for more").withStyle(ChatFormatting.GRAY), false);
      }

      return to - from;
   }

   private static int get(CommandContext<CommandSourceStack> context, CodxSettings spec) {
      CodxSettings.ConfigValue<?> value = lookup(context, spec);
      if (value == null) {
         return unknown(context, spec);
      } else {
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               () -> Component.literal(value.name() + " = ")
                  .withStyle(ChatFormatting.YELLOW)
                  .append(Component.literal(value.asString()).withStyle(value.isDefault() ? ChatFormatting.GRAY : ChatFormatting.GREEN)),
               false
            );
         if (!value.comment().isEmpty()) {
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal(" " + value.comment()).withStyle(ChatFormatting.WHITE), false);
         }

         String range = value.rangeText();
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               () -> Component.literal(
                     " " + value.typeName() + (range.isEmpty() ? "" : ", " + range) + ", default " + value.defaultAsString() + " [" + value.category() + "]"
                  )
                  .withStyle(ChatFormatting.GRAY),
               false
            );
         return 1;
      }
   }

   private static int set(CommandContext<CommandSourceStack> context, CodxSettings spec, CodxSettingsCommand.ChangeNote note) {
      CodxSettings.ConfigValue<?> value = lookup(context, spec);
      if (value == null) {
         return unknown(context, spec);
      } else {
         String text = StringArgumentType.getString(context, "value");
         String before = value.asString();
         if (!value.setFromString(text)) {
            ((CommandSourceStack)context.getSource())
               .sendFailure(
                  Component.literal(
                     "'"
                        + text
                        + "' is not a valid "
                        + value.typeName()
                        + " for "
                        + value.name()
                        + (value.rangeText().isEmpty() ? "." : " (" + value.rangeText() + ").")
                  )
               );
            return 0;
         } else {
            return applied(context, spec, note, value.name() + ": " + before + " -> " + value.asString(), value);
         }
      }
   }

   private static int reset(CommandContext<CommandSourceStack> context, CodxSettings spec, CodxSettingsCommand.ChangeNote note) {
      CodxSettings.ConfigValue<?> value = lookup(context, spec);
      if (value == null) {
         return unknown(context, spec);
      } else {
         String before = value.asString();
         value.reset();
         return applied(context, spec, note, value.name() + ": " + before + " -> " + value.asString() + " (default)", value);
      }
   }

   private static int resetEverything(CommandContext<CommandSourceStack> context, CodxSettings spec, CodxSettingsCommand.ChangeNote note) {
      int changed = 0;

      for (CodxSettings.ConfigValue<?> value : spec.values()) {
         if (!value.isDefault()) {
            value.reset();
            changed++;
         }
      }

      return applied(context, spec, note, changed + " setting(s) put back to their defaults", null);
   }

   private static int reload(CommandContext<CommandSourceStack> context, CodxSettings spec) {
      spec.load();
      ((CommandSourceStack)context.getSource())
         .sendSuccess(() -> Component.literal("Re-read " + spec.fileName() + " from disk.").withStyle(ChatFormatting.GREEN), true);
      return 1;
   }

   private static int save(CommandContext<CommandSourceStack> context, CodxSettings spec) {
      if (!spec.save()) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Could not write " + spec.fileName() + " — see the server log."));
         return 0;
      } else {
         ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("Wrote " + spec.fileName() + ".").withStyle(ChatFormatting.GREEN), true);
         return 1;
      }
   }

   private static int menu(CommandContext<CommandSourceStack> context, Consumer<ServerPlayer> menuOpener) {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayer();
      if (player == null) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Only a player can open the settings menu."));
         return 0;
      } else {
         menuOpener.accept(player);
         return 1;
      }
   }

   static int applied(
      CommandContext<CommandSourceStack> context, CodxSettings spec, CodxSettingsCommand.ChangeNote note, String message, CodxSettings.ConfigValue<?> value
   ) {
      boolean written = spec.apply();
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal(message).withStyle(ChatFormatting.GREEN), true);
      if (!written) {
         ((CommandSourceStack)context.getSource())
            .sendFailure(Component.literal("Changed for this session only — " + spec.fileName() + " could not be written, see the server log."));
      }

      String extra = note == null ? null : note.noteFor(value);
      if (extra != null && !extra.isEmpty()) {
         ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal(extra).withStyle(ChatFormatting.GRAY), false);
      }

      return 1;
   }

   private static CodxSettings.ConfigValue<?> lookup(CommandContext<CommandSourceStack> context, CodxSettings spec) {
      return spec.find(StringArgumentType.getString(context, "option"));
   }

   private static int unknown(CommandContext<CommandSourceStack> context, CodxSettings spec) {
      String name = StringArgumentType.getString(context, "option");
      Function<String, String> hint = text -> {
         for (CodxSettings.ConfigValue<?> value : spec.values()) {
            if (value.name().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
               return " Did you mean " + value.name() + "?";
            }
         }

         return "";
      };
      ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("No setting called '" + name + "'." + hint.apply(name)));
      return 0;
   }

   private static List<String> suggestionsFor(CodxSettings.ConfigValue<?> value) {
      List<String> suggestions = new ArrayList<>();

      for (String suggestion : value.suggestions()) {
         if (!suggestions.contains(suggestion)) {
            suggestions.add(suggestion);
         }
      }

      return suggestions;
   }

   @FunctionalInterface
   public interface ChangeNote {
      String noteFor(CodxSettings.ConfigValue<?> var1);
   }
}
