package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;

public class TESyncPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, TESyncPacket> CODEC = StreamCodec.ofMember(TESyncPacket::encode, TESyncPacket::new);
   public static final Type<TESyncPacket> TYPE = new Type(HexereiUtil.getResource("te_sync"));
   BlockPos pos;
   CompoundTag tag;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public TESyncPacket(BlockPos pos, CompoundTag tag) {
      this.pos = pos;
      this.tag = tag;
   }

   public TESyncPacket(RegistryFriendlyByteBuf buffer) {
      this(buffer.readBlockPos(), buffer.readNbt());
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
      buffer.writeNbt(this.tag);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (minecraft.level.getBlockEntity(this.pos) != null) {
         minecraft.level.getBlockEntity(this.pos).loadWithComponents(this.tag, minecraft.level.registryAccess());
         minecraft.level.getBlockEntity(this.pos).setChanged();
      }
   }
}
