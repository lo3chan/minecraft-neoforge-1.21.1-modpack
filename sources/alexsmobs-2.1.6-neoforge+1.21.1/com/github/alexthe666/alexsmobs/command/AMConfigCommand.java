package com.github.alexthe666.alexsmobs.command;

import codx.codxlib.api.settings.CodxSettingsCommand;
import codx.codxlib.api.settings.CodxSettings.ConfigValue;
import codx.codxlib.api.settings.CodxSettingsCommand.ChangeNote;
import codx.codxlib.api.ui.menu.CodxSettingsMenu;
import com.github.alexthe666.alexsmobs.config.ConfigHolder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Locale;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;

public final class AMConfigCommand {
   private static final Set<String> LIVE_SPAWN_OPTIONS = Set.of(
      "restrictfarseerspawns", "farseerborderspawndistance", "beachedcachalotwhales", "beachedcachalotwhalespawnchance", "beachedcachalotwhalespawndelay"
   );
   private static final ChangeNote RELOAD_NOTE = changed -> changed != null && !needsWorldReload(changed)
      ? null
      : "Spawn settings are applied when a world loads — restart the server (or quit to the title screen and re-enter) for those to take effect.";

   private AMConfigCommand() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> root = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("amc").requires(CodxSettingsCommand::isOperator))
               .then(Commands.literal("menu").executes(AMConfigCommand::menuCommand)))
            .then(CodxSettingsCommand.node("config", ConfigHolder.COMMON_SPEC, RELOAD_NOTE, AMConfigCommand::openMenu))
      );
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("aac").requires(CodxSettingsCommand::isOperator)).redirect(root));
   }

   private static int menuCommand(CommandContext<CommandSourceStack> context) {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayer();
      if (player == null) {
         ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("Only a player can open the settings menu."));
         return 0;
      } else {
         openMenu(player);
         return 1;
      }
   }

   private static void openMenu(ServerPlayer player) {
      CodxSettingsMenu.builder(ConfigHolder.COMMON_SPEC)
         .title("§6§lAlex's Mobs Continued")
         .command("/amc config")
         .note(RELOAD_NOTE)
         .icon("general", Items.BOOK)
         .icon("spawning", Items.EGG)
         .icon("uniqueSpawning", Items.END_PORTAL_FRAME)
         .icon("dangerZone", Items.TNT)
         .group("spawning", AMConfigCommand::spawnGroup)
         .open(player);
   }

   private static String spawnGroup(ConfigValue<?> value) {
      String name = value.name();
      int spawn = name.indexOf("Spawn");
      return spawn > 0 ? name.substring(0, spawn) : name;
   }

   private static boolean needsWorldReload(ConfigValue<?> value) {
      String category = value.category();
      return ("spawning".equals(category) || "uniqueSpawning".equals(category)) && !LIVE_SPAWN_OPTIONS.contains(value.name().toLowerCase(Locale.ROOT));
   }
}
