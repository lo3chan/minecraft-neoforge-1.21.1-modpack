package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class MessageCrowMountPlayer {
   public int rider;
   public int mount;

   public MessageCrowMountPlayer(int rider, int mount) {
      this.rider = rider;
      this.mount = mount;
   }

   public MessageCrowMountPlayer() {
   }

   public static MessageCrowMountPlayer read(FriendlyByteBuf buf) {
      return new MessageCrowMountPlayer(buf.readInt(), buf.readInt());
   }

   public static void write(MessageCrowMountPlayer message, FriendlyByteBuf buf) {
      buf.writeInt(message.rider);
      buf.writeInt(message.mount);
   }

   public static class Handler {
      public static void handle(MessageCrowMountPlayer message, AMNetContext context) {
         context.setPacketHandled(true);
         context.enqueueWork(() -> {
            Player player = context.getSender();
            if (context.isClientSide()) {
               player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null && player.level() != null) {
               Entity entity = player.level().getEntity(message.rider);
               Entity mountEntity = player.level().getEntity(message.mount);
               if (entity instanceof EntityCrow && mountEntity instanceof Player && entity.distanceTo(mountEntity) < 16.0) {
                  AMCompat.startRiding(entity, mountEntity, true);
               }
            }
         });
      }
   }
}
