package com.leonardoinc22.shortgrass.neoforge;

import com.leonardoinc22.shortgrass.client.render.GrassRenderPass;
import com.leonardoinc22.shortgrass.client.render.GrassRenderType;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod("grassiergrass")
public final class ShortGrassMod {
   public static final String MODID = "grassiergrass";

   public ShortGrassMod(IEventBus modEventBus, ModContainer modContainer) {
      modContainer.registerConfig(Type.CLIENT, ShortGrassConfig.SPEC);
      modEventBus.addListener(event -> ShortGrassConfig.sync());
      modEventBus.addListener(event -> ShortGrassConfig.sync());
      GrassConfig.setPersistHook(ShortGrassConfig::writeBack);
      modEventBus.addListener(ShortGrassMod::onRegisterShaders);
      modEventBus.addListener(HiddenBakedModel::onModifyBakingResult);
      NeoForge.EVENT_BUS.addListener(ShortGrassMod::onRegisterClientCommands);
      NeoForge.EVENT_BUS.addListener(ShortGrassMod::onRenderLevelStage);
      modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
   }

   private static void onRenderLevelStage(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_CUTOUT_BLOCKS) {
         GrassRenderPass.render(
            event.getCamera(),
            event.getPartialTick().getGameTimeDeltaPartialTick(true),
            event.getProjectionMatrix(),
            event.getModelViewMatrix(),
            event.getFrustum()
         );
      }
   }

   private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("grassiergrass")
                     .then(
                        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("wind")
                                 .then(
                                    ((LiteralArgumentBuilder)Commands.literal("dynamic")
                                          .executes(
                                             context -> {
                                                GrassConfig.setDynamicWind(!GrassConfig.dynamicWind);
                                                GrassConfig.save();
                                                ((CommandSourceStack)context.getSource())
                                                   .sendSuccess(
                                                      () -> Component.literal("Grassier Grass dynamic wind: " + (GrassConfig.dynamicWind ? "on" : "off")),
                                                      false
                                                   );
                                                return GrassConfig.dynamicWind ? 1 : 0;
                                             }
                                          ))
                                       .then(
                                          Commands.argument("enabled", BoolArgumentType.bool())
                                             .executes(
                                                context -> {
                                                   GrassConfig.setDynamicWind(BoolArgumentType.getBool(context, "enabled"));
                                                   GrassConfig.save();
                                                   ((CommandSourceStack)context.getSource())
                                                      .sendSuccess(
                                                         () -> Component.literal("Grassier Grass dynamic wind: " + (GrassConfig.dynamicWind ? "on" : "off")),
                                                         false
                                                      );
                                                   return GrassConfig.dynamicWind ? 1 : 0;
                                                }
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("direction")
                                    .then(
                                       Commands.argument("degrees", FloatArgumentType.floatArg())
                                          .executes(
                                             context -> {
                                                GrassConfig.setWindDirectionDegrees(FloatArgumentType.getFloat(context, "degrees"));
                                                GrassConfig.save();
                                                ((CommandSourceStack)context.getSource())
                                                   .sendSuccess(
                                                      () -> Component.literal("Grassier Grass wind direction: " + GrassConfig.windDirectionDegrees + " degrees"),
                                                      false
                                                   );
                                                return Math.round(GrassConfig.windDirectionDegrees);
                                             }
                                          )
                                    )
                              ))
                           .then(Commands.argument("speed", IntegerArgumentType.integer(0, 500)).executes(context -> {
                              GrassConfig.setWindSpeed(IntegerArgumentType.getInteger(context, "speed"));
                              GrassConfig.save();
                              ((CommandSourceStack)context.getSource())
                                 .sendSuccess(() -> Component.literal("Grassier Grass wind speed: " + GrassConfig.windSpeed), false);
                              return GrassConfig.windSpeed;
                           }))
                     ))
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("blacklist")
                                    .then(Commands.literal("list").executes(ShortGrassMod::listBlacklist)))
                                 .then(
                                    Commands.literal("add")
                                       .then(
                                          Commands.argument("block", StringArgumentType.greedyString())
                                             .suggests((context, builder) -> suggestKnownBlocks(builder))
                                             .executes(ShortGrassMod::addBlacklist)
                                       )
                                 ))
                              .then(
                                 Commands.literal("remove")
                                    .then(
                                       Commands.argument("block", StringArgumentType.greedyString())
                                          .suggests((context, builder) -> suggestBlacklistedBlocks(builder))
                                          .executes(ShortGrassMod::removeBlacklist)
                                    )
                              ))
                           .then(Commands.literal("clear").executes(ShortGrassMod::clearBlacklist)))
                        .then(Commands.literal("reset").executes(ShortGrassMod::resetBlacklist))
                  ))
               .then(
                  ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("whitelist")
                                 .then(Commands.literal("list").executes(ShortGrassMod::listWhitelist)))
                              .then(
                                 Commands.literal("add")
                                    .then(
                                       Commands.argument("block", StringArgumentType.greedyString())
                                          .suggests((context, builder) -> suggestKnownBlocks(builder))
                                          .executes(ShortGrassMod::addWhitelist)
                                    )
                              ))
                           .then(
                              Commands.literal("remove")
                                 .then(
                                    Commands.argument("block", StringArgumentType.greedyString())
                                       .suggests((context, builder) -> suggestWhitelistedBlocks(builder))
                                       .executes(ShortGrassMod::removeWhitelist)
                                 )
                           ))
                        .then(Commands.literal("clear").executes(ShortGrassMod::clearWhitelist)))
                     .then(Commands.literal("reset").executes(ShortGrassMod::resetWhitelist))
               )
         );
   }

   private static int listBlacklist(CommandContext<CommandSourceStack> context) {
      List<String> ids = GrassConfig.plantBlacklistIds();
      ((CommandSourceStack)context.getSource())
         .sendSuccess(() -> Component.literal("Grassier Grass plant blacklist: " + (ids.isEmpty() ? "(empty)" : String.join(", ", ids))), false);
      return ids.size();
   }

   private static int addBlacklist(CommandContext<CommandSourceStack> context) {
      ResourceLocation id = parseCommandBlock(context);
      if (id == null) {
         ((CommandSourceStack)context.getSource())
            .sendSuccess(() -> Component.literal("Unknown block id: " + StringArgumentType.getString(context, "block")), false);
         return 0;
      } else {
         boolean changed = GrassConfig.addPlantBlacklist(id);
         saveAndFlushBlacklist();
         ((CommandSourceStack)context.getSource())
            .sendSuccess(() -> Component.literal("Grassier Grass plant blacklist: " + id + (changed ? " added" : " was already present")), false);
         return changed ? 1 : 0;
      }
   }

   private static int removeBlacklist(CommandContext<CommandSourceStack> context) {
      ResourceLocation id = parseCommandBlock(context);
      if (id == null) {
         ((CommandSourceStack)context.getSource())
            .sendSuccess(() -> Component.literal("Unknown block id: " + StringArgumentType.getString(context, "block")), false);
         return 0;
      } else {
         boolean changed = GrassConfig.removePlantBlacklist(id);
         saveAndFlushBlacklist();
         ((CommandSourceStack)context.getSource())
            .sendSuccess(() -> Component.literal("Grassier Grass plant blacklist: " + id + (changed ? " removed" : " was not present")), false);
         return changed ? 1 : 0;
      }
   }

   private static int clearBlacklist(CommandContext<CommandSourceStack> context) {
      boolean changed = GrassConfig.clearPlantBlacklist();
      saveAndFlushBlacklist();
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("Grassier Grass plant blacklist cleared"), false);
      return changed ? 1 : 0;
   }

   private static int resetBlacklist(CommandContext<CommandSourceStack> context) {
      boolean changed = GrassConfig.resetPlantBlacklist();
      saveAndFlushBlacklist();
      ((CommandSourceStack)context.getSource())
         .sendSuccess(() -> Component.literal("Grassier Grass plant blacklist reset to defaults: " + String.join(", ", GrassConfig.plantBlacklistIds())), false);
      return changed ? 1 : 0;
   }

   private static int listWhitelist(CommandContext<CommandSourceStack> context) {
      List<String> ids = GrassConfig.plantWhitelistIds();
      ((CommandSourceStack)context.getSource())
         .sendSuccess(() -> Component.literal("Grassier Grass plant whitelist: " + (ids.isEmpty() ? "(empty)" : String.join(", ", ids))), false);
      return ids.size();
   }

   private static int addWhitelist(CommandContext<CommandSourceStack> context) {
      ResourceLocation id = parseCommandBlock(context);
      if (id == null) {
         ((CommandSourceStack)context.getSource())
            .sendSuccess(() -> Component.literal("Unknown block id: " + StringArgumentType.getString(context, "block")), false);
         return 0;
      } else {
         boolean changed = GrassConfig.addPlantWhitelist(id);
         saveAndFlushWhitelist();
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               () -> Component.literal("Grassier Grass plant whitelist: " + id + (changed ? " added (reloading resources)" : " was already present")), false
            );
         return changed ? 1 : 0;
      }
   }

   private static int removeWhitelist(CommandContext<CommandSourceStack> context) {
      ResourceLocation id = parseCommandBlock(context);
      if (id == null) {
         ((CommandSourceStack)context.getSource())
            .sendSuccess(() -> Component.literal("Unknown block id: " + StringArgumentType.getString(context, "block")), false);
         return 0;
      } else {
         boolean changed = GrassConfig.removePlantWhitelist(id);
         saveAndFlushWhitelist();
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               () -> Component.literal("Grassier Grass plant whitelist: " + id + (changed ? " removed (reloading resources)" : " was not present")), false
            );
         return changed ? 1 : 0;
      }
   }

   private static int clearWhitelist(CommandContext<CommandSourceStack> context) {
      boolean changed = GrassConfig.clearPlantWhitelist();
      saveAndFlushWhitelist();
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("Grassier Grass plant whitelist cleared (reloading resources)"), false);
      return changed ? 1 : 0;
   }

   private static int resetWhitelist(CommandContext<CommandSourceStack> context) {
      boolean changed = GrassConfig.resetPlantWhitelist();
      saveAndFlushWhitelist();
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("Grassier Grass plant whitelist reset (reloading resources)"), false);
      return changed ? 1 : 0;
   }

   private static ResourceLocation parseCommandBlock(CommandContext<CommandSourceStack> context) {
      return GrassConfig.parseKnownBlockIdentifier(StringArgumentType.getString(context, "block"));
   }

   private static void saveAndFlushBlacklist() {
      GrassConfig.save();
      GrassRenderPass.flushAllGeometry();
   }

   private static void saveAndFlushWhitelist() {
      GrassConfig.save();
      GrassRenderPass.flushAllGeometry();
      Minecraft.getInstance().reloadResourcePacks();
   }

   private static CompletableFuture<Suggestions> suggestKnownBlocks(SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (String id : GrassConfig.knownBlockIds()) {
         if (id.startsWith(remaining)) {
            builder.suggest(id);
         }
      }

      return builder.buildFuture();
   }

   private static CompletableFuture<Suggestions> suggestBlacklistedBlocks(SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (String id : GrassConfig.plantBlacklistIds()) {
         if (id.startsWith(remaining)) {
            builder.suggest(id);
         }
      }

      return builder.buildFuture();
   }

   private static CompletableFuture<Suggestions> suggestWhitelistedBlocks(SuggestionsBuilder builder) {
      String remaining = builder.getRemainingLowerCase();

      for (String id : GrassConfig.plantWhitelistIds()) {
         if (id.startsWith(remaining)) {
            builder.suggest(id);
         }
      }

      return builder.buildFuture();
   }

   private static void onRegisterShaders(RegisterShadersEvent event) {
      try {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("grassiergrass", "grass_blades"), DefaultVertexFormat.BLOCK),
            GrassRenderType::setGrassShader
         );
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("grassiergrass", "grass_plant"), DefaultVertexFormat.BLOCK),
            GrassRenderType::setPlantShader
         );
      } catch (IOException var2) {
         throw new IllegalStateException("Failed to load grassiergrass shaders", var2);
      }
   }
}
