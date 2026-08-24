package dev.corgitaco.dataanchor.data.type.chunk.network;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.chunk.SyncedLevelChunkTrackedData;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public record SyncLevelChunkTrackedDataS2C(TrackedDataKey<SyncedLevelChunkTrackedData> dataKey, ChunkPos pos, CompoundTag tag) implements Packet {
   public static final StreamCodec<RegistryFriendlyByteBuf, SyncLevelChunkTrackedDataS2C> STREAM_CODEC = CustomPacketPayload.codec(
      SyncLevelChunkTrackedDataS2C::write, SyncLevelChunkTrackedDataS2C::new
   );
   public static final Type<SyncLevelChunkTrackedDataS2C> TYPE = new Type(DataAnchor.id("chunk_tracked_data"));

   public SyncLevelChunkTrackedDataS2C(FriendlyByteBuf buf) {
      this(TrackedDataKey.fromID(TrackedDataRegistries.CHUNK, buf.readResourceLocation()), new ChunkPos(buf.readInt(), buf.readInt()), buf.readNbt());
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.dataKey.getId());
      buf.writeInt(this.pos.x);
      buf.writeInt(this.pos.z);
      buf.writeNbt(this.tag);
   }

   @Override
   public void handle(@Nullable Level level, @Nullable Player player) {
      LevelChunk chunk = level.getChunk(this.pos.x, this.pos.z);
      if (!chunk.isEmpty() && chunk instanceof TrackedDataContainer access) {
         access.dataAnchor$getTrackedData(this.dataKey).ifPresent(data -> {
            if (data instanceof SyncedLevelChunkTrackedData trackedData) {
               trackedData.readFromNetwork(this.tag);
            }
         });
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
