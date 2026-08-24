package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.network.v4.message.Message;
import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.configuration.ServerboundConfigurationMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import fuzs.puzzleslib.impl.core.context.PayloadTypesContextImpl;
import fuzs.puzzleslib.neoforge.impl.network.MessageContextNeoForgeImpl;
import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public abstract class PayloadTypesContextNeoForgeImpl extends PayloadTypesContextImpl {
   net.neoforged.neoforge.network.registration.PayloadRegistrar registrar;

   PayloadTypesContextNeoForgeImpl(String modId, RegisterPayloadHandlersEvent event) {
      super(modId);
      this.registrar = event.registrar(this.channelName.toString());
   }

   @Override
   public <T extends ServerboundPlayMessage> void playToServer(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
      this.playToServer(this.registerPayloadType(clazz), streamCodec, clazz::getSimpleName);
   }

   @Override
   public <T extends ServerboundConfigurationMessage> void configurationToServer(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
      this.configurationToServer(this.registerPayloadType(clazz), streamCodec, clazz::getSimpleName);
   }

   @Override
   public <T extends ServerboundPlayMessage> void playToServer(Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
      this.playToServer(payloadType, streamCodec, payloadType.id()::toString);
   }

   @Override
   public <T extends ServerboundConfigurationMessage> void configurationToServer(Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
      this.configurationToServer(payloadType, streamCodec, payloadType.id()::toString);
   }

   @Override
   public void optional() {
      this.registrar = this.registrar.optional();
   }

   <T extends ServerboundPlayMessage> void playToServer(
      Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<String> payloadContextSupplier
   ) {
      this.register(payloadType, streamCodec, this.registrar::playToServer, MessageContextNeoForgeImpl.ServerboundPlay::new, payloadContextSupplier);
   }

   <T extends ServerboundConfigurationMessage> void configurationToServer(
      Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec, Supplier<String> payloadContextSupplier
   ) {
      this.register(
         payloadType, streamCodec, this.registrar::configurationToServer, MessageContextNeoForgeImpl.ServerboundConfiguration::new, payloadContextSupplier
      );
   }

   <B extends FriendlyByteBuf, T extends Message<C>, C extends Message.Context<?>> void register(
      Type<T> payloadType,
      StreamCodec<? super B, T> streamCodec,
      PayloadTypesContextNeoForgeImpl.PayloadRegistrar<B, T> registrar,
      Function<IPayloadContext, C> contextFactory,
      Supplier<String> payloadContextSupplier
   ) {
      registrar.apply(
         payloadType,
         streamCodec,
         (payload, context) -> context.enqueueWork(() -> payload.getListener().accept((T)contextFactory.apply(context))).exceptionally(throwable -> {
            this.disconnectExceptionally(payloadContextSupplier.get()).accept(throwable, context::disconnect);
            return null;
         })
      );
   }

   public static class ClientImpl extends PayloadTypesContextNeoForgeImpl {
      public ClientImpl(String modId, RegisterPayloadHandlersEvent event) {
         super(modId, event);
      }

      @Override
      public <T extends ClientboundPlayMessage> void playToClient(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
         this.playToClient(this.registerPayloadType(clazz), streamCodec, clazz::getSimpleName);
      }

      @Override
      public <T extends ClientboundConfigurationMessage> void configurationToClient(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
         this.configurationToClient(this.registerPayloadType(clazz), streamCodec, clazz::getSimpleName);
      }

      @Override
      public <T extends ClientboundPlayMessage> void playToClient(Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
         this.playToClient(payloadType, streamCodec, payloadType.id()::toString);
      }

      @Override
      public <T extends ClientboundConfigurationMessage> void configurationToClient(Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
         this.configurationToClient(payloadType, streamCodec, payloadType.id()::toString);
      }

      <T extends ClientboundPlayMessage> void playToClient(
         Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<String> payloadContextSupplier
      ) {
         this.register(payloadType, streamCodec, this.registrar::playToClient, MessageContextNeoForgeImpl.ClientboundPlay::new, payloadContextSupplier);
      }

      <T extends ClientboundConfigurationMessage> void configurationToClient(
         Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec, Supplier<String> payloadContextSupplier
      ) {
         this.register(
            payloadType, streamCodec, this.registrar::configurationToClient, MessageContextNeoForgeImpl.ClientboundConfiguration::new, payloadContextSupplier
         );
      }
   }

   @FunctionalInterface
   interface PayloadRegistrar<B extends ByteBuf, T extends CustomPacketPayload> {
      void apply(Type<T> var1, StreamCodec<? super B, T> var2, IPayloadHandler<T> var3);
   }

   public static class ServerImpl extends PayloadTypesContextNeoForgeImpl {
      public ServerImpl(String modId, RegisterPayloadHandlersEvent event) {
         super(modId, event);
      }

      @Override
      public <T extends ClientboundPlayMessage> void playToClient(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
         this.registerWithoutReceiver(this.registerPayloadType(clazz), streamCodec, this.registrar::playToClient);
      }

      @Override
      public <T extends ClientboundConfigurationMessage> void configurationToClient(Class<T> clazz, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
         this.registerWithoutReceiver(this.registerPayloadType(clazz), streamCodec, this.registrar::configurationToClient);
      }

      @Override
      public <T extends ClientboundPlayMessage> void playToClient(Type<T> payloadType, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
         this.registerWithoutReceiver(payloadType, streamCodec, this.registrar::playToClient);
      }

      @Override
      public <T extends ClientboundConfigurationMessage> void configurationToClient(Type<T> payloadType, StreamCodec<? super FriendlyByteBuf, T> streamCodec) {
         this.registerWithoutReceiver(payloadType, streamCodec, this.registrar::configurationToClient);
      }

      <B extends FriendlyByteBuf, T extends Message<C>, C extends Message.Context<?>> void registerWithoutReceiver(
         Type<T> payloadType, StreamCodec<? super B, T> streamCodec, PayloadTypesContextNeoForgeImpl.PayloadRegistrar<B, T> registrar
      ) {
         this.register(payloadType, streamCodec, registrar, context -> null, () -> "");
      }
   }
}
