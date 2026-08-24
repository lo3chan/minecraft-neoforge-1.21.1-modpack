package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.command.SunAltarWhitelist;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SunAltarUpdatePacket(long dayTime, int timeScale) implements CustomPacketPayload {
   public static final Type<SunAltarUpdatePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aether", "update_sun_altar"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SunAltarUpdatePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_LONG, SunAltarUpdatePacket::dayTime, ByteBufCodecs.INT, SunAltarUpdatePacket::timeScale, SunAltarUpdatePacket::new
   );

   public Type<SunAltarUpdatePacket> type() {
      return TYPE;
   }

   public static void execute(SunAltarUpdatePacket payload, IPayloadContext context) {
      Player playerEntity = context.player();
      if (playerEntity.level() instanceof ServerLevel level
         && (
            !(Boolean)AetherConfig.SERVER.sun_altar_whitelist.get()
               || playerEntity.hasPermissions(4)
               || SunAltarWhitelist.INSTANCE.isWhiteListed(playerEntity.getGameProfile())
         )
         && ((List)AetherConfig.SERVER.sun_altar_dimensions.get()).contains(level.dimension().location().toString())) {
         long dayBase = level.getDayTime() / payload.timeScale();
         long dayTime = dayBase * payload.timeScale() + payload.dayTime();
         level.setDayTime(dayTime);
         level.getServer()
            .getPlayerList()
            .broadcastAll(
               new ClientboundSetTimePacket(level.getGameTime(), level.getDayTime(), level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)),
               level.dimension()
            );
      }
   }
}
