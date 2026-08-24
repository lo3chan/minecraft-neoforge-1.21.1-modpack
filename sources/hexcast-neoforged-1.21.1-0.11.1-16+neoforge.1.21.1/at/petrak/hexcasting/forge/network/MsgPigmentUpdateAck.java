package at.petrak.hexcasting.forge.network;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record MsgPigmentUpdateAck(FrozenPigment update) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("color");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgPigmentUpdateAck deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      CompoundTag tag = buf.readNbt();
      FrozenPigment colorizer = FrozenPigment.fromNBT(tag);
      return new MsgPigmentUpdateAck(colorizer);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeNbt(this.update.serializeToNBT());
   }

   public static void handle(final MsgPigmentUpdateAck self) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
               IXplatAbstractions.INSTANCE.setPigment(player, self.update());
            }
         }
      });
   }
}
