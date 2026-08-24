package io.wispforest.owo.mixin.neoforge;

import io.netty.channel.ChannelHandlerContext;
import io.wispforest.owo.config.ConfigSynchronizer;
import io.wispforest.owo.network.OwoHandshake;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Connection.class})
public class ClientConnectionDisconnectHookMixin {
   @Shadow
   private PacketListener packetListener;

   @Inject(
      method = {"channelInactive(Lio/netty/channel/ChannelHandlerContext;)V"},
      at = {@At("HEAD")}
   )
   private void owoNeo$disconnectAddon(ChannelHandlerContext channelHandlerContext, CallbackInfo ci) {
      if (this.packetListener instanceof ClientConfigurationPacketListenerImpl) {
         OwoHandshake.onDisconnect();
      } else if (this.packetListener instanceof ClientboundPacketListener) {
         OwoHandshake.onDisconnect();
         ConfigSynchronizer.onDisconnect();
      }
   }

   @Inject(
      method = {"handleDisconnection()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/PacketListener;onDisconnect(Lnet/minecraft/network/DisconnectionDetails;)V"
      )}
   )
   private void owoNeo$disconnectAddon(CallbackInfo ci) {
      if (this.packetListener instanceof ClientConfigurationPacketListenerImpl) {
         OwoHandshake.onDisconnect();
      } else if (this.packetListener instanceof ClientboundPacketListener) {
         OwoHandshake.onDisconnect();
         ConfigSynchronizer.onDisconnect();
      }
   }
}
