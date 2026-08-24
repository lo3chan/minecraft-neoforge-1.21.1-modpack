package at.petrak.hexcasting.forge.network;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public record MsgBrainsweepAck(int target) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("sweep");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgBrainsweepAck deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      int target = buf.readInt();
      return new MsgBrainsweepAck(target);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeInt(this.target);
   }

   public static MsgBrainsweepAck of(Entity target) {
      return new MsgBrainsweepAck(target.getId());
   }

   public static void handle(final MsgBrainsweepAck msg) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.getEntity(msg.target()) instanceof Mob living) {
               IXplatAbstractions.INSTANCE.setBrainsweepAddlData(living);
            }
         }
      });
   }
}
