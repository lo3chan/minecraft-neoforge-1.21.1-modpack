package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.seibel.distanthorizons.common.wrappers.misc.ServerPlayerWrapper_neoforge;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public abstract class AbstractCommand_neoforge {
   public abstract LiteralArgumentBuilder<CommandSourceStack> buildCommand();

   protected int sendSuccessResponse(CommandContext<CommandSourceStack> commandContext, String text, boolean notifyAdmins) {
      ((CommandSourceStack)commandContext.getSource()).sendSuccess(() -> Component.literal(text), notifyAdmins);
      return 1;
   }

   protected int sendFailureResponse(CommandContext<CommandSourceStack> commandContext, String text) {
      ((CommandSourceStack)commandContext.getSource()).sendFailure(Component.literal(text));
      return 1;
   }

   protected IServerPlayerWrapper getSourcePlayer(CommandContext<CommandSourceStack> commandContext) {
      return ServerPlayerWrapper_neoforge.getWrapper(Objects.requireNonNull(((CommandSourceStack)commandContext.getSource()).getPlayer()));
   }

   protected boolean isPlayerSource(CommandSourceStack source) {
      return source.isPlayer();
   }
}
