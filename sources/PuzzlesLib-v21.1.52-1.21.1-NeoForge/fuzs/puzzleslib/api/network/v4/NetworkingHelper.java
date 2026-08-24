package fuzs.puzzleslib.api.network.v4;

import fuzs.puzzleslib.api.network.v4.message.Message;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.PayloadTypesContextImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.Objects;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkingHelper {
   private NetworkingHelper() {
   }

   public static <T extends Message<?>> Type<T> getPayloadType(Class<T> payloadClazz) {
      Objects.requireNonNull(payloadClazz, "payload class is null");
      return PayloadTypesContextImpl.getPayloadType(payloadClazz);
   }

   public static Packet<ClientCommonPacketListener> toClientboundPacket(CustomPacketPayload payload) {
      Objects.requireNonNull(payload, "payload is null");
      return ProxyImpl.get().toClientboundPacket(payload);
   }

   public static Packet<ServerCommonPacketListener> toServerboundPacket(CustomPacketPayload payload) {
      Objects.requireNonNull(payload, "payload is null");
      return ProxyImpl.get().toServerboundPacket(payload);
   }

   public static void finishConfigurationTask(
      ServerConfigurationPacketListener packetListener, net.minecraft.server.network.ConfigurationTask.Type configurationTaskType
   ) {
      Objects.requireNonNull(packetListener, "packet listener is null");
      Objects.requireNonNull(configurationTaskType, "configuration task type is null");
      ProxyImpl.get().finishConfigurationTask(packetListener, configurationTaskType);
   }

   public static boolean hasChannel(PacketListener packetListener, Type<?> payloadType) {
      Objects.requireNonNull(packetListener, "packet listener is null");
      Objects.requireNonNull(payloadType, "custom packet payload type is null");
      return ProxyImpl.get().hasChannel(packetListener, payloadType);
   }

   public static ClientGamePacketListener getClientPacketListener() {
      return ProxyImpl.get().getClientPacketListener();
   }

   public static Connection getConnection(PacketListener packetListener) {
      Objects.requireNonNull(packetListener, "packet listener is null");
      Connection connection = ProxyImpl.get().getConnection(packetListener);
      Objects.requireNonNull(connection, "connection is null");
      return connection;
   }

   public static boolean isModPresentServerside(String modId) {
      return ModContext.getModContexts().containsKey(modId) && ModContext.getModContexts().get(modId).isPresentServerside();
   }

   public static boolean isModPresentClientside(ServerPlayer serverPlayer, String modId) {
      return ModContext.getModContexts().containsKey(modId) && ModContext.getModContexts().get(modId).isPresentClientside(serverPlayer);
   }
}
