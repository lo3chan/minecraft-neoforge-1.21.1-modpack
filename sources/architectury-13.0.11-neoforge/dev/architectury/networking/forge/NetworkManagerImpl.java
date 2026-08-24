package dev.architectury.networking.forge;

import com.mojang.logging.LogUtils;
import dev.architectury.impl.NetworkAggregator;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.SpawnEntityPacket;
import dev.architectury.platform.hooks.EventBusesHooks;
import dev.architectury.utils.Env;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public class NetworkManagerImpl {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static NetworkAggregator.Adaptor getAdaptor() {
      return new NetworkAggregator.Adaptor() {
         @Override
         public <T extends CustomPacketPayload> void registerC2S(
            Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> receiver
         ) {
            EventBusesHooks.whenAvailable(
               "architectury",
               bus -> bus.addListener(
                  event -> event.registrar(type.id().getNamespace())
                     .optional()
                     .playToServer(type, codec, (arg, context) -> receiver.receive((T)arg, this.context(context.player(), context, false)))
               )
            );
         }

         @Override
         public <T extends CustomPacketPayload> void registerS2C(
            Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> receiver
         ) {
            EventBusesHooks.whenAvailable(
               "architectury",
               bus -> bus.addListener(
                  event -> event.registrar(type.id().getNamespace())
                     .optional()
                     .playToClient(type, codec, (arg, context) -> receiver.receive((T)arg, this.context(context.player(), context, true)))
               )
            );
         }

         @Override
         public <T extends CustomPacketPayload> Packet<?> toC2SPacket(T payload) {
            return new ServerboundCustomPayloadPacket(payload);
         }

         @Override
         public <T extends CustomPacketPayload> Packet<?> toS2CPacket(T payload) {
            return new ClientboundCustomPayloadPacket(payload);
         }

         @Override
         public <T extends CustomPacketPayload> void registerS2CType(Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
            this.registerS2C(type, codec, (payload, context) -> {});
         }

         public NetworkManager.PacketContext context(Player player, IPayloadContext taskQueue, boolean client) {
            return new NetworkManager.PacketContext() {
               @Override
               public Player getPlayer() {
                  return this.getEnvironment() == Env.CLIENT ? NetworkManagerImpl.getClientPlayer() : player;
               }

               @Override
               public void queue(Runnable runnable) {
                  taskQueue.enqueueWork(runnable);
               }

               @Override
               public Env getEnvironment() {
                  return client ? Env.CLIENT : Env.SERVER;
               }

               @Override
               public RegistryAccess registryAccess() {
                  return this.getEnvironment() == Env.CLIENT ? NetworkManagerImpl.getClientRegistryAccess() : player.registryAccess();
               }
            };
         }
      };
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean canServerReceive(ResourceLocation id) {
      return Minecraft.getInstance().getConnection() != null ? Minecraft.getInstance().getConnection().hasChannel(id) : false;
   }

   public static boolean canPlayerReceive(ServerPlayer player, ResourceLocation id) {
      return player.connection.hasChannel(id);
   }

   public static Packet<ClientGamePacketListener> createAddEntityPacket(Entity entity, ServerEntity serverEntity) {
      return SpawnEntityPacket.create(entity, serverEntity);
   }

   @OnlyIn(Dist.CLIENT)
   public static Player getClientPlayer() {
      return Minecraft.getInstance().player;
   }

   @OnlyIn(Dist.CLIENT)
   public static RegistryAccess getClientRegistryAccess() {
      if (Minecraft.getInstance().level != null) {
         return Minecraft.getInstance().level.registryAccess();
      } else if (Minecraft.getInstance().getConnection() != null) {
         return Minecraft.getInstance().getConnection().registryAccess();
      } else {
         return Minecraft.getInstance().gameMode != null
            ? Minecraft.getInstance().gameMode.connection.registryAccess()
            : RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
      }
   }
}
