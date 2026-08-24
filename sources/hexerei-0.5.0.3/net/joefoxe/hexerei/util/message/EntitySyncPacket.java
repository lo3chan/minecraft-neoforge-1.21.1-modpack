package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntitySyncPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, EntitySyncPacket> CODEC = StreamCodec.ofMember(EntitySyncPacket::encode, EntitySyncPacket::new);
   public static final Type<EntitySyncPacket> TYPE = new Type(HexereiUtil.getResource("entity_sync_packet"));
   int sourceId;
   CompoundTag tag;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public EntitySyncPacket(Entity entity, CompoundTag tag) {
      this.sourceId = entity.getId();
      this.tag = tag;
   }

   public EntitySyncPacket(FriendlyByteBuf buf) {
      this.sourceId = buf.readInt();
      this.tag = buf.readNbt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.sourceId);
      buffer.writeNbt(this.tag);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getEntity(this.sourceId) instanceof CrowEntity crow) {
         crow.load(this.tag);
      }

      if (player.level().getEntity(this.sourceId) instanceof OwlEntity owl) {
         owl.load(this.tag);
      }
   }
}
