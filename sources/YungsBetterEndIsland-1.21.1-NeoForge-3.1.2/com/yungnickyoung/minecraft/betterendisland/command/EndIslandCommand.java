package com.yungnickyoung.minecraft.betterendisland.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.yungnickyoung.minecraft.betterendisland.world.IBetterDragonFight;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class EndIslandCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, CommandSelection selection) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("end_island").requires(source -> source.hasPermission(2)))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("reset").executes(context -> executeReset((CommandSourceStack)context.getSource(), false)))
                  .then(
                     Commands.argument("forceNewPortalPos", BoolArgumentType.bool())
                        .executes(context -> executeReset((CommandSourceStack)context.getSource(), BoolArgumentType.getBool(context, "forceNewPortalPos")))
                  )
            )
      );
   }

   public static int executeReset(CommandSourceStack commandSource, boolean forcePortalPosReset) {
      ServerLevel serverLevel = commandSource.getServer().getLevel(Level.END);
      if (serverLevel == null) {
         commandSource.sendFailure(Component.literal("Could not find the End dimension."));
         return -1;
      } else if (serverLevel.getDragonFight() == null) {
         commandSource.sendFailure(Component.literal("Could not find the dragon fight."));
         return -1;
      } else {
         IBetterDragonFight dragonFight = (IBetterDragonFight)serverLevel.getDragonFight();
         dragonFight.reset(forcePortalPosReset);
         commandSource.sendSuccess(() -> Component.literal("Ender Dragon fight has been reset."), false);
         return 1;
      }
   }
}
