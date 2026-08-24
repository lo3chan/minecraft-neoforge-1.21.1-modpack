package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NotificationPayload(NotificationToastData data) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, NotificationPayload> STREAM_CODEC = NotificationToastData.STREAM_CODEC
      .map(NotificationPayload::new, NotificationPayload::data);

   public Type<?> type() {
      return KubeJSNet.NOTIFICATION;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player p0 = KubeJS.PROXY.getClientPlayer();
         if (p0 != null) {
            p0.kjs$notify(this.data);
         }
      });
   }
}
