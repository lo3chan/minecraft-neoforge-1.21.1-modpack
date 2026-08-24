package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MessageKangarooEat {
   public int kangaroo;
   public ItemStack stack;

   public MessageKangarooEat(int kangaroo, ItemStack stack) {
      this.kangaroo = kangaroo;
      this.stack = stack;
   }

   public MessageKangarooEat() {
   }

   public static MessageKangarooEat read(FriendlyByteBuf buf) {
      return new MessageKangarooEat(buf.readInt(), AMCompat.readItem(buf));
   }

   public static void write(MessageKangarooEat message, FriendlyByteBuf buf) {
      buf.writeInt(message.kangaroo);
      AMCompat.writeItem(buf, message.stack);
   }

   public static class Handler {
      public static void handle(MessageKangarooEat message, AMNetContext context) {
         context.setPacketHandled(true);
         context.enqueueWork(
            () -> {
               Player player = context.getSender();
               if (context.isClientSide()) {
                  player = AlexsMobs.PROXY.getClientSidePlayer();
               }

               if (player != null && player.level() != null) {
                  Entity entity = player.level().getEntity(message.kangaroo);
                  if (entity instanceof EntityKangaroo kangaroo && ((EntityKangaroo)entity).kangarooInventory != null) {
                     for (int i = 0; i < 7; i++) {
                        double d2 = kangaroo.getRandom().nextGaussian() * 0.02;
                        double d0 = kangaroo.getRandom().nextGaussian() * 0.02;
                        double d1 = kangaroo.getRandom().nextGaussian() * 0.02;
                        entity.level()
                           .addParticle(
                              new ItemParticleOption(ParticleTypes.ITEM, message.stack),
                              entity.getX() + kangaroo.getRandom().nextFloat() * entity.getBbWidth() - entity.getBbWidth() * 0.5,
                              entity.getY() + entity.getBbHeight() * 0.5F + kangaroo.getRandom().nextFloat() * entity.getBbHeight() * 0.5F,
                              entity.getZ() + kangaroo.getRandom().nextFloat() * entity.getBbWidth() - entity.getBbWidth() * 0.5,
                              d0,
                              d1,
                              d2
                           );
                     }
                  }
               }
            }
         );
      }
   }
}
