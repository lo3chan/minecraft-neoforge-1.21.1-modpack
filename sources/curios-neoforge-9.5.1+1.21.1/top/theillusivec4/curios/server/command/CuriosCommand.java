package top.theillusivec4.curios.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.data.CuriosEntityManager;
import top.theillusivec4.curios.common.data.CuriosSlotManager;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncCurios;

public class CuriosCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
      LiteralArgumentBuilder<CommandSourceStack> curiosCommand = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("curios")
         .requires(player -> player.hasPermission(2));
      curiosCommand.then(Commands.literal("list").executes(context -> {
         Map<String, Set<String>> map = new HashMap<>();

         for (Entry<String, Set<String>> entry : CuriosSlotManager.SERVER.getModsFromSlots().entrySet()) {
            map.computeIfAbsent(entry.getKey(), k -> new HashSet<>()).addAll(entry.getValue());
         }

         for (Entry<String, Set<String>> entry : CuriosEntityManager.SERVER.getModsFromSlots().entrySet()) {
            map.computeIfAbsent(entry.getKey(), k -> new HashSet<>()).addAll(entry.getValue());
         }

         for (Entry<String, Set<String>> entry : map.entrySet()) {
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal(entry.getKey() + " - " + String.join(", ", entry.getValue())), false);
         }

         return 1;
      }));
      curiosCommand.then(
         Commands.literal("replace")
            .then(
               Commands.argument("slot", CurioArgumentType.slot())
                  .then(
                     Commands.argument("index", IntegerArgumentType.integer())
                        .then(
                           Commands.argument("player", EntityArgument.player())
                              .then(
                                 Commands.literal("with")
                                    .then(
                                       ((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item(buildContext))
                                             .executes(
                                                context -> replaceItemForPlayer(
                                                   (CommandSourceStack)context.getSource(),
                                                   EntityArgument.getPlayer(context, "player"),
                                                   CurioArgumentType.getSlot(context, "slot"),
                                                   IntegerArgumentType.getInteger(context, "index"),
                                                   ItemArgument.getItem(context, "item")
                                                )
                                             ))
                                          .then(
                                             Commands.argument("count", IntegerArgumentType.integer())
                                                .executes(
                                                   context -> replaceItemForPlayer(
                                                      (CommandSourceStack)context.getSource(),
                                                      EntityArgument.getPlayer(context, "player"),
                                                      CurioArgumentType.getSlot(context, "slot"),
                                                      IntegerArgumentType.getInteger(context, "index"),
                                                      ItemArgument.getItem(context, "item"),
                                                      IntegerArgumentType.getInteger(context, "count")
                                                   )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
      curiosCommand.then(
         Commands.literal("set")
            .then(
               Commands.argument("slot", CurioArgumentType.slot())
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("player", EntityArgument.player())
                           .executes(
                              context -> setSlotsForPlayer(
                                 (CommandSourceStack)context.getSource(),
                                 EntityArgument.getPlayer(context, "player"),
                                 CurioArgumentType.getSlot(context, "slot"),
                                 1
                              )
                           ))
                        .then(
                           Commands.argument("amount", IntegerArgumentType.integer())
                              .executes(
                                 context -> setSlotsForPlayer(
                                    (CommandSourceStack)context.getSource(),
                                    EntityArgument.getPlayer(context, "player"),
                                    CurioArgumentType.getSlot(context, "slot"),
                                    IntegerArgumentType.getInteger(context, "amount")
                                 )
                              )
                        )
                  )
            )
      );
      curiosCommand.then(
         Commands.literal("add")
            .then(
               Commands.argument("slot", CurioArgumentType.slot())
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("player", EntityArgument.player())
                           .executes(
                              context -> growSlotForPlayer(
                                 (CommandSourceStack)context.getSource(),
                                 EntityArgument.getPlayer(context, "player"),
                                 CurioArgumentType.getSlot(context, "slot"),
                                 1
                              )
                           ))
                        .then(
                           Commands.argument("amount", IntegerArgumentType.integer())
                              .executes(
                                 context -> growSlotForPlayer(
                                    (CommandSourceStack)context.getSource(),
                                    EntityArgument.getPlayer(context, "player"),
                                    CurioArgumentType.getSlot(context, "slot"),
                                    IntegerArgumentType.getInteger(context, "amount")
                                 )
                              )
                        )
                  )
            )
      );
      curiosCommand.then(
         Commands.literal("remove")
            .then(
               Commands.argument("slot", CurioArgumentType.slot())
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("player", EntityArgument.player())
                           .executes(
                              context -> shrinkSlotForPlayer(
                                 (CommandSourceStack)context.getSource(),
                                 EntityArgument.getPlayer(context, "player"),
                                 CurioArgumentType.getSlot(context, "slot"),
                                 1
                              )
                           ))
                        .then(
                           Commands.argument("amount", IntegerArgumentType.integer())
                              .executes(
                                 context -> shrinkSlotForPlayer(
                                    (CommandSourceStack)context.getSource(),
                                    EntityArgument.getPlayer(context, "player"),
                                    CurioArgumentType.getSlot(context, "slot"),
                                    IntegerArgumentType.getInteger(context, "amount")
                                 )
                              )
                        )
                  )
            )
      );
      curiosCommand.then(
         Commands.literal("clear")
            .then(
               ((RequiredArgumentBuilder)Commands.argument("player", EntityArgument.player())
                     .executes(context -> clearSlotsForPlayer((CommandSourceStack)context.getSource(), EntityArgument.getPlayer(context, "player"), "")))
                  .then(
                     Commands.argument("slot", CurioArgumentType.slot())
                        .executes(
                           context -> clearSlotsForPlayer(
                              (CommandSourceStack)context.getSource(), EntityArgument.getPlayer(context, "player"), CurioArgumentType.getSlot(context, "slot")
                           )
                        )
                  )
            )
      );
      curiosCommand.then(
         Commands.literal("drop")
            .then(
               ((RequiredArgumentBuilder)Commands.argument("player", EntityArgument.player())
                     .executes(context -> dropSlotsForPlayer((CommandSourceStack)context.getSource(), EntityArgument.getPlayer(context, "player"), "")))
                  .then(
                     Commands.argument("slot", CurioArgumentType.slot())
                        .executes(
                           context -> dropSlotsForPlayer(
                              (CommandSourceStack)context.getSource(), EntityArgument.getPlayer(context, "player"), CurioArgumentType.getSlot(context, "slot")
                           )
                        )
                  )
            )
      );
      curiosCommand.then(
         Commands.literal("reset")
            .then(
               Commands.argument("player", EntityArgument.player())
                  .executes(context -> resetSlotsForPlayer((CommandSourceStack)context.getSource(), EntityArgument.getPlayer(context, "player")))
            )
      );
      dispatcher.register(curiosCommand);
   }

   private static int replaceItemForPlayer(CommandSourceStack source, ServerPlayer player, String slot, int index, ItemInput item) throws CommandSyntaxException {
      return replaceItemForPlayer(source, player, slot, index, item, 1);
   }

   private static int replaceItemForPlayer(CommandSourceStack source, ServerPlayer player, String slot, int index, ItemInput item, int count) throws CommandSyntaxException {
      ItemStack stack = item.createItemStack(count, false);
      CuriosApi.getCuriosHelper().setEquippedCurio(player, slot, index, stack);
      source.sendSuccess(
         () -> Component.translatable("commands.curios.replace.success", new Object[]{slot, player.getDisplayName(), stack.getDisplayName()}), true
      );
      return 1;
   }

   private static int setSlotsForPlayer(CommandSourceStack source, ServerPlayer playerMP, String slot, int amount) {
      CuriosApi.getSlotHelper().setSlotsForType(slot, playerMP, amount);
      source.sendSuccess(
         () -> Component.translatable(
            "commands.curios.set.success", new Object[]{slot, CuriosApi.getSlotHelper().getSlotsForType(playerMP, slot), playerMP.getDisplayName()}
         ),
         true
      );
      return 1;
   }

   private static int growSlotForPlayer(CommandSourceStack source, ServerPlayer playerMP, String slot, int amount) {
      CuriosApi.getSlotHelper().growSlotType(slot, amount, playerMP);
      source.sendSuccess(() -> Component.translatable("commands.curios.add.success", new Object[]{amount, slot, playerMP.getDisplayName()}), true);
      return 1;
   }

   private static int shrinkSlotForPlayer(CommandSourceStack source, ServerPlayer playerMP, String slot, int amount) {
      CuriosApi.getSlotHelper().shrinkSlotType(slot, amount, playerMP);
      source.sendSuccess(() -> Component.translatable("commands.curios.remove.success", new Object[]{amount, slot, playerMP.getDisplayName()}), true);
      return 1;
   }

   private static int dropSlotsForPlayer(CommandSourceStack source, ServerPlayer playerMP, String slot) {
      CuriosApi.getCuriosHelper().getCuriosHandler(playerMP).ifPresent(handler -> {
         Map<String, ICurioStacksHandler> curios = handler.getCurios();
         if (!slot.isEmpty() && curios.get(slot) != null) {
            drop(curios.get(slot), playerMP);
         } else {
            for (String id : curios.keySet()) {
               drop(curios.get(id), playerMP);
            }
         }
      });
      if (slot.isEmpty()) {
         source.sendSuccess(() -> Component.translatable("commands.curios.dropAll.success", new Object[]{playerMP.getDisplayName()}), true);
      } else {
         source.sendSuccess(() -> Component.translatable("commands.curios.drop.success", new Object[]{slot, playerMP.getDisplayName()}), true);
      }

      return 1;
   }

   private static void drop(ICurioStacksHandler stacksHandler, ServerPlayer serverPlayer) {
      for (int i = 0; i < stacksHandler.getSlots(); i++) {
         ItemStack stack1 = stacksHandler.getStacks().getStackInSlot(i);
         stacksHandler.getStacks().setStackInSlot(i, ItemStack.EMPTY);
         ItemStack stack2 = stacksHandler.getCosmeticStacks().getStackInSlot(i);
         stacksHandler.getCosmeticStacks().setStackInSlot(i, ItemStack.EMPTY);
         if (!stack1.isEmpty()) {
            serverPlayer.drop(stack1, true, false);
         }

         if (!stack2.isEmpty()) {
            serverPlayer.drop(stack2, true, false);
         }
      }
   }

   private static int clearSlotsForPlayer(CommandSourceStack source, ServerPlayer playerMP, String slot) {
      CuriosApi.getCuriosHelper().getCuriosHandler(playerMP).ifPresent(handler -> {
         Map<String, ICurioStacksHandler> curios = handler.getCurios();
         if (!slot.isEmpty() && curios.get(slot) != null) {
            clear(curios.get(slot));
         } else {
            for (String id : curios.keySet()) {
               clear(curios.get(id));
            }
         }
      });
      if (slot.isEmpty()) {
         source.sendSuccess(() -> Component.translatable("commands.curios.clearAll.success", new Object[]{playerMP.getDisplayName()}), true);
      } else {
         source.sendSuccess(() -> Component.translatable("commands.curios.clear.success", new Object[]{slot, playerMP.getDisplayName()}), true);
      }

      return 1;
   }

   private static int resetSlotsForPlayer(CommandSourceStack source, ServerPlayer playerMP) {
      CuriosApi.getCuriosHelper()
         .getCuriosHandler(playerMP)
         .ifPresent(
            handler -> {
               handler.reset();
               PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                  playerMP, new SPacketSyncCurios(playerMP.getId(), handler.getCurios()), new CustomPacketPayload[0]
               );
            }
         );
      source.sendSuccess(() -> Component.translatable("commands.curios.reset.success", new Object[]{playerMP.getDisplayName()}), true);
      return 1;
   }

   private static void clear(ICurioStacksHandler stacksHandler) {
      for (int i = 0; i < stacksHandler.getSlots(); i++) {
         stacksHandler.getStacks().setStackInSlot(i, ItemStack.EMPTY);
         stacksHandler.getCosmeticStacks().setStackInSlot(i, ItemStack.EMPTY);
      }
   }
}
