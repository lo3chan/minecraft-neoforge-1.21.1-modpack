package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.client.event.hooks.GuiHooks;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public abstract class BossInfoPacket implements CustomPacketPayload {
   protected final UUID bossEvent;
   protected final int entityID;

   public BossInfoPacket(UUID bossEvent, int entityID) {
      this.bossEvent = bossEvent;
      this.entityID = entityID;
   }

   public UUID getBossEvent() {
      return this.bossEvent;
   }

   public int getEntityID() {
      return this.entityID;
   }

   public static class Display extends BossInfoPacket {
      public static final Type<BossInfoPacket.Display> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "add_custom_bossbar"));
      public static final StreamCodec<RegistryFriendlyByteBuf, BossInfoPacket.Display> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC, BossInfoPacket::getBossEvent, ByteBufCodecs.INT, BossInfoPacket::getEntityID, BossInfoPacket.Display::new
      );

      public Display(UUID bossEvent, int entityID) {
         super(bossEvent, entityID);
      }

      public Type<BossInfoPacket.Display> type() {
         return TYPE;
      }

      public static void execute(BossInfoPacket.Display payload, IPayloadContext context) {
         GuiHooks.BOSS_EVENTS.put(payload.bossEvent, payload.entityID);
      }
   }

   public static class Remove extends BossInfoPacket {
      public static final Type<BossInfoPacket.Remove> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "remove_custom_bossbar"));
      public static final StreamCodec<RegistryFriendlyByteBuf, BossInfoPacket.Remove> STREAM_CODEC = StreamCodec.composite(
         UUIDUtil.STREAM_CODEC, BossInfoPacket::getBossEvent, ByteBufCodecs.INT, BossInfoPacket::getEntityID, BossInfoPacket.Remove::new
      );

      public Remove(UUID bossEvent, int entityID) {
         super(bossEvent, entityID);
      }

      public Type<BossInfoPacket.Remove> type() {
         return TYPE;
      }

      public static void execute(BossInfoPacket.Remove payload, IPayloadContext context) {
         GuiHooks.BOSS_EVENTS.remove(payload.bossEvent);
      }
   }
}
