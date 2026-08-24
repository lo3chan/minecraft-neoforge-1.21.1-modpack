package com.finndog.moogs_structures.commands;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import com.finndog.moogs_structures.utils.DebugFlags;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class DebugCommand {
   private DebugCommand() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("moogs_structures").requires(source -> source.hasPermission(2)))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug")
                              .executes(context -> toggle((CommandSourceStack)context.getSource())))
                           .then(Commands.literal("on").executes(context -> set((CommandSourceStack)context.getSource(), true))))
                        .then(Commands.literal("off").executes(context -> set((CommandSourceStack)context.getSource(), false))))
                     .then(Commands.literal("status").executes(context -> report((CommandSourceStack)context.getSource()))))
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("keepjigsaws")
                                 .executes(context -> toggleKeepJigsaws((CommandSourceStack)context.getSource())))
                              .then(Commands.literal("on").executes(context -> setKeepJigsaws((CommandSourceStack)context.getSource(), true))))
                           .then(Commands.literal("off").executes(context -> setKeepJigsaws((CommandSourceStack)context.getSource(), false))))
                        .then(Commands.literal("status").executes(context -> reportKeepJigsaws((CommandSourceStack)context.getSource())))
                  )
            )
      );
   }

   private static int toggle(CommandSourceStack source) {
      boolean enabled = DebugFlags.toggle();
      return notify(source, "toggled", enabled);
   }

   private static int set(CommandSourceStack source, boolean value) {
      boolean enabled = DebugFlags.setEnabled(value);
      return notify(source, value ? "enabled" : "disabled", enabled);
   }

   private static int report(CommandSourceStack source) {
      boolean enabled = DebugFlags.isEnabled();
      source.sendSuccess(() -> Component.literal("Moog's Structure debug mode is " + (enabled ? "enabled" : "disabled")), false);
      return enabled ? 1 : 0;
   }

   private static int notify(CommandSourceStack source, String action, boolean enabled) {
      String message = "Moog's Structure debug mode " + action + " (" + (enabled ? "enabled" : "disabled") + ")";
      MoogsStructuresCommon.LOGGER.info(message);
      source.sendSuccess(() -> Component.literal(message), true);
      return enabled ? 1 : 0;
   }

   private static int toggleKeepJigsaws(CommandSourceStack source) {
      boolean enabled = DebugFlags.toggleKeepJigsawBlocks();
      return notifyKeepJigsaws(source, "toggled", enabled);
   }

   private static int setKeepJigsaws(CommandSourceStack source, boolean value) {
      boolean enabled = DebugFlags.setKeepJigsawBlocks(value);
      return notifyKeepJigsaws(source, value ? "enabled" : "disabled", enabled);
   }

   private static int reportKeepJigsaws(CommandSourceStack source) {
      boolean enabled = DebugFlags.isKeepJigsawBlocks();
      source.sendSuccess(() -> Component.literal("Moog's Structure keep-jigsaw-blocks mode is " + (enabled ? "enabled" : "disabled")), false);
      return enabled ? 1 : 0;
   }

   private static int notifyKeepJigsaws(CommandSourceStack source, String action, boolean enabled) {
      String message = "Moog's Structure keep-jigsaw-blocks mode "
         + action
         + " ("
         + (enabled ? "enabled" : "disabled")
         + "). Newly generated/placed structures will "
         + (enabled ? "retain" : "no longer retain")
         + " jigsaw blocks.";
      MoogsStructuresCommon.LOGGER.info(message);
      source.sendSuccess(() -> Component.literal(message), true);
      return enabled ? 1 : 0;
   }
}
