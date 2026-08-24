package dev.corgitaco.dataanchor.data.type.level.network;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.level.SyncedLevelTrackedData;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record SyncLevelTrackedDataS2C(TrackedDataKey<SyncedLevelTrackedData> dataKey, CompoundTag tag) implements Packet {
   public static final StreamCodec<RegistryFriendlyByteBuf, SyncLevelTrackedDataS2C> STREAM_CODEC = CustomPacketPayload.codec(
      SyncLevelTrackedDataS2C::write, SyncLevelTrackedDataS2C::new
   );
   public static final Type<SyncLevelTrackedDataS2C> TYPE = new Type(DataAnchor.id("level_tracked_data"));

   public SyncLevelTrackedDataS2C(FriendlyByteBuf buf) {
      this(TrackedDataKey.fromID(TrackedDataRegistries.LEVEL, buf.readResourceLocation()), buf.readNbt());
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.dataKey.getId());
      buf.writeNbt(this.tag);
   }

   @Override
   public void handle(@Nullable Level level, @Nullable Player player) {
      if (level != null && level instanceof TrackedDataContainer access) {
         access.dataAnchor$getTrackedData(this.dataKey).ifPresent(data -> {
            if (data instanceof SyncedLevelTrackedData syncedData) {
               syncedData.readFromNetwork(this.tag);
            }
         });
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
