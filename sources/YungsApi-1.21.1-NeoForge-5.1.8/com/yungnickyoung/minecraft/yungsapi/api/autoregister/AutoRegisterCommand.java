package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.ApiStatus.Internal;

public class AutoRegisterCommand {
   private final TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, CommandSelection> registerHandler;

   public static AutoRegisterCommand of(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, CommandSelection> handler) {
      return new AutoRegisterCommand(handler);
   }

   private AutoRegisterCommand(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, CommandSelection> handler) {
      this.registerHandler = handler;
   }

   @Internal
   public void invokeHandler(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, CommandSelection selection) {
      this.registerHandler.accept(dispatcher, context, selection);
   }
}
