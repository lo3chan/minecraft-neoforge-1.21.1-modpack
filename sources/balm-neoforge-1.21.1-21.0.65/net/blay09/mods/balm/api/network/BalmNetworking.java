package net.blay09.mods.balm.api.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface BalmNetworking {
   void openMenu(Player var1, MenuProvider var2);

   void defineNetworkVersion(String var1, String var2);

   default void allowClientAndServerOnly(String modId) {
      this.allowClientOnly(modId);
      this.allowServerOnly(modId);
   }

   void allowClientOnly(String var1);

   void allowServerOnly(String var1);

   <T extends CustomPacketPayload> void reply(T var1);

   <T extends CustomPacketPayload> void sendTo(Player var1, T var2);

   <T extends CustomPacketPayload> void sendToTracking(ServerLevel var1, BlockPos var2, T var3);

   <T extends CustomPacketPayload> void sendToTracking(Entity var1, T var2);

   <T extends CustomPacketPayload> void sendToAll(MinecraftServer var1, T var2);

   <T extends CustomPacketPayload> void sendToServer(T var1);

   <T extends CustomPacketPayload> void registerClientboundPacket(
      Type<T> var1, Class<T> var2, StreamCodec<RegistryFriendlyByteBuf, T> var3, BiConsumer<Player, T> var4
   );

   <T extends CustomPacketPayload> void registerServerboundPacket(
      Type<T> var1, Class<T> var2, StreamCodec<RegistryFriendlyByteBuf, T> var3, BiConsumer<ServerPlayer, T> var4
   );

   @Deprecated
   default <T extends CustomPacketPayload> void registerClientboundPacket(
      Type<T> type,
      Class<T> clazz,
      BiConsumer<RegistryFriendlyByteBuf, T> encodeFunc,
      Function<RegistryFriendlyByteBuf, T> decodeFunc,
      BiConsumer<Player, T> handler
   ) {
      this.registerClientboundPacket(type, clazz, StreamCodec.of(encodeFunc::accept, decodeFunc::apply), handler);
   }

   @Deprecated
   default <T extends CustomPacketPayload> void registerServerboundPacket(
      Type<T> type,
      Class<T> clazz,
      BiConsumer<RegistryFriendlyByteBuf, T> encodeFunc,
      Function<RegistryFriendlyByteBuf, T> decodeFunc,
      BiConsumer<ServerPlayer, T> handler
   ) {
      this.registerServerboundPacket(type, clazz, StreamCodec.of(encodeFunc::accept, decodeFunc::apply), handler);
   }

   @Deprecated
   default void openGui(Player player, MenuProvider menuProvider) {
      this.openMenu(player, menuProvider);
   }

   BalmNetworking scoped(String var1);

   boolean isMessageSupported(ServerPlayer var1, CustomPacketPayload var2);

   boolean isMessageSupportedByServer(CustomPacketPayload var1);
}
