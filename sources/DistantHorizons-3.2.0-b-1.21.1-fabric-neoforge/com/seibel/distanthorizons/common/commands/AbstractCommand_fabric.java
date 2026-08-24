package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.seibel.distanthorizons.common.wrappers.misc.ServerPlayerWrapper_fabric;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import java.util.Objects;
import net.minecraft.class_2168;
import net.minecraft.class_2561;

public abstract class AbstractCommand_fabric {
   public abstract LiteralArgumentBuilder<class_2168> buildCommand();

   protected int sendSuccessResponse(CommandContext<class_2168> commandContext, String text, boolean notifyAdmins) {
      ((class_2168)commandContext.getSource()).method_9226(() -> class_2561.method_43470(text), notifyAdmins);
      return 1;
   }

   protected int sendFailureResponse(CommandContext<class_2168> commandContext, String text) {
      ((class_2168)commandContext.getSource()).method_9213(class_2561.method_43470(text));
      return 1;
   }

   protected IServerPlayerWrapper getSourcePlayer(CommandContext<class_2168> commandContext) {
      return ServerPlayerWrapper_fabric.getWrapper(Objects.requireNonNull(((class_2168)commandContext.getSource()).method_44023()));
   }

   protected boolean isPlayerSource(class_2168 source) {
      return source.method_43737();
   }
}
