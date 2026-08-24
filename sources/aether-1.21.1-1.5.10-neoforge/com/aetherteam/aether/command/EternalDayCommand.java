package com.aetherteam.aether.command;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class EternalDayCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("aether")
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("eternal_day")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2)))
                     .then(
                        Commands.literal("set")
                           .then(
                              Commands.argument("option", BoolArgumentType.bool())
                                 .suggests((context, builder) -> SharedSuggestionProvider.suggest(BoolArgumentType.bool().getExamples(), builder))
                                 .executes(context -> setEternalDay((CommandSourceStack)context.getSource(), BoolArgumentType.getBool(context, "option")))
                           )
                     ))
                  .then(Commands.literal("query").executes(context -> queryEternalDay((CommandSourceStack)context.getSource())))
            )
      );
   }

   private static int setEternalDay(CommandSourceStack source, boolean value) {
      ServerLevel level = source.getLevel();
      if (level.hasData(AetherDataAttachments.AETHER_TIME)) {
         AetherTimeAttachment data = (AetherTimeAttachment)level.getData(AetherDataAttachments.AETHER_TIME);
         data.setEternalDay(value);
         data.updateEternalDay(level);
         if ((Boolean)AetherConfig.SERVER.sync_aether_time.get()) {
            data.setSynched(-1, Direction.DIMENSION, "setShouldWait", true, level);
         }

         source.sendSuccess(() -> Component.translatable("commands.aether.capability.time.eternal_day.set", new Object[]{value}), true);
      }

      return 1;
   }

   private static int queryEternalDay(CommandSourceStack source) {
      ServerLevel level = source.getLevel();
      if (level.hasData(AetherDataAttachments.AETHER_TIME)) {
         source.sendSuccess(
            () -> Component.translatable(
               "commands.aether.capability.time.eternal_day.query",
               new Object[]{((AetherTimeAttachment)level.getData(AetherDataAttachments.AETHER_TIME)).isEternalDay()}
            ),
            true
         );
      }

      return 1;
   }
}
