package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.seibel.distanthorizons.core.network.messages.MessageRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.Nullable;

public class CommandInitializer_neoforge {
   private boolean serverReady = false;
   private static final int REQUIRED_PERMISSION_LEVEL = 4;
   @Nullable
   private CommandDispatcher<CommandSourceStack> commandDispatcher;

   public void onServerReady() {
      this.serverReady = true;
      if (this.commandDispatcher != null) {
         this.initCommands(this.commandDispatcher);
         this.commandDispatcher = null;
      }
   }

   public void initCommands(CommandDispatcher<CommandSourceStack> commandDispatcher) {
      if (!this.serverReady) {
         this.commandDispatcher = commandDispatcher;
      } else {
         LiteralArgumentBuilder<CommandSourceStack> builder = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("dh")
            .requires(source -> source.hasPermission(4));
         builder.then(new ConfigCommand_neoforge().buildCommand());
         builder.then(new DebugCommand_neoforge().buildCommand());
         builder.then(new PregenCommand_neoforge().buildCommand());
         if (MessageRegistry.DEBUG_CODEC_CRASH_MESSAGE) {
            builder.then(new CrashCommand_neoforge().buildCommand());
         }

         commandDispatcher.register(builder);
      }
   }
}
