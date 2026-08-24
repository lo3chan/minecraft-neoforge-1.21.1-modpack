package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.multiplayer.server.ServerPlayerState;
import com.seibel.distanthorizons.core.network.messages.base.CodecCrashMessage;
import net.minecraft.class_2168;
import net.minecraft.class_2170;

public class CrashCommand_fabric extends AbstractCommand_fabric {
   @Override
   public LiteralArgumentBuilder<class_2168> buildCommand() {
      return (LiteralArgumentBuilder<class_2168>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("crash")
               .requires(this::isPlayerSource))
            .then(class_2170.method_9247("encode").executes(c -> {
               assert SharedApi.tryGetDhServerWorld() != null;

               ServerPlayerState serverPlayerState = SharedApi.tryGetDhServerWorld().getServerPlayerStateManager().getConnectedPlayer(this.getSourcePlayer(c));
               if (serverPlayerState != null) {
                  serverPlayerState.networkSession.sendMessage(new CodecCrashMessage(CodecCrashMessage.ECrashPhase.ENCODE));
               }

               return 1;
            })))
         .then(class_2170.method_9247("decode").executes(c -> {
            assert SharedApi.tryGetDhServerWorld() != null;

            ServerPlayerState serverPlayerState = SharedApi.tryGetDhServerWorld().getServerPlayerStateManager().getConnectedPlayer(this.getSourcePlayer(c));
            if (serverPlayerState != null) {
               serverPlayerState.networkSession.sendMessage(new CodecCrashMessage(CodecCrashMessage.ECrashPhase.DECODE));
            }

            return 1;
         }));
   }
}
