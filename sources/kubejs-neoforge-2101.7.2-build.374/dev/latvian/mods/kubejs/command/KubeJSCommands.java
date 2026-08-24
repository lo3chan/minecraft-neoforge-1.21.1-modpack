package dev.latvian.mods.kubejs.command;

import com.google.common.base.Predicate;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.betteradvancedtooltips.BATIcons;
import dev.latvian.mods.betteradvancedtooltips.TooltipTagType;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.core.WithPersistentData;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.net.DisplayClientErrorsPayload;
import dev.latvian.mods.kubejs.net.DisplayServerErrorsPayload;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.net.ReloadStartupScriptsPayload;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextIcons;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextWrapper;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.server.BasicCommandKubeEvent;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer.ReloadableResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackResources;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public class KubeJSCommands {
   static final DynamicCommandExceptionType NO_REGISTRY = new DynamicCommandExceptionType(
      id -> Component.literal("No builtin or static registry found for " + id)
   );

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      Predicate<CommandSourceStack> spOrOP = source -> source.getServer().isSingleplayer() || source.hasPermission(2);
      LiteralArgumentBuilder<CommandSourceStack> cmd = (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                                   "kubejs"
                                                )
                                                .then(Commands.literal("help").executes(context -> help((CommandSourceStack)context.getSource()))))
                                             .then(
                                                Commands.literal("hand")
                                                   .executes(
                                                      context -> InformationCommands.hand(
                                                         ((CommandSourceStack)context.getSource()).getPlayerOrException(), InteractionHand.MAIN_HAND
                                                      )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("offhand")
                                                .executes(
                                                   context -> InformationCommands.hand(
                                                      ((CommandSourceStack)context.getSource()).getPlayerOrException(), InteractionHand.OFF_HAND
                                                   )
                                                )
                                          ))
                                       .then(
                                          Commands.literal("inventory")
                                             .executes(
                                                context -> InformationCommands.inventory(((CommandSourceStack)context.getSource()).getPlayerOrException())
                                             )
                                       ))
                                    .then(
                                       Commands.literal("hotbar")
                                          .executes(context -> InformationCommands.hotbar(((CommandSourceStack)context.getSource()).getPlayerOrException()))
                                    ))
                                 .then(
                                    ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("errors")
                                             .then(
                                                ((LiteralArgumentBuilder)Commands.literal("startup").requires(spOrOP))
                                                   .executes(context -> errors((CommandSourceStack)context.getSource(), ScriptType.STARTUP))
                                             ))
                                          .then(
                                             ((LiteralArgumentBuilder)Commands.literal("server").requires(spOrOP))
                                                .executes(context -> errors((CommandSourceStack)context.getSource(), ScriptType.SERVER))
                                          ))
                                       .then(
                                          ((LiteralArgumentBuilder)Commands.literal("client").requires(source -> true))
                                             .executes(context -> errors((CommandSourceStack)context.getSource(), ScriptType.CLIENT))
                                       )
                                 ))
                              .then(
                                 ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload")
                                          .then(
                                             ((LiteralArgumentBuilder)Commands.literal("config").requires(spOrOP))
                                                .executes(context -> reloadConfig((CommandSourceStack)context.getSource()))
                                          ))
                                       .then(
                                          ((LiteralArgumentBuilder)Commands.literal("startup-scripts").requires(spOrOP))
                                             .executes(context -> reloadStartup((CommandSourceStack)context.getSource()))
                                       ))
                                    .then(
                                       ((LiteralArgumentBuilder)Commands.literal("server-scripts").requires(spOrOP))
                                          .executes(context -> reloadServer((CommandSourceStack)context.getSource()))
                                    )
                              ))
                           .then(
                              ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("export")
                                             .requires(spOrOP))
                                          .then(Commands.literal("debug").executes(context -> export((CommandSourceStack)context.getSource()))))
                                       .then(Commands.literal("pack-zips").executes(context -> exportPacks((CommandSourceStack)context.getSource(), true))))
                                    .then(Commands.literal("pack-folders").executes(context -> exportPacks((CommandSourceStack)context.getSource(), false))))
                                 .then(
                                    ((LiteralArgumentBuilder)Commands.literal("recipe-schema-json").requires(spOrOP))
                                       .then(
                                          Commands.argument("recipe-type", ResourceKeyArgument.key(Registries.RECIPE_SERIALIZER))
                                             .executes(
                                                ctx -> exportRecipeSchemaJson(
                                                   (CommandSourceStack)ctx.getSource(), (ResourceKey<?>)ctx.getArgument("recipe-type", ResourceKey.class)
                                                )
                                             )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("list-tag")
                              .then(
                                 ((RequiredArgumentBuilder)Commands.argument("registry", ResourceLocationArgument.id())
                                       .suggests(
                                          (ctx, builder) -> SharedSuggestionProvider.suggest(
                                             ((CommandSourceStack)ctx.getSource())
                                                .registryAccess()
                                                .registries()
                                                .map(entry -> entry.key().location().toString()),
                                             builder
                                          )
                                       )
                                       .executes(ctx -> listTagsFor((CommandSourceStack)ctx.getSource(), registry(ctx, "registry"))))
                                    .then(
                                       Commands.argument("tag", ResourceLocationArgument.id())
                                          .suggests(
                                             (ctx, builder) -> SharedSuggestionProvider.suggest(
                                                allTags((CommandSourceStack)ctx.getSource(), registry(ctx, "registry"))
                                                   .map(TagKey::location)
                                                   .map(ResourceLocation::toString),
                                                builder
                                             )
                                          )
                                          .executes(
                                             ctx -> tagObjects(
                                                (CommandSourceStack)ctx.getSource(),
                                                TagKey.create(registry(ctx, "registry"), ResourceLocationArgument.getId(ctx, "tag"))
                                             )
                                          )
                                    )
                              )
                        ))
                     .then(
                        ((LiteralArgumentBuilder)Commands.literal("dump")
                              .then(
                                 Commands.literal("registry")
                                    .then(
                                       Commands.argument("registry", ResourceLocationArgument.id())
                                          .suggests(
                                             (ctx, builder) -> SharedSuggestionProvider.suggest(
                                                ((CommandSourceStack)ctx.getSource())
                                                   .registryAccess()
                                                   .registries()
                                                   .map(entry -> entry.key().location().toString()),
                                                builder
                                             )
                                          )
                                          .executes(ctx -> DumpCommands.registry((CommandSourceStack)ctx.getSource(), registry(ctx, "registry")))
                                    )
                              ))
                           .then(
                              ((LiteralArgumentBuilder)Commands.literal("events").requires(spOrOP))
                                 .executes(context -> DumpCommands.events((CommandSourceStack)context.getSource()))
                           )
                     ))
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stages")
                                    .requires(spOrOP))
                                 .then(
                                    Commands.literal("add")
                                       .then(
                                          Commands.argument("player", EntityArgument.players())
                                             .then(
                                                Commands.argument("stage", StringArgumentType.string())
                                                   .executes(
                                                      context -> StageCommands.addStage(
                                                         (CommandSourceStack)context.getSource(),
                                                         EntityArgument.getPlayers(context, "player"),
                                                         StringArgumentType.getString(context, "stage")
                                                      )
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("remove")
                                    .then(
                                       Commands.argument("player", EntityArgument.players())
                                          .then(
                                             Commands.argument("stage", StringArgumentType.string())
                                                .executes(
                                                   context -> StageCommands.removeStage(
                                                      (CommandSourceStack)context.getSource(),
                                                      EntityArgument.getPlayers(context, "player"),
                                                      StringArgumentType.getString(context, "stage")
                                                   )
                                                )
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("clear")
                                 .then(
                                    Commands.argument("player", EntityArgument.players())
                                       .executes(
                                          context -> StageCommands.clearStages(
                                             (CommandSourceStack)context.getSource(), EntityArgument.getPlayers(context, "player")
                                          )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("list")
                              .then(
                                 Commands.argument("player", EntityArgument.players())
                                    .executes(
                                       context -> StageCommands.listStages(
                                          (CommandSourceStack)context.getSource(), EntityArgument.getPlayers(context, "player")
                                       )
                                    )
                              )
                        )
                  ))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("generate-typings").requires(spOrOP))
                     .executes(context -> generateTypings((CommandSourceStack)context.getSource()))
               ))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("packmode").requires(spOrOP))
                     .executes(context -> packmode((CommandSourceStack)context.getSource(), "")))
                  .then(
                     Commands.argument("name", StringArgumentType.word())
                        .executes(context -> packmode((CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "name")))
                  )
            ))
         .then(
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("persistent-data").requires(spOrOP))
                     .then(
                        PersistentDataCommands.addPersistentDataCommands(
                           Commands.literal("server"), ctx -> Set.of(((CommandSourceStack)ctx.getSource()).getServer())
                        )
                     ))
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("dimension")
                           .then(
                              PersistentDataCommands.addPersistentDataCommands(
                                 Commands.literal("*"),
                                 ctx -> (Collection<? extends WithPersistentData>)((CommandSourceStack)ctx.getSource()).getServer().getAllLevels()
                              )
                           ))
                        .then(
                           PersistentDataCommands.addPersistentDataCommands(
                              Commands.argument("dimension", DimensionArgument.dimension()), ctx -> Set.of(DimensionArgument.getDimension(ctx, "dimension"))
                           )
                        )
                  ))
               .then(
                  Commands.literal("entity")
                     .then(
                        PersistentDataCommands.addPersistentDataCommands(
                           Commands.argument("entity", EntityArgument.entities()), ctx -> EntityArgument.getEntities(ctx, "entity")
                        )
                     )
               )
         );
      if (!FMLLoader.isProduction()) {
         cmd.then(
            ((LiteralArgumentBuilder)Commands.literal("eval").requires(spOrOP))
               .then(
                  Commands.argument("code", StringArgumentType.greedyString())
                     .executes(ctx -> eval((CommandSourceStack)ctx.getSource(), StringArgumentType.getString(ctx, "code")))
               )
         );
      }

      LiteralCommandNode<CommandSourceStack> cmd1 = dispatcher.register(cmd);
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("kjs").redirect(cmd1));

      for (String id : ServerEvents.BASIC_COMMAND.findUniqueExtraIds(ScriptType.SERVER)) {
         dispatcher.register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(id).requires(spOrOP))
                  .executes(ctx -> customCommand(ServerEvents.BASIC_COMMAND, (CommandSourceStack)ctx.getSource(), id, "")))
               .then(
                  Commands.argument("input", StringArgumentType.greedyString())
                     .executes(
                        ctx -> customCommand(ServerEvents.BASIC_COMMAND, (CommandSourceStack)ctx.getSource(), id, StringArgumentType.getString(ctx, "input"))
                     )
               )
         );
      }

      for (String id : ServerEvents.BASIC_PUBLIC_COMMAND.findUniqueExtraIds(ScriptType.SERVER)) {
         dispatcher.register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(id)
                  .executes(ctx -> customCommand(ServerEvents.BASIC_PUBLIC_COMMAND, (CommandSourceStack)ctx.getSource(), id, "")))
               .then(
                  Commands.argument("input", StringArgumentType.greedyString())
                     .executes(
                        ctx -> customCommand(
                           ServerEvents.BASIC_PUBLIC_COMMAND, (CommandSourceStack)ctx.getSource(), id, StringArgumentType.getString(ctx, "input")
                        )
                     )
               )
         );
      }
   }

   private static <T> ResourceKey<Registry<T>> registry(CommandContext<CommandSourceStack> ctx, String arg) {
      return ResourceKey.createRegistryKey(ResourceLocationArgument.getId(ctx, arg));
   }

   private static <T> Stream<TagKey<T>> allTags(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
      return ((Registry)source.registryAccess().registry(registry).orElseThrow(() -> NO_REGISTRY.create(registry.location()))).getTagNames();
   }

   private static void link(CommandSourceStack source, ChatFormatting color, Component icon, String name, @Nullable Component info, String url) {
      MutableComponent c = Component.literal("• ").withStyle(style -> style.withClickEvent(new ClickEvent(Action.OPEN_URL, url)));
      if (info != null) {
         c = c.withStyle(style -> style.withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, info)));
      }

      c.append(icon);
      c.append(TextIcons.smallSpace());
      c.append(Component.literal(name).withStyle(color));
      source.sendSystemMessage(c);
   }

   private static int help(CommandSourceStack source) {
      link(source, ChatFormatting.GOLD, TextIcons.crafting(), "Wiki", null, "https://kubejs.com/?" + KubeJS.QUERY);
      link(source, ChatFormatting.GREEN, TextIcons.info(), "Support", null, "https://kubejs.com/support?" + KubeJS.QUERY);
      link(source, ChatFormatting.BLUE, TextIcons.copy(), "Changelog", null, "https://kubejs.com/changelog?" + KubeJS.QUERY);
      LocalWebServer server = LocalWebServer.instance();
      if (server != null && !server.explorerCode().isEmpty() && source.getServer().isSingleplayer()) {
         link(
            source,
            ChatFormatting.LIGHT_PURPLE,
            TooltipTagType.ENTITY_TYPE.component(),
            "Explore",
            null,
            "https://kubejs.com/explorer#" + server.explorerCode()
         );
      }

      return 1;
   }

   private static int customCommand(TargetedEventHandler<String> event, CommandSourceStack source, String id, String input) {
      if (event.hasListeners(id)) {
         EventResult result = event.post(new BasicCommandKubeEvent(source, id, input.trim()), id);
         if (result.value() instanceof Throwable ex) {
            source.sendFailure(Component.literal(ex.toString()));
            return 0;
         } else {
            if (result.value() != null && result.cx() != null) {
               source.sendSuccess(() -> TextWrapper.wrap(result.cx(), result.value()), false);
            }

            return 1;
         }
      } else {
         return 0;
      }
   }

   private static int errors(CommandSourceStack source, ScriptType type) throws CommandSyntaxException {
      if (type == ScriptType.CLIENT) {
         ServerPlayer player = source.getPlayerOrException();
         KubeJSNet.safeSendToPlayer(player, new DisplayClientErrorsPayload());
         return 1;
      } else if (source.getServer().isSingleplayer()) {
         KubeJS.PROXY.openErrors(type);
         return 1;
      } else {
         ServerPlayer player = source.getPlayerOrException();
         ArrayList<ConsoleLine> errors = new ArrayList<>(type.console.errors);
         ArrayList<ConsoleLine> warnings = new ArrayList<>(type.console.warnings);
         player.sendSystemMessage(Component.literal("You need KubeJS on client side!").withStyle(ChatFormatting.RED), true);
         KubeJSNet.safeSendToPlayer(player, new DisplayServerErrorsPayload(type.ordinal(), errors, warnings));
         return 1;
      }
   }

   private static int reloadConfig(CommandSourceStack source) {
      KubeJS.PROXY.reloadConfig();
      source.sendSystemMessage(Component.literal("Done!"));
      return 1;
   }

   private static int reloadStartup(CommandSourceStack source) {
      KubeJS.getStartupScriptManager().reload();
      source.sendSystemMessage(Component.literal("Done!"));
      KubeJSNet.sendToAllPlayers(new ReloadStartupScriptsPayload(source.getServer().isDedicatedServer()));
      return 1;
   }

   private static int reloadServer(CommandSourceStack source) {
      ReloadableResources resources = source.getServer().getServerResources();
      resources.managers().kjs$getServerScriptManager().reload();
      source.sendSuccess(
         () -> Component.literal("Done! To reload recipes, tags, loot tables and other datapack things, run ")
            .append(Component.literal("'/reload'").kjs$clickRunCommand("/reload").kjs$hover(Component.literal("Click to run"))),
         false
      );
      return 1;
   }

   private static int export(CommandSourceStack source) {
      if (DataExport.export != null) {
         return 0;
      } else {
         DataExport.export = new DataExport();
         DataExport.export.source = source;
         source.sendSuccess(() -> Component.literal("Reloading server and exporting data..."), true);
         source.getServer().kjs$runCommand("reload");
         return 1;
      }
   }

   private static void afterReload(CommandSourceStack source) {
      source.sendSuccess(() -> Component.literal("Reloaded!"), true);
   }

   private static int exportPacks(CommandSourceStack source, boolean exportZip) {
      ArrayList<ExportablePackResources> packs = new ArrayList<>();

      for (PackResources pack : source.getServer().getResourceManager().listPacks().toList()) {
         if (pack instanceof ExportablePackResources e) {
            packs.add(e);
         }
      }

      KubeJS.PROXY.export(packs);
      int success = 0;
      Path combinedPath = KubeJSPaths.EXPORTED_PACKS.resolve(exportZip ? "combined.zip" : "combined");

      try {
         if (exportZip) {
            Files.deleteIfExists(combinedPath);
         } else if (Files.exists(combinedPath)) {
            Files.walk(combinedPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
         }
      } catch (Exception var15) {
         var15.printStackTrace();
      }

      for (ExportablePackResources packx : packs) {
         String packName = exportZip ? packx.exportPath() + ".zip" : packx.exportPath();

         try {
            Path path = KubeJSPaths.EXPORTED_PACKS.resolve(packName);
            Path parent = path.getParent();
            if (Files.notExists(parent)) {
               Files.createDirectories(parent);
            }

            if (exportZip) {
               Files.deleteIfExists(path);

               try (FileSystem fs = FileSystems.newFileSystem(path, Map.of("create", true))) {
                  packx.export(fs.getPath("."));
               }

               try (FileSystem fs = FileSystems.newFileSystem(combinedPath, Map.of("create", true))) {
                  packx.export(fs.getPath("."));
               }
            } else {
               if (Files.exists(path)) {
                  Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
               }

               Files.createDirectories(path);
               packx.export(path);
               packx.export(combinedPath);
            }

            source.sendSuccess(() -> Component.empty().append(TextIcons.yes()).append(Component.literal(packName).withStyle(ChatFormatting.BLUE)), false);
            success++;
         } catch (IOException var18) {
            var18.printStackTrace();
            source.sendFailure(
               Component.empty()
                  .append(BATIcons.NO)
                  .append("Failed to export %s!".formatted(packName))
                  .withStyle(
                     style -> style.withColor(ChatFormatting.RED)
                        .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal(var18.getMessage())))
                  )
            );
         }
      }

      int success1 = success;
      if (source.getServer().isSingleplayer() && !source.getServer().isPublished()) {
         source.sendSuccess(
            () -> Component.literal("Exported " + success1 + " packs").kjs$clickOpenFile(KubeJSPaths.EXPORTED_PACKS.toAbsolutePath().toString()), false
         );
      } else {
         source.sendSuccess(() -> Component.literal("Exported " + success1 + " packs"), false);
      }

      return success;
   }

   private static <T> int listTagsFor(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
      Stream<TagKey<T>> tags = allTags(source, registry);
      source.sendSystemMessage(Component.empty());
      source.sendSystemMessage(Component.literal("List of all Tags for " + registry.location() + ":"));
      source.sendSystemMessage(Component.empty());
      long size = tags.<ResourceLocation>map(TagKey::location)
         .map(
            tag -> Component.literal("- %s".formatted(tag))
               .withStyle(
                  Style.EMPTY
                     .withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/kubejs list_tag %s %s".formatted(registry.location(), tag)))
                     .withHoverEvent(
                        new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal("[Show all entries for %s]".formatted(tag)))
                     )
               )
         )
         .mapToLong(msg -> {
            source.sendSystemMessage(msg);
            return 1L;
         })
         .sum();
      source.sendSystemMessage(Component.empty());
      source.sendSystemMessage(Component.literal("Total: %d tags".formatted(size)));
      source.sendSystemMessage(Component.literal("(Click on any of the above tags to list their contents!)"));
      source.sendSystemMessage(Component.empty());
      return 1;
   }

   private static <T> int tagObjects(CommandSourceStack source, TagKey<T> key) throws CommandSyntaxException {
      Registry<T> registry = (Registry<T>)source.registryAccess().registry(key.registry()).orElseThrow(() -> NO_REGISTRY.create(key.registry().location()));
      Optional<Named<T>> tag = registry.getTag(key);
      if (tag.isEmpty()) {
         source.sendFailure(Component.literal("Tag not found or empty!"));
         return 0;
      } else {
         source.sendSystemMessage(Component.empty());
         source.sendSystemMessage(Component.literal("Contents of #" + key.location() + " [" + key.registry().location() + "]:"));
         source.sendSystemMessage(Component.empty());
         Named<T> items = tag.get();

         for (Holder<T> holder : items) {
            String id = (String)holder.unwrap().map(o -> o.location().toString(), o -> o + " (unknown ID)");
            source.sendSystemMessage(Component.literal("- " + id));
         }

         source.sendSystemMessage(Component.empty());
         source.sendSystemMessage(Component.literal("Total: " + items.size() + " elements"));
         source.sendSystemMessage(Component.empty());
         return 1;
      }
   }

   private static int generateTypings(CommandSourceStack source) {
      if (!source.getServer().isSingleplayer()) {
         source.sendFailure(Component.literal("You can only run this command in singleplayer!"));
         return 0;
      } else {
         KubeJS.PROXY.generateTypings(source);
         return 1;
      }
   }

   private static int packmode(CommandSourceStack source, String packmode) {
      if (packmode.isEmpty()) {
         source.sendSuccess(() -> Component.literal("Current packmode: " + CommonProperties.get().packMode), false);
      } else {
         CommonProperties.get().setPackMode(packmode);
         source.sendSuccess(() -> Component.literal("Set packmode to: " + packmode), true);
      }

      return 1;
   }

   private static int eval(CommandSourceStack source, String code) {
      KubeJSContext cx = (KubeJSContext)source.getServer().getServerResources().managers().kjs$getServerScriptManager().contextFactory.enter();
      cx.evaluateString(cx.topLevelScope, code, "eval", 1, null);
      return 1;
   }

   private static int exportRecipeSchemaJson(CommandSourceStack source, ResourceKey<?> id) {
      RecipeSchemaStorage storage = source.getServer().getServerResources().managers().kjs$getServerScriptManager().recipeSchemaStorage;
      RecipeSchemaType schemaType = storage.namespace(id.location().getNamespace()).get(id.location().getPath());
      RegistryOps<JsonElement> ops = source.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
      source.sendSuccess(() -> Component.literal("Check console/log for the exported JSON"), false);
      if (schemaType != null) {
         JsonObject json = schemaType.schema.toJson(storage, schemaType, ops);
         ConsoleJS.SERVER.info("JSON of " + id.location() + ": (May be inaccurate!)\n" + JsonUtils.toPrettyString(json));
      } else {
         ConsoleJS.SERVER.info("Failed to generate JSON of " + id.location());
      }

      return 1;
   }
}
