package top.theillusivec4.curios.common.network.server.sync;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class SPacketSyncCurios implements CustomPacketPayload {
   public static final Type<SPacketSyncCurios> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("curios", "sync_curios"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SPacketSyncCurios> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, SPacketSyncCurios>() {
      @Nonnull
      public SPacketSyncCurios decode(@Nonnull RegistryFriendlyByteBuf buf) {
         return new SPacketSyncCurios(buf);
      }

      public void encode(@Nonnull RegistryFriendlyByteBuf buf, SPacketSyncCurios packet) {
         buf.writeInt(packet.entityId);
         buf.writeInt(packet.entrySize);

         for (Entry<String, CompoundTag> entry : packet.map.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeNbt((Tag)entry.getValue());
         }
      }
   };
   public final int entityId;
   public final int entrySize;
   public final Map<String, CompoundTag> map;

   public SPacketSyncCurios(int entityId, Map<String, ICurioStacksHandler> map) {
      Map<String, CompoundTag> result = new LinkedHashMap<>();

      for (Entry<String, ICurioStacksHandler> entry : map.entrySet()) {
         result.put(entry.getKey(), entry.getValue().getSyncTag());
      }

      this.entityId = entityId;
      this.entrySize = map.size();
      this.map = result;
   }

   public SPacketSyncCurios(FriendlyByteBuf buf) {
      int entityId = buf.readInt();
      int entrySize = buf.readInt();
      Map<String, CompoundTag> map = new LinkedHashMap<>();

      for (int i = 0; i < entrySize; i++) {
         String key = buf.readUtf();
         map.put(key, buf.readNbt());
      }

      this.entityId = entityId;
      this.entrySize = map.size();
      this.map = map;
   }

   @Nonnull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
