package dev.latvian.mods.kubejs.net;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WebServerUpdateNBTPayload(String event, String requiredTag, Optional<Tag> payload) implements CustomPacketPayload {
   public static final StreamCodec<ByteBuf, WebServerUpdateNBTPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      WebServerUpdateNBTPayload::event,
      ByteBufCodecs.STRING_UTF8,
      WebServerUpdateNBTPayload::requiredTag,
      ByteBufCodecs.optional(ByteBufCodecs.TAG),
      WebServerUpdateNBTPayload::payload,
      WebServerUpdateNBTPayload::new
   );

   public Type<?> type() {
      return KubeJSNet.WEB_SERVER_NBT_UPDATE;
   }

   public void handle(IPayloadContext ctx) {
      int count = KubeJSWeb.broadcastUpdate(
         "server/" + this.event,
         this.requiredTag,
         () -> this.payload.<JsonElement>map(tagx -> (JsonElement)NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, tagx)).orElse(null)
      );
      if (count == 0 && this.event.equals("highlight/items")) {
         for (Tag e : ((CompoundTag)this.payload.get()).getList("items", 10)) {
            CompoundTag t = (CompoundTag)e;
            KubeJS.LOGGER.info("[Highlighted Item] {}", t.getString("string"));
            Tag var7 = t.get("tags");
            if (var7 instanceof ListTag) {
               for (Tag tag : (ListTag)var7) {
                  KubeJS.LOGGER.info("[Highlighted Item] - #{}", tag.getAsString());
               }
            }
         }
      }
   }
}
