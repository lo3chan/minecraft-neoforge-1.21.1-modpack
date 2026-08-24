package com.aetherteam.aether.perk.types;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record Halo(String hexColor) {
   public static final StreamCodec<FriendlyByteBuf, Halo> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, Halo>() {
      public Halo decode(FriendlyByteBuf buffer) {
         String hexColor = buffer.readUtf();
         return new Halo(hexColor);
      }

      public void encode(FriendlyByteBuf buffer, Halo halo) {
         buffer.writeUtf(halo.hexColor());
      }
   };
}
