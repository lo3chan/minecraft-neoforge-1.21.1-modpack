package com.iafenvoy.jupiter.network.payload;

import com.iafenvoy.jupiter.util.RLUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ConfigSyncPayload(ResourceLocation id, boolean allow, CompoundTag compound) implements CustomPacketPayload {
   public static final Type<ConfigSyncPayload> TYPE = new Type(RLUtil.id("config_sync"));
   public static final StreamCodec<FriendlyByteBuf, ConfigSyncPayload> CODEC = StreamCodec.of((buf, value) -> {
      buf.writeResourceLocation(value.id);
      buf.writeBoolean(value.allow);
      buf.writeNbt(value.compound);
   }, buf -> new ConfigSyncPayload(buf.readResourceLocation(), buf.readBoolean(), buf.readNbt()));

   public ConfigSyncPayload(ResourceLocation id, CompoundTag compound) {
      this(id, true, compound);
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
