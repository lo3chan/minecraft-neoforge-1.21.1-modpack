package net.nycto_team.overpacked.core.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nycto_team.overpacked.util.ModLoc;
import net.nycto_team.overpacked.util.Utils;

public record PlaceBackpackPacket() implements CustomPacketPayload {
   public static final Type<PlaceBackpackPacket> type = new Type(ModLoc.get("place_backpack"));
   public static final StreamCodec<FriendlyByteBuf, PlaceBackpackPacket> codec = StreamCodec.unit(new PlaceBackpackPacket());

   public Type<? extends CustomPacketPayload> type() {
      return type;
   }

   public static void Handle(PlaceBackpackPacket packet, IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = ctx.player();
         ItemStack stack = Utils.get_curio_backpack(player);
         Utils.PlaceBackpack(player.level(), stack, player, true);
      });
   }
}
