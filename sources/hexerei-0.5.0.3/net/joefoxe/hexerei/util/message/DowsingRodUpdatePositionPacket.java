package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.custom.DowsingRodItem;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DowsingRodUpdatePositionPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, DowsingRodUpdatePositionPacket> CODEC = StreamCodec.ofMember(
      DowsingRodUpdatePositionPacket::encode, DowsingRodUpdatePositionPacket::new
   );
   public static final Type<DowsingRodUpdatePositionPacket> TYPE = new Type(HexereiUtil.getResource("dowsing_rod_update"));
   ItemStack itemStack;
   BlockPos blockPos;
   Boolean swampMode;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public DowsingRodUpdatePositionPacket(ItemStack itemStack, BlockPos blockPos, Boolean swampMode) {
      this.itemStack = itemStack;
      this.blockPos = blockPos;
      this.swampMode = swampMode;
   }

   public DowsingRodUpdatePositionPacket(RegistryFriendlyByteBuf buf) {
      this.itemStack = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
      this.blockPos = buf.readBlockPos();
      this.swampMode = buf.readBoolean();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      ItemStack.STREAM_CODEC.encode(buffer, this.itemStack);
      buffer.writeBlockPos(this.blockPos);
      buffer.writeBoolean(this.swampMode);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      ((DowsingRodItem)this.itemStack.getItem()).nearestPos = this.blockPos;
      ((DowsingRodItem)this.itemStack.getItem()).swampMode = this.swampMode;
   }
}
