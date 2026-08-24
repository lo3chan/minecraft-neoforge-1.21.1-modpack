package net.joefoxe.hexerei.util.message;

import java.util.UUID;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class SendOwlCourierPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, SendOwlCourierPacket> CODEC = StreamCodec.ofMember(
      SendOwlCourierPacket::encode, SendOwlCourierPacket::new
   );
   public static final Type<SendOwlCourierPacket> TYPE = new Type(HexereiUtil.getResource("owl_courier"));
   int sourceId;
   boolean deliverToEntity;
   GlobalPos pos;
   InteractionHand hand;
   UUID entityId;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public SendOwlCourierPacket(Entity owl, GlobalPos pos, InteractionHand hand) {
      this.sourceId = owl.getId();
      this.pos = pos;
      this.hand = hand;
      this.deliverToEntity = false;
   }

   public SendOwlCourierPacket(Entity owl, UUID playerUUID, InteractionHand hand) {
      this.sourceId = owl.getId();
      this.entityId = playerUUID;
      this.hand = hand;
      this.deliverToEntity = true;
   }

   public SendOwlCourierPacket(RegistryFriendlyByteBuf buf) {
      this.sourceId = buf.readInt();
      this.deliverToEntity = buf.readBoolean();
      this.hand = (InteractionHand)buf.readEnum(InteractionHand.class);
      if (this.deliverToEntity) {
         this.entityId = buf.readUUID();
      } else {
         this.pos = buf.readGlobalPos();
      }
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
      buffer.writeBoolean(this.deliverToEntity);
      buffer.writeEnum(this.hand);
      if (this.deliverToEntity) {
         buffer.writeUUID(this.entityId);
      } else {
         buffer.writeGlobalPos(this.pos);
      }
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getEntity(this.sourceId) instanceof OwlEntity owl) {
         if (!owl.currentTask.isNone()) {
            player.sendSystemMessage(Component.translatable("message.hexerei.owl_already_doing_task", new Object[]{owl.getName()}));
         } else {
            if (this.deliverToEntity) {
               if (server.getPlayerList().getPlayer(this.entityId) != null) {
                  ItemStack copyStack = player.getItemInHand(this.hand).copy();
                  if (copyStack.getItem() == ModItems.COURIER_PACKAGE.get() || copyStack.getItem() == ModItems.COURIER_LETTER.get()) {
                     ServerPlayer player1 = server.getPlayerList().getPlayer(this.entityId);
                     owl.messagingController.setDestination(player1);
                     copyStack.setCount(1);
                     owl.messagingController.setMessageStack(copyStack);
                     player.getItemInHand(this.hand).shrink(1);
                     owl.sync();
                  }
               }
            } else {
               ServerLevel level = player.level().getServer().getLevel(this.pos.dimension());
               if (level == null || !(level.getBlockState(this.pos.pos()).getBlock() instanceof OwlCourierDepot)) {
                  OwlCourierDepotSavedData.get().clearOwlCourierDepot(this.pos);
                  return;
               }

               ItemStack copyStack = player.getItemInHand(this.hand).copy();
               if (copyStack.getItem() == ModItems.COURIER_PACKAGE.get() || copyStack.getItem() == ModItems.COURIER_LETTER.get()) {
                  owl.messagingController.setDestination(this.pos);
                  copyStack.setCount(1);
                  owl.messagingController.setMessageStack(copyStack);
                  player.getItemInHand(this.hand).shrink(1);
                  owl.sync();
               }
            }

            owl.messagingController.start(GlobalPos.of(owl.level().dimension(), owl.blockPosition()));
            owl.currentTask = OwlEntity.OwlTask.DELIVER_MESSAGE;
         }
      }
   }
}
