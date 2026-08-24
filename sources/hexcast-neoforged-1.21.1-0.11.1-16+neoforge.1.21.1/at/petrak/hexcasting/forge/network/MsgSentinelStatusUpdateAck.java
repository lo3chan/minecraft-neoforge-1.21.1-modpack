package at.petrak.hexcasting.forge.network;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record MsgSentinelStatusUpdateAck(@Nullable Sentinel update) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("sntnl");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgSentinelStatusUpdateAck deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      boolean exists = buf.readBoolean();
      if (!exists) {
         return new MsgSentinelStatusUpdateAck(null);
      } else {
         boolean greater = buf.readBoolean();
         Vec3 origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
         ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
         Sentinel sentinel = new Sentinel(greater, origin, dimension);
         return new MsgSentinelStatusUpdateAck(sentinel);
      }
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      if (this.update == null) {
         buf.writeBoolean(false);
      } else {
         buf.writeBoolean(true);
         buf.writeBoolean(this.update.extendsRange());
         buf.writeDouble(this.update.position().x);
         buf.writeDouble(this.update.position().y);
         buf.writeDouble(this.update.position().z);
         buf.writeResourceLocation(this.update.dimension().location());
      }
   }

   public static void handle(final MsgSentinelStatusUpdateAck self) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
               IXplatAbstractions.INSTANCE.setSentinel(player, self.update());
            }
         }
      });
   }
}
