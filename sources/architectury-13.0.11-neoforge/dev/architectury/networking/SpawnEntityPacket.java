package dev.architectury.networking;

import dev.architectury.extensions.network.EntitySpawnExtension;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SpawnEntityPacket {
   private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("architectury", "spawn_entity_packet");
   private static final Type<SpawnEntityPacket.PacketPayload> PACKET_TYPE = new Type(PACKET_ID);
   private static final StreamCodec<RegistryFriendlyByteBuf, SpawnEntityPacket.PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(
      SpawnEntityPacket.PacketPayload::write, SpawnEntityPacket.PacketPayload::new
   );

   public static Packet<ClientGamePacketListener> create(Entity entity, ServerEntity serverEntity) {
      if (entity.level().isClientSide()) {
         throw new IllegalStateException("SpawnPacketUtil.create called on the logical client!");
      } else {
         return (Packet<ClientGamePacketListener>)NetworkManager.toPacket(
            NetworkManager.s2c(), new SpawnEntityPacket.PacketPayload(entity, serverEntity), entity.registryAccess()
         );
      }
   }

   public static void register() {
      NetworkManager.registerS2CPayloadType(PACKET_TYPE, PACKET_CODEC);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Client {
      @OnlyIn(Dist.CLIENT)
      public static void register() {
         NetworkManager.registerReceiver(NetworkManager.s2c(), SpawnEntityPacket.PACKET_TYPE, SpawnEntityPacket.PACKET_CODEC, SpawnEntityPacket.Client::receive);
      }

      @OnlyIn(Dist.CLIENT)
      private static void receive(SpawnEntityPacket.PacketPayload payload, NetworkManager.PacketContext context) {
         context.queue(() -> {
            if (Minecraft.getInstance().level == null) {
               throw new IllegalStateException("Client world is null!");
            } else {
               Entity entity = payload.entityType().create(Minecraft.getInstance().level);
               if (entity == null) {
                  throw new IllegalStateException("Created entity is null!");
               } else {
                  entity.setUUID(payload.uuid());
                  entity.setId(payload.id());
                  entity.syncPacketPositionCodec(payload.x(), payload.y(), payload.z());
                  entity.moveTo(payload.x(), payload.y(), payload.z());
                  entity.setXRot(payload.xRot());
                  entity.setYRot(payload.yRot());
                  entity.setYHeadRot(payload.yHeadRot());
                  entity.setYBodyRot(payload.yHeadRot());
                  if (entity instanceof EntitySpawnExtension ext) {
                     RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), context.registryAccess());
                     ext.loadAdditionalSpawnData(buf);
                     buf.release();
                  }

                  Minecraft.getInstance().level.addEntity(entity);
                  entity.lerpMotion(payload.deltaX(), payload.deltaY(), payload.deltaZ());
               }
            }
         });
      }
   }

   private record PacketPayload(
      EntityType<?> entityType,
      UUID uuid,
      int id,
      double x,
      double y,
      double z,
      float xRot,
      float yRot,
      float yHeadRot,
      double deltaX,
      double deltaY,
      double deltaZ,
      byte[] data
   ) implements CustomPacketPayload {
      public PacketPayload(RegistryFriendlyByteBuf buf) {
         this(
            (EntityType<?>)ByteBufCodecs.registry(Registries.ENTITY_TYPE).decode(buf),
            buf.readUUID(),
            buf.readVarInt(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readByteArray()
         );
      }

      public PacketPayload(Entity entity, ServerEntity serverEntity) {
         this(
            entity.getType(),
            entity.getUUID(),
            entity.getId(),
            serverEntity.getPositionBase().x(),
            serverEntity.getPositionBase().y(),
            serverEntity.getPositionBase().z(),
            serverEntity.getLastSentXRot(),
            serverEntity.getLastSentYRot(),
            serverEntity.getLastSentYHeadRot(),
            serverEntity.getLastSentMovement().x,
            serverEntity.getLastSentMovement().y,
            serverEntity.getLastSentMovement().z,
            saveExtra(entity)
         );
      }

      public PacketPayload(Entity entity, BlockPos pos) {
         this(
            entity.getType(),
            entity.getUUID(),
            entity.getId(),
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            entity.getXRot(),
            entity.getYRot(),
            entity.getYHeadRot(),
            entity.getDeltaMovement().x,
            entity.getDeltaMovement().y,
            entity.getDeltaMovement().z,
            saveExtra(entity)
         );
      }

      private static byte[] saveExtra(Entity entity) {
         FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

         byte[] var6;
         try {
            if (entity instanceof EntitySpawnExtension ext) {
               ext.saveAdditionalSpawnData(buf);
            }

            var6 = ByteBufUtil.getBytes(buf);
         } finally {
            buf.release();
         }

         return var6;
      }

      public void write(RegistryFriendlyByteBuf buf) {
         ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(buf, this.entityType);
         buf.writeUUID(this.uuid);
         buf.writeVarInt(this.id);
         buf.writeDouble(this.x);
         buf.writeDouble(this.y);
         buf.writeDouble(this.z);
         buf.writeFloat(this.xRot);
         buf.writeFloat(this.yRot);
         buf.writeFloat(this.yHeadRot);
         buf.writeDouble(this.deltaX);
         buf.writeDouble(this.deltaY);
         buf.writeDouble(this.deltaZ);
         buf.writeByteArray(this.data);
      }

      public Type<? extends CustomPacketPayload> type() {
         return SpawnEntityPacket.PACKET_TYPE;
      }
   }
}
