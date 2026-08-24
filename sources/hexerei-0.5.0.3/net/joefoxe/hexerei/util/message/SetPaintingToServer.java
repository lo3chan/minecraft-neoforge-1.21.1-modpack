package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class SetPaintingToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, SetPaintingToServer> CODEC = StreamCodec.ofMember(
      SetPaintingToServer::encode, SetPaintingToServer::new
   );
   public static final Type<SetPaintingToServer> TYPE = new Type(HexereiUtil.getResource("set_painting_server"));
   String loc;
   float u0;
   float v0;
   float u1;
   float v1;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public SetPaintingToServer(String loc, float u0, float u1, float v0, float v1) {
      this.loc = loc;
      this.u0 = u0;
      this.u1 = u1;
      this.v0 = v0;
      this.v1 = v1;
   }

   public SetPaintingToServer(RegistryFriendlyByteBuf buf) {
      this.loc = buf.readUtf();
      this.u0 = buf.readFloat();
      this.u1 = buf.readFloat();
      this.v0 = buf.readFloat();
      this.v1 = buf.readFloat();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeUtf(this.loc);
      buffer.writeFloat(this.u0);
      buffer.writeFloat(this.u1);
      buffer.writeFloat(this.v0);
      buffer.writeFloat(this.v1);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.BOOK_CANVAS.get()) {
         ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         tag.putString("painting_location", this.loc);
         tag.putFloat("U0", this.u0);
         tag.putFloat("U1", this.u1);
         tag.putFloat("V0", this.v0);
         tag.putFloat("V1", this.v1);
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }
   }
}
