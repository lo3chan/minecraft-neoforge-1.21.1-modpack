package io.wispforest.owo.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import io.wispforest.owo.Owo;
import io.wispforest.owo.command.EnumArgumentType;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.renderdoc.RenderDoc;
import io.wispforest.owo.renderdoc.RenderdocScreen;
import io.wispforest.owo.ui.hud.HudInspectorScreen;
import io.wispforest.owo.ui.parsing.ConfigureHotReloadScreen;
import io.wispforest.owo.ui.parsing.UIModelLoader;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.event.Level;

@Internal
public class OwoDebugCommands {
   private static EnumArgumentType<Level> LEVEL_ARGUMENT_TYPE;
   private static final SuggestionProvider<CommandSourceStack> POI_TYPES = (context, builder) -> SharedSuggestionProvider.suggestResource(
      BuiltInRegistries.POINT_OF_INTEREST_TYPE.keySet(), builder
   );
   private static final SimpleCommandExceptionType NO_POI_TYPE = new SimpleCommandExceptionType(Component.nullToEmpty("Invalid POI type"));
   public static final int GENERAL_PURPLE = 12157951;
   public static final int KEY_BLUE = 9745405;
   public static final int VALUE_BLUE = 9755391;

   public static void register(IEventBus modBus) {
      modBus.addListener(
         RegisterEvent.class,
         event -> event.register(
            Registries.COMMAND_ARGUMENT_TYPE, helper -> LEVEL_ARGUMENT_TYPE = EnumArgumentType.create(Level.class, "'{}' is not a valid logging level")
         )
      );
      NeoForge.EVENT_BUS
         .addListener(
            event -> {
               CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
               CommandBuildContext registryAccess = event.getBuildContext();
               dispatcher.register(
                  (LiteralArgumentBuilder)Commands.literal("logger")
                     .then(
                        Commands.argument("level", LEVEL_ARGUMENT_TYPE)
                           .executes(
                              context -> {
                                 Level level = (Level)LEVEL_ARGUMENT_TYPE.get(context, "level");
                                 LogUtils.configureRootLoggingLevel(level);
                                 ((CommandSourceStack)context.getSource())
                                    .sendSuccess(() -> TextOps.concat(Owo.PREFIX, Component.nullToEmpty("global logging level set to: §9" + level)), false);
                                 return 0;
                              }
                           )
                     )
               );
               dispatcher.register(
                  (LiteralArgumentBuilder)Commands.literal("query-poi")
                     .then(
                        Commands.argument("poi_type", ResourceLocationArgument.id())
                           .suggests(POI_TYPES)
                           .then(
                              Commands.argument("radius", IntegerArgumentType.integer())
                                 .executes(
                                    context -> {
                                       ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayer();
                                       PoiType poiType = (PoiType)BuiltInRegistries.POINT_OF_INTEREST_TYPE
                                          .getOptional(ResourceLocationArgument.getId(context, "poi_type"))
                                          .orElseThrow(NO_POI_TYPE::create);
                                       List<PoiRecord> entries = ((ServerLevel)player.level())
                                          .getPoiManager()
                                          .getInRange(
                                             type -> type.value() == poiType,
                                             player.blockPosition(),
                                             IntegerArgumentType.getInteger(context, "radius"),
                                             Occupancy.ANY
                                          )
                                          .toList();
                                       player.displayClientMessage(
                                          TextOps.concat(
                                             Owo.PREFIX,
                                             TextOps.withColor(
                                                "Found §" + entries.size() + " §entr" + (entries.size() == 1 ? "y" : "ies"),
                                                TextOps.color(ChatFormatting.GRAY),
                                                12157951,
                                                TextOps.color(ChatFormatting.GRAY)
                                             )
                                          ),
                                          false
                                       );

                                       for (PoiRecord entry : entries) {
                                          BlockPos entryPos = entry.getPos();
                                          String blockId = BuiltInRegistries.BLOCK.getKey(player.level().getBlockState(entryPos).getBlock()).toString();
                                          String posString = "(" + entryPos.getX() + " " + entryPos.getY() + " " + entryPos.getZ() + ")";
                                          MutableComponent message = TextOps.withColor(
                                             "-> §" + blockId + " §" + posString, TextOps.color(ChatFormatting.GRAY), 9745405, 9755391
                                          );
                                          message.withStyle(
                                             style -> style.withClickEvent(
                                                   new ClickEvent(
                                                      Action.SUGGEST_COMMAND, "/tp " + entryPos.getX() + " " + entryPos.getY() + " " + entryPos.getZ()
                                                   )
                                                )
                                                .withHoverEvent(
                                                   new HoverEvent(
                                                      net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.nullToEmpty("Click to teleport")
                                                   )
                                                )
                                          );
                                          player.displayClientMessage(message, false);
                                       }

                                       return entries.size();
                                    }
                                 )
                           )
                     )
               );
               dispatcher.register(
                  (LiteralArgumentBuilder)Commands.literal("dumpfield")
                     .then(
                        Commands.argument("field_name", StringArgumentType.string())
                           .executes(
                              context -> {
                                 String targetField = StringArgumentType.getString(context, "field_name");
                                 CommandSourceStack source = (CommandSourceStack)context.getSource();
                                 ServerPlayer player = source.getPlayer();
                                 HitResult target = player.pick(5.0, 0.0F, false);
                                 if (target.getType() != Type.BLOCK) {
                                    source.sendFailure(TextOps.concat(Owo.PREFIX, Component.literal("You're not looking at a block")));
                                    return 1;
                                 } else {
                                    BlockPos pos = ((BlockHitResult)target).getBlockPos();
                                    BlockEntity blockEntity = player.level().getBlockEntity(pos);
                                    if (blockEntity == null) {
                                       source.sendFailure(TextOps.concat(Owo.PREFIX, Component.literal("No block entity")));
                                       return 1;
                                    } else {
                                       Class<? extends BlockEntity> blockEntityClass = (Class<? extends BlockEntity>)blockEntity.getClass();

                                       try {
                                          Field field = blockEntityClass.getDeclaredField(targetField);
                                          if (!field.canAccess(blockEntity)) {
                                             field.setAccessible(true);
                                          }

                                          Object value = field.get(blockEntity);
                                          source.sendSuccess(
                                             () -> TextOps.concat(
                                                Owo.PREFIX, TextOps.withColor("Field value: §" + value, TextOps.color(ChatFormatting.GRAY), 9745405)
                                             ),
                                             false
                                          );
                                       } catch (Exception var10) {
                                          source.sendFailure(
                                             TextOps.concat(
                                                Owo.PREFIX,
                                                Component.literal("Could not access field - " + var10.getClass().getSimpleName() + ": " + var10.getMessage())
                                             )
                                          );
                                       }

                                       return 0;
                                    }
                                 }
                              }
                           )
                     )
               );
               MakeLootContainerCommand.register(dispatcher, registryAccess);
               DumpdataCommand.register(dispatcher);
               HealCommand.register(dispatcher);
            }
         );
   }

   @OnlyIn(Dist.CLIENT)
   public static class Client {
      private static final SuggestionProvider<CommandSourceStack> LOADED_UI_MODELS = (context, builder) -> SharedSuggestionProvider.suggestResource(
         UIModelLoader.allLoadedModels(), builder
      );
      private static final SimpleCommandExceptionType NO_SUCH_UI_MODEL = new SimpleCommandExceptionType(Component.literal("No such UI model is loaded"));

      public static void register() {
         NeoForge.EVENT_BUS
            .addListener(
               event -> {
                  CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
                  CommandBuildContext registryAccess = event.getBuildContext();
                  dispatcher.register((LiteralArgumentBuilder)Commands.literal("owo-hud-inspect").executes(context -> {
                     Minecraft.getInstance().setScreen(new HudInspectorScreen());
                     return 0;
                  }));
                  dispatcher.register(
                     (LiteralArgumentBuilder)Commands.literal("owo-ui-set-reload-path")
                        .then(Commands.argument("model-id", ResourceLocationArgument.id()).suggests(LOADED_UI_MODELS).executes(context -> {
                           ResourceLocation modelId = (ResourceLocation)context.getArgument("model-id", ResourceLocation.class);
                           if (UIModelLoader.getPreloaded(modelId) == null) {
                              throw NO_SUCH_UI_MODEL.create();
                           } else {
                              Minecraft.getInstance().setScreen(new ConfigureHotReloadScreen(modelId, null));
                              return 0;
                           }
                        }))
                  );
                  if (RenderDoc.isAvailable()) {
                     dispatcher.register(
                        (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("renderdoc").executes(context -> {
                              Minecraft.getInstance().setScreen(new RenderdocScreen());
                              return 1;
                           }))
                           .then(
                              Commands.literal("comment")
                                 .then(
                                    Commands.argument("capture_index", IntegerArgumentType.integer(0))
                                       .then(
                                          Commands.argument("comment", StringArgumentType.greedyString())
                                             .executes(
                                                context -> {
                                                   RenderDoc.Capture capture = RenderDoc.getCapture(IntegerArgumentType.getInteger(context, "capture_index"));
                                                   if (capture == null) {
                                                      ((CommandSourceStack)context.getSource())
                                                         .sendFailure(TextOps.concat(Owo.PREFIX, Component.nullToEmpty("no such capture")));
                                                      return 0;
                                                   } else {
                                                      RenderDoc.setCaptureComments(capture, StringArgumentType.getString(context, "comment"));
                                                      ((CommandSourceStack)context.getSource())
                                                         .sendSuccess(() -> TextOps.concat(Owo.PREFIX, Component.nullToEmpty("comment updated")), false);
                                                      return 1;
                                                   }
                                                }
                                             )
                                       )
                                 )
                           )
                     );
                  }
               }
            );
      }
   }
}
