package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class MessageMosquitoMountPlayer {
   public int rider;
   public int mount;

   public MessageMosquitoMountPlayer(int rider, int mount) {
      this.rider = rider;
      this.mount = mount;
   }

   public MessageMosquitoMountPlayer() {
   }

   public static MessageMosquitoMountPlayer read(FriendlyByteBuf buf) {
      return new MessageMosquitoMountPlayer(buf.readInt(), buf.readInt());
   }

   public static void write(MessageMosquitoMountPlayer message, FriendlyByteBuf buf) {
      buf.writeInt(message.rider);
      buf.writeInt(message.mount);
   }

   public static class Handler {
      public static void handle(MessageMosquitoMountPlayer message, AMNetContext context) {
         context.setPacketHandled(true);
         context.enqueueWork(
            () -> {
               Player player = context.getSender();
               if (context.isClientSide()) {
                  player = AlexsMobs.PROXY.getClientSidePlayer();
               }

               if (player != null && player.level() != null) {
                  Entity entity = player.level().getEntity(message.rider);
                  Entity mountEntity = player.level().getEntity(message.mount);
                  if ((entity instanceof EntityCrimsonMosquito || entity instanceof EntityEnderiophage || entity instanceof EntityBaldEagle)
                     && mountEntity instanceof Player
                     && entity.distanceTo(mountEntity) < 16.0) {
                     AMCompat.startRiding(entity, mountEntity, true);
                  }
               }
            }
         );
      }
   }
}
