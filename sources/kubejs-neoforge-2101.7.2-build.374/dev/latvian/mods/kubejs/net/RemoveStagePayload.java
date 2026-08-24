package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveStagePayload(UUID player, String stage) implements CustomPacketPayload {
   public static final StreamCodec<ByteBuf, RemoveStagePayload> STREAM_CODEC = StreamCodec.composite(
      UUIDUtil.STREAM_CODEC, RemoveStagePayload::player, ByteBufCodecs.STRING_UTF8, RemoveStagePayload::stage, RemoveStagePayload::new
   );

   public Type<?> type() {
      return KubeJSNet.REMOVE_STAGE;
   }

   public void handle(IPayloadContext ctx) {
      Player p0 = KubeJS.PROXY.getClientPlayer();
      if (p0 != null) {
         ctx.enqueueWork(() -> {
            Player p = this.player.equals(p0.getUUID()) ? p0 : p0.level().getPlayerByUUID(this.player);
            if (p != null) {
               p.kjs$getStages().remove(this.stage);
            }
         });
      }
   }
}
