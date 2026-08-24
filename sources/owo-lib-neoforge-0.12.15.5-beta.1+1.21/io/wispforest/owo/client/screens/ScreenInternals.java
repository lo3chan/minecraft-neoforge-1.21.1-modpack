package io.wispforest.owo.client.screens;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import io.wispforest.owo.util.pond.OwoScreenHandlerExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class ScreenInternals {
   public static final ResourceLocation SYNC_PROPERTIES = ResourceLocation.fromNamespaceAndPath("owo", "sync_screen_handler_properties");

   public static void init(PayloadRegistrar registrar) {
      StreamCodec<FriendlyByteBuf, ScreenInternals.LocalPacket> localPacketCodec = CodecUtils.toPacketCodec(ScreenInternals.LocalPacket.ENDEC);
      registrar.playBidirectional(ScreenInternals.LocalPacket.ID, localPacketCodec, (payload, context) -> context.enqueueWork(() -> {
         AbstractContainerMenu screenHandler = context.player().containerMenu;
         if (screenHandler == null) {
            Owo.LOGGER.error("Received local packet for null ScreenHandler");
         } else {
            ((OwoScreenHandlerExtension)screenHandler).owo$handlePacket(payload, context.player().level().isClientSide());
         }
      }));
      registrar.playToClient(
         ScreenInternals.SyncPropertiesPacket.ID,
         CodecUtils.toPacketCodec(ScreenInternals.SyncPropertiesPacket.ENDEC),
         (payload, context) -> context.enqueueWork(() -> {
            AbstractContainerMenu screenHandler = context.player().containerMenu;
            if (screenHandler == null) {
               Owo.LOGGER.error("Received sync properties packet for null ScreenHandler");
            } else {
               ((OwoScreenHandlerExtension)screenHandler).owo$readPropertySync(payload);
            }
         })
      );
   }

   @OnlyIn(Dist.CLIENT)
   public static class Client {
      public static void init() {
         NeoForge.EVENT_BUS.addListener(event -> {
            if (event.getScreen() instanceof MenuAccess<?> handled) {
               ((OwoScreenHandlerExtension)handled.getMenu()).owo$attachToPlayer(Minecraft.getInstance().player);
            }
         });
      }
   }

   public record LocalPacket(int packetId, FriendlyByteBuf payload) implements CustomPacketPayload {
      public static final Type<ScreenInternals.LocalPacket> ID = new Type(ResourceLocation.fromNamespaceAndPath("owo", "local_packet"));
      public static final Endec<ScreenInternals.LocalPacket> ENDEC = StructEndecBuilder.of(
         Endec.VAR_INT.fieldOf("packetId", ScreenInternals.LocalPacket::packetId),
         MinecraftEndecs.PACKET_BYTE_BUF.fieldOf("payload", ScreenInternals.LocalPacket::payload),
         ScreenInternals.LocalPacket::new
      );

      public Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }

   public record SyncPropertiesPacket(FriendlyByteBuf payload) implements CustomPacketPayload {
      public static final Type<ScreenInternals.SyncPropertiesPacket> ID = new Type(ScreenInternals.SYNC_PROPERTIES);
      public static final Endec<ScreenInternals.SyncPropertiesPacket> ENDEC = StructEndecBuilder.of(
         MinecraftEndecs.PACKET_BYTE_BUF.fieldOf("payload", ScreenInternals.SyncPropertiesPacket::payload), ScreenInternals.SyncPropertiesPacket::new
      );

      public Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }
}
