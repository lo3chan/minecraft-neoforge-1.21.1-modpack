package dev.corgitaco.enhancedcelestials2core.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.corgitaco.enhancedcelestials2core.core.EC2Constants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ECCommands {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> requires = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("enhancedcelestials2core")
         .requires(commandSource -> commandSource.hasPermission(2));
      requires.then(SetLunarEventCommand.register(dispatcher));
      requires.then(LunarForecastCommand.register(dispatcher));
      requires.then(ScheduleLunarEventCommand.register(dispatcher));
      requires.then(DumpModifiersCommand.register(dispatcher));
      dispatcher.register(requires);
      LiteralArgumentBuilder<CommandSourceStack> ecAlias = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("ec")
         .requires(commandSource -> commandSource.hasPermission(2));
      ecAlias.then(SetLunarEventCommand.register(dispatcher));
      ecAlias.then(LunarForecastCommand.register(dispatcher));
      ecAlias.then(ScheduleLunarEventCommand.register(dispatcher));
      ecAlias.then(DumpModifiersCommand.register(dispatcher));
      dispatcher.register(ecAlias);
      EC2Constants.LOGGER.debug("Registered Enhanced Celestial Commands!");
   }
}
