package com.yungnickyoung.minecraft.yungsapi.module;

import com.mojang.brigadier.CommandDispatcher;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterCommand;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandModuleNeoForge {
   public static void processEntries() {
      NeoForge.EVENT_BUS.addListener(CommandModuleNeoForge::registerCommands);
   }

   private static void registerCommands(RegisterCommandsEvent event) {
      AutoRegistrationManager.COMMANDS
         .stream()
         .forEach(data -> registerCommand(data, event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
   }

   private static void registerCommand(
      AutoRegisterField data, CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, CommandSelection selection
   ) {
      AutoRegisterCommand autoRegisterCommand = (AutoRegisterCommand)data.object();
      autoRegisterCommand.invokeHandler(dispatcher, context, selection);
   }
}
