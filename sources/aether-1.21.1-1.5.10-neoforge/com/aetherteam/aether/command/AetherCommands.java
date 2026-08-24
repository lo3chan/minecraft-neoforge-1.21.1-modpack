package com.aetherteam.aether.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class AetherCommands {
   public static void registerCommands(RegisterCommandsEvent event) {
      CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
      AetherTimeCommand.register(dispatcher);
      EternalDayCommand.register(dispatcher);
      PlayerCapabilityCommand.register(dispatcher);
      SunAltarWhitelistCommand.register(dispatcher);
   }
}
