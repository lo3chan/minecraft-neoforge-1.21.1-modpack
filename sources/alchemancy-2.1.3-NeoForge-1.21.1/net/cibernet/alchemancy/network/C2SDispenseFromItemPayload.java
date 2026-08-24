package net.cibernet.alchemancy.network;

import net.cibernet.alchemancy.properties.DispensingProperty;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SDispenseFromItemPayload(ItemStack stack, Vec3 pos, Pose pose, float xRot, float yRot) implements CustomPacketPayload {
   public static final Type<C2SDispenseFromItemPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("alchemancy", "c2s/dispense_from_item"));
   public static final StreamCodec<RegistryFriendlyByteBuf, C2SDispenseFromItemPayload> STREAM_CODEC = StreamCodec.composite(
      ItemStack.STREAM_CODEC,
      C2SDispenseFromItemPayload::stack,
      CommonUtils.VEC3_CODEC,
      C2SDispenseFromItemPayload::pos,
      Pose.STREAM_CODEC,
      C2SDispenseFromItemPayload::pose,
      ByteBufCodecs.FLOAT,
      C2SDispenseFromItemPayload::xRot,
      ByteBufCodecs.FLOAT,
      C2SDispenseFromItemPayload::yRot,
      C2SDispenseFromItemPayload::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handleDataOnMain(IPayloadContext context) {
      DispensingProperty.placeByActivation(context.player(), context.player().level(), this.pos(), this.pose(), this.stack(), this.xRot(), this.yRot());
   }
}
