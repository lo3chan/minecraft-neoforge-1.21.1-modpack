package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.client.highlight.KubedexPayloadHandler;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestInventoryKubedexPayload(List<Integer> slots, List<ItemStack> stacks, int flags) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, RequestInventoryKubedexPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()),
      RequestInventoryKubedexPayload::slots,
      ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
      RequestInventoryKubedexPayload::stacks,
      ByteBufCodecs.VAR_INT,
      RequestInventoryKubedexPayload::flags,
      RequestInventoryKubedexPayload::new
   );

   public Type<?> type() {
      return KubeJSNet.Kubedex.REQUEST_INVENTORY;
   }

   public void handle(IPayloadContext ctx) {
      if (ctx.player() instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(2)) {
         ctx.enqueueWork(() -> KubedexPayloadHandler.inventory(serverPlayer, this.slots, this.stacks, this.flags));
      }
   }
}
