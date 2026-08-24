package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record MsgBlinkS2C(Vec3 addedPosition) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("blink");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgBlinkS2C deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      double x = buf.readDouble();
      double y = buf.readDouble();
      double z = buf.readDouble();
      return new MsgBlinkS2C(new Vec3(x, y, z));
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeDouble(this.addedPosition.x);
      buf.writeDouble(this.addedPosition.y);
      buf.writeDouble(this.addedPosition.z);
   }

   public static void handle(final MsgBlinkS2C self) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            LocalPlayer player = Minecraft.getInstance().player;
            player.setPos(player.position().add(self.addedPosition()));
         }
      });
   }
}
