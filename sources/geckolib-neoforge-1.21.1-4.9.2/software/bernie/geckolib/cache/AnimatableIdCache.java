package software.bernie.geckolib.cache;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public final class AnimatableIdCache extends SavedData {
   private static final Factory<AnimatableIdCache> FACTORY = new Factory(AnimatableIdCache::new, AnimatableIdCache::new, null);
   private static final String DATA_KEY = "geckolib_id_cache";
   private long lastId;

   private AnimatableIdCache() {
   }

   private AnimatableIdCache(CompoundTag tag, Provider registryLookup) {
      this.lastId = tag.getLong("last_id");
   }

   public static long getFreeId(ServerLevel level) {
      return getCache(level).getNextId();
   }

   private long getNextId() {
      this.setDirty();
      return ++this.lastId;
   }

   public CompoundTag save(CompoundTag tag, Provider registryLookup) {
      tag.putLong("last_id", this.lastId);
      return tag;
   }

   private static AnimatableIdCache getCache(ServerLevel level) {
      return (AnimatableIdCache)level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, "geckolib_id_cache");
   }
}
