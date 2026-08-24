package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.seibel.distanthorizons.core.network.messages.MessageRegistry;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import org.jetbrains.annotations.Nullable;

public class CommandInitializer_fabric {
   private boolean serverReady = false;
   private static final int REQUIRED_PERMISSION_LEVEL = 4;
   @Nullable
   private CommandDispatcher<class_2168> commandDispatcher;

   public void onServerReady() {
      this.serverReady = true;
      if (this.commandDispatcher != null) {
         this.initCommands(this.commandDispatcher);
         this.commandDispatcher = null;
      }
   }

   public void initCommands(CommandDispatcher<class_2168> commandDispatcher) {
      if (!this.serverReady) {
         this.commandDispatcher = commandDispatcher;
      } else {
         LiteralArgumentBuilder<class_2168> builder = (LiteralArgumentBuilder<class_2168>)class_2170.method_9247("dh")
            .requires(source -> source.method_9259(4));
         builder.then(new ConfigCommand_fabric().buildCommand());
         builder.then(new DebugCommand_fabric().buildCommand());
         builder.then(new PregenCommand_fabric().buildCommand());
         if (MessageRegistry.DEBUG_CODEC_CRASH_MESSAGE) {
            builder.then(new CrashCommand_fabric().buildCommand());
         }

         commandDispatcher.register(builder);
      }
   }
}
