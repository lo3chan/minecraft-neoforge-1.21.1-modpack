package com.mrcrayfish.configured.impl.framework.message;

import com.mrcrayfish.configured.impl.framework.handler.FrameworkClientHandler;
import com.mrcrayfish.configured.impl.framework.handler.FrameworkServerHandler;
import com.mrcrayfish.configured.network.ConfiguredCodecs;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class MessageFramework {
   public record Request(ResourceLocation id) implements CustomPacketPayload {
      public static final Type<MessageFramework.Request> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("configured", "request_framework_config"));
      public static final StreamCodec<RegistryFriendlyByteBuf, MessageFramework.Request> STREAM_CODEC = StreamCodec.composite(
         ResourceLocation.STREAM_CODEC, MessageFramework.Request::id, MessageFramework.Request::new
      );

      public static void handle(MessageFramework.Request message, Consumer<Runnable> executor, @Nullable Player player, Consumer<Component> disconnect) {
         if (player instanceof ServerPlayer serverPlayer) {
            executor.accept(() -> FrameworkServerHandler.handleRequestConfig(serverPlayer, message, disconnect));
         }
      }

      public Type<MessageFramework.Request> type() {
         return TYPE;
      }
   }

   public record Response(byte[] data) implements CustomPacketPayload {
      public static final Type<MessageFramework.Response> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("configured", "response_framework_config"));
      public static final StreamCodec<RegistryFriendlyByteBuf, MessageFramework.Response> STREAM_CODEC = StreamCodec.composite(
         ConfiguredCodecs.BYTE_ARRAY, MessageFramework.Response::data, MessageFramework.Response::new
      );

      public static void handle(MessageFramework.Response message, Consumer<Runnable> executor, @Nullable Player player, Consumer<Component> disconnect) {
         executor.accept(() -> FrameworkClientHandler.handleResponse(message, disconnect));
      }

      public Type<MessageFramework.Response> type() {
         return TYPE;
      }
   }

   public record Sync(ResourceLocation id, byte[] data) implements CustomPacketPayload {
      public static final Type<MessageFramework.Sync> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("configured", "sync_framework_config"));
      public static final StreamCodec<RegistryFriendlyByteBuf, MessageFramework.Sync> STREAM_CODEC = StreamCodec.composite(
         ResourceLocation.STREAM_CODEC, MessageFramework.Sync::id, ConfiguredCodecs.BYTE_ARRAY, MessageFramework.Sync::data, MessageFramework.Sync::new
      );

      public static void handle(MessageFramework.Sync message, Consumer<Runnable> executor, @Nullable Player player, Consumer<Component> disconnect) {
         if (player instanceof ServerPlayer serverPlayer) {
            executor.accept(() -> FrameworkServerHandler.handleServerSync(serverPlayer, message, disconnect));
         }
      }

      public Type<MessageFramework.Sync> type() {
         return TYPE;
      }
   }
}
