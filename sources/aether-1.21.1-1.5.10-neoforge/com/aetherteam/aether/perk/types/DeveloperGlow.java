package com.aetherteam.aether.perk.types;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DeveloperGlow(String hexColor) {
   public static final StreamCodec<FriendlyByteBuf, DeveloperGlow> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, DeveloperGlow>() {
      public DeveloperGlow decode(FriendlyByteBuf buffer) {
         String hexColor = buffer.readUtf();
         return new DeveloperGlow(hexColor);
      }

      public void encode(FriendlyByteBuf buffer, DeveloperGlow developerGlow) {
         buffer.writeUtf(developerGlow.hexColor());
      }
   };
}
