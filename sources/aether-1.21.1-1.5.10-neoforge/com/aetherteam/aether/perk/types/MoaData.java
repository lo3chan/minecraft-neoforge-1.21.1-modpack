package com.aetherteam.aether.perk.types;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MoaData(@Nullable UUID moaUUID, @Nullable MoaSkins.MoaSkin moaSkin) {
   public static final StreamCodec<FriendlyByteBuf, MoaData> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, MoaData>() {
      public MoaData decode(FriendlyByteBuf buffer) {
         UUID uuid = null;
         if (buffer.readBoolean()) {
            uuid = buffer.readUUID();
         }

         MoaSkins.MoaSkin moaSkin = (MoaSkins.MoaSkin)MoaSkins.MoaSkin.STREAM_CODEC.decode(buffer);
         return new MoaData(uuid, moaSkin);
      }

      public void encode(FriendlyByteBuf buffer, MoaData moaData) {
         if (moaData.moaUUID() == null) {
            buffer.writeBoolean(false);
         } else {
            buffer.writeBoolean(true);
            buffer.writeUUID(moaData.moaUUID());
         }

         MoaSkins.MoaSkin.STREAM_CODEC.encode(buffer, moaData.moaSkin());
      }
   };
}
