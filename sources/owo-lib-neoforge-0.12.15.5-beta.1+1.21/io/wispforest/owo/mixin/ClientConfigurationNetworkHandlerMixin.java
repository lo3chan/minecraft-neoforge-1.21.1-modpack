package io.wispforest.owo.mixin;

import io.wispforest.owo.network.OwoClientConnectionExtension;
import io.wispforest.owo.network.OwoHandshake;
import io.wispforest.owo.network.QueuedChannelSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientConfigurationPacketListenerImpl.class})
public abstract class ClientConfigurationNetworkHandlerMixin extends ClientCommonPacketListenerImpl {
   protected ClientConfigurationNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
      super(client, connection, connectionState);
   }

   @ModifyArg(
      method = {"handleConfigurationFinished(Lnet/minecraft/network/protocol/configuration/ClientboundFinishConfigurationPacket;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/network/Connection;Lnet/minecraft/client/multiplayer/CommonListenerCookie;)V"
      )
   )
   private Connection applyChannelSet(Connection connection) {
      ((OwoClientConnectionExtension)connection).owo$setChannelSet(QueuedChannelSet.channels);
      QueuedChannelSet.channels = null;
      return connection;
   }

   @Inject(
      method = {"handleConfigurationFinished(Lnet/minecraft/network/protocol/configuration/ClientboundFinishConfigurationPacket;)V"},
      at = {@At(
         value = "NEW",
         target = "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/network/Connection;Lnet/minecraft/client/multiplayer/CommonListenerCookie;)Lnet/minecraft/client/multiplayer/ClientPacketListener;"
      )}
   )
   public void owoNeo$handleComplete(ClientboundFinishConfigurationPacket packet, CallbackInfo ci) {
      OwoHandshake.handleReadyClient((ClientConfigurationPacketListenerImpl)this, this.minecraft);
   }
}
