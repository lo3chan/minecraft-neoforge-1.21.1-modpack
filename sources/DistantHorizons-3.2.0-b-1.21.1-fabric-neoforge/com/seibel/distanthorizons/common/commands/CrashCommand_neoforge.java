package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.multiplayer.server.ServerPlayerState;
import com.seibel.distanthorizons.core.network.messages.base.CodecCrashMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CrashCommand_neoforge extends AbstractCommand_neoforge {
   @Override
   public LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("crash")
               .requires(this::isPlayerSource))
            .then(Commands.literal("encode").executes(c -> {
               assert SharedApi.tryGetDhServerWorld() != null;

               ServerPlayerState serverPlayerState = SharedApi.tryGetDhServerWorld().getServerPlayerStateManager().getConnectedPlayer(this.getSourcePlayer(c));
               if (serverPlayerState != null) {
                  serverPlayerState.networkSession.sendMessage(new CodecCrashMessage(CodecCrashMessage.ECrashPhase.ENCODE));
               }

               return 1;
            })))
         .then(Commands.literal("decode").executes(c -> {
            assert SharedApi.tryGetDhServerWorld() != null;

            ServerPlayerState serverPlayerState = SharedApi.tryGetDhServerWorld().getServerPlayerStateManager().getConnectedPlayer(this.getSourcePlayer(c));
            if (serverPlayerState != null) {
               serverPlayerState.networkSession.sendMessage(new CodecCrashMessage(CodecCrashMessage.ECrashPhase.DECODE));
            }

            return 1;
         }));
   }
}
