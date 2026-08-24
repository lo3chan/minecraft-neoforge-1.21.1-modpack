package net.cibernet.alchemancy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CRotatePlayerPayload(float amount) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, S2CRotatePlayerPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.FLOAT, S2CRotatePlayerPayload::amount, S2CRotatePlayerPayload::new
   );
   public static final Type<S2CRotatePlayerPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("alchemancy", "s2c/rotate_player"));

   public void handleDataOnMain(IPayloadContext context) {
      Player target = context.player();
      float prevRot = target.getYRot();
      target.setYRot(prevRot - this.amount());
      target.setYHeadRot(prevRot);
      target.setYBodyRot(target.yBodyRot - this.amount());
   }

   public Type<S2CRotatePlayerPayload> type() {
      return TYPE;
   }
}
