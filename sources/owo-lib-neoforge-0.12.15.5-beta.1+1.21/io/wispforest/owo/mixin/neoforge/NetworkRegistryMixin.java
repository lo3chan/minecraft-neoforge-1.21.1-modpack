package io.wispforest.owo.mixin.neoforge;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.owo.network.neoforge.SidedPacketCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({NetworkRegistry.class})
public class NetworkRegistryMixin {
   @ModifyReturnValue(
      method = {"getCodec(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/ConnectionProtocol;Lnet/minecraft/network/protocol/PacketFlow;)Lnet/minecraft/network/codec/StreamCodec;"},
      at = {@At(
         value = "RETURN",
         ordinal = 3
      )}
   )
   private static StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload> owo$unpackSidedCodec(
      StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload> original, @Local(argsOnly = true) PacketFlow flow
   ) {
      if (original instanceof SidedPacketCodec<?> sidedPacketCodec) {
         original = (StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload>)sidedPacketCodec.getCodec(flow);
      }

      return original;
   }
}
