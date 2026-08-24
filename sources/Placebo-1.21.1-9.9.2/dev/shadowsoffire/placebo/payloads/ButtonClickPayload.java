package dev.shadowsoffire.placebo.payloads;

import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.network.PayloadProvider;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ButtonClickPayload(int button) implements CustomPacketPayload {
   public static final Type<ButtonClickPayload> TYPE = new Type(Placebo.loc("button_click"));
   public static final StreamCodec<FriendlyByteBuf, ButtonClickPayload> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, ButtonClickPayload::button, ButtonClickPayload::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public interface IButtonContainer {
      void onButtonClick(int var1);
   }

   public static class Provider implements PayloadProvider<ButtonClickPayload> {
      @Override
      public Type<ButtonClickPayload> getType() {
         return ButtonClickPayload.TYPE;
      }

      @Override
      public StreamCodec<? super RegistryFriendlyByteBuf, ButtonClickPayload> getCodec() {
         return ButtonClickPayload.CODEC;
      }

      public void handle(ButtonClickPayload msg, IPayloadContext ctx) {
         if (ctx.player().containerMenu instanceof ButtonClickPayload.IButtonContainer) {
            ((ButtonClickPayload.IButtonContainer)ctx.player().containerMenu).onButtonClick(msg.button);
         }
      }

      @Override
      public List<ConnectionProtocol> getSupportedProtocols() {
         return List.of(ConnectionProtocol.PLAY);
      }

      @Override
      public Optional<PacketFlow> getFlow() {
         return Optional.of(PacketFlow.SERVERBOUND);
      }

      @Override
      public String getVersion() {
         return "1";
      }
   }
}
