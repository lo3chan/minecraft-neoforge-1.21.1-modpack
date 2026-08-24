package at.petrak.hexcasting.forge.network;

import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.common.msgs.MsgBeepS2C;
import at.petrak.hexcasting.common.msgs.MsgBlinkS2C;
import at.petrak.hexcasting.common.msgs.MsgCastParticleS2C;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgNewSpellPatternC2S;
import at.petrak.hexcasting.common.msgs.MsgNewSpellPatternS2C;
import at.petrak.hexcasting.common.msgs.MsgNewSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgNewWallScrollS2C;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.common.msgs.MsgRecalcWallScrollDisplayS2C;
import at.petrak.hexcasting.common.msgs.MsgShiftScrollC2S;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.util.TriConsumer;

public class ForgePacketHandler {
   private static final String PROTOCOL_VERSION = "1";

   public static void init(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");
      server(registrar, MsgNewSpellPatternC2S.ID, MsgNewSpellPatternC2S::deserialize, MsgNewSpellPatternC2S::handle);
      server(registrar, MsgShiftScrollC2S.ID, MsgShiftScrollC2S::deserialize, MsgShiftScrollC2S::handle);
      client(registrar, MsgNewSpellPatternS2C.ID, MsgNewSpellPatternS2C::deserialize, MsgNewSpellPatternS2C::handle);
      client(registrar, MsgBlinkS2C.ID, MsgBlinkS2C::deserialize, MsgBlinkS2C::handle);
      client(registrar, MsgSentinelStatusUpdateAck.ID, MsgSentinelStatusUpdateAck::deserialize, MsgSentinelStatusUpdateAck::handle);
      client(registrar, MsgPigmentUpdateAck.ID, MsgPigmentUpdateAck::deserialize, MsgPigmentUpdateAck::handle);
      client(registrar, MsgAltioraUpdateAck.ID, MsgAltioraUpdateAck::deserialize, MsgAltioraUpdateAck::handle);
      client(registrar, MsgCastParticleS2C.ID, MsgCastParticleS2C::deserialize, MsgCastParticleS2C::handle);
      client(registrar, MsgOpenSpellGuiS2C.ID, MsgOpenSpellGuiS2C::deserialize, MsgOpenSpellGuiS2C::handle);
      client(registrar, MsgBeepS2C.ID, MsgBeepS2C::deserialize, MsgBeepS2C::handle);
      client(registrar, MsgBrainsweepAck.ID, MsgBrainsweepAck::deserialize, MsgBrainsweepAck::handle);
      client(registrar, MsgNewWallScrollS2C.ID, MsgNewWallScrollS2C::deserialize, MsgNewWallScrollS2C::handle);
      client(registrar, MsgRecalcWallScrollDisplayS2C.ID, MsgRecalcWallScrollDisplayS2C::deserialize, MsgRecalcWallScrollDisplayS2C::handle);
      client(registrar, MsgNewSpiralPatternsS2C.ID, MsgNewSpiralPatternsS2C::deserialize, MsgNewSpiralPatternsS2C::handle);
      client(registrar, MsgClearSpiralPatternsS2C.ID, MsgClearSpiralPatternsS2C::deserialize, MsgClearSpiralPatternsS2C::handle);
   }

   public static void sendToPlayer(ServerPlayer player, IMessage message) {
      PacketDistributor.sendToPlayer(player, payload(message), new CustomPacketPayload[0]);
   }

   public static void sendToServer(IMessage message) {
      PacketDistributor.sendToServer(payload(message), new CustomPacketPayload[0]);
   }

   public static void sendNear(ServerPlayer excluded, double x, double y, double z, double radius, ServerLevel level, IMessage message) {
      PacketDistributor.sendToPlayersNear(level, excluded, x, y, z, radius, payload(message), new CustomPacketPayload[0]);
   }

   public static void sendTracking(Entity entity, IMessage message) {
      PacketDistributor.sendToPlayersTrackingEntity(entity, payload(message), new CustomPacketPayload[0]);
   }

   public static Packet<ClientGamePacketListener> toVanillaClientboundPacket(IMessage message) {
      return new ClientboundCustomPayloadPacket(payload(message));
   }

   private static <T extends IMessage> void client(PayloadRegistrar registrar, ResourceLocation id, Function<FriendlyByteBuf, T> decoder, Consumer<T> handler) {
      Type<ForgePacketHandler.MessagePayload<T>> type = new Type(id);
      registrar.playToClient(type, codec(type, decoder), (payload, context) -> context.enqueueWork(() -> handler.accept((T)payload.message())));
   }

   private static <T extends IMessage> void server(
      PayloadRegistrar registrar, ResourceLocation id, Function<FriendlyByteBuf, T> decoder, TriConsumer<T, MinecraftServer, ServerPlayer> handler
   ) {
      Type<ForgePacketHandler.MessagePayload<T>> type = new Type(id);
      registrar.playToServer(type, codec(type, decoder), (payload, context) -> context.enqueueWork(() -> {
         ServerPlayer player = (ServerPlayer)context.player();
         handler.accept(payload.message(), player.getServer(), player);
      }));
   }

   private static <T extends IMessage> StreamCodec<RegistryFriendlyByteBuf, ForgePacketHandler.MessagePayload<T>> codec(
      Type<ForgePacketHandler.MessagePayload<T>> type, Function<FriendlyByteBuf, T> decoder
   ) {
      return StreamCodec.of((buf, payload) -> payload.message().serialize(buf), buf -> new ForgePacketHandler.MessagePayload(type, decoder.apply(buf)));
   }

   private static CustomPacketPayload payload(IMessage message) {
      return new ForgePacketHandler.MessagePayload<>(new Type(message.id()), message);
   }

   private record MessagePayload<T extends IMessage>(Type<ForgePacketHandler.MessagePayload<T>> type, T message) implements CustomPacketPayload {
   }
}
