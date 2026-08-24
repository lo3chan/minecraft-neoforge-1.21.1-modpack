package net.mehvahdjukaar.moonlight.api.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundSyncWorldDataMessage;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class WorldSavedData extends SavedData {
   public final CompoundTag save(CompoundTag tag, Provider registries) {
      Codec codec = this.getType().getCodec();
      RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
      DataResult dataResult = codec.encode(this, ops, new CompoundTag());
      CompoundTag inner = (CompoundTag)dataResult.getOrThrow();
      tag.put(this.getType().getName(), inner);
      return tag;
   }

   public void setDirty(boolean dirty) {
      super.setDirty(dirty);
   }

   public void sync() {
      if (this.getType().isSyncable()) {
         NetworkHelper.sendToAllClientPlayers(new ClientBoundSyncWorldDataMessage<>(this));
      }
   }

   public abstract WorldSavedDataType<? extends WorldSavedData> getType();

   public void onReassigned(Level level) {
   }
}
