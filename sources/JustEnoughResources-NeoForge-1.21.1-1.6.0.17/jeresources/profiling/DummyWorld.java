package jeresources.profiling;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public class DummyWorld extends ServerLevel {
   public List<Entity> spawnedEntities = new ArrayList<>();

   public DummyWorld(ServerLevel level) {
      super(null, null, null, null, null, null, null, false, 0L, null, false, null);
   }

   public void clearChunks() {
   }

   @Nullable
   public Entity getEntity(int id) {
      return null;
   }

   @Nullable
   public MapItemSavedData getMapData(MapId mapId) {
      return super.getMapData(mapId);
   }

   public void setMapData(MapId mapId, MapItemSavedData mapData) {
      this.getServer().overworld().getDataStorage().set(mapId.key(), mapData);
   }

   public MapId getFreeMapId() {
      return new MapId(0);
   }

   public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
   }

   public RecipeManager getRecipeManager() {
      return null;
   }

   public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
      if (this.isOutsideBuildHeight(pos) && this.isLoaded(pos)) {
         ChunkAccess chunk = this.getChunk(pos);
         BlockState blockState = chunk.setBlockState(pos, newState, false);
         return blockState != null;
      } else {
         return false;
      }
   }

   public boolean setBlockAndUpdate(BlockPos pos, BlockState state) {
      return this.setBlock(pos, state, 3);
   }

   public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
   }

   public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent soundIn, SoundSource source, float volume, float pitch) {
   }

   public void playSound(
      @Nullable Player p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundSource p_217384_4_, float p_217384_5_, float p_217384_6_
   ) {
   }

   public boolean addFreshEntity(Entity entity) {
      this.spawnedEntities.add(entity);
      return true;
   }

   public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
   }

   private static class DummyChunkSource extends ChunkSource {
      private final Level realLevel;
      private final ChunkSource realChunkSource;
      private boolean allowLoading = true;

      public DummyChunkSource(Level realLevel, ChunkSource serverChunkSource) {
         this.realLevel = realLevel;
         this.realChunkSource = serverChunkSource;
      }

      @Nullable
      public ChunkAccess getChunk(int x, int z, ChunkStatus requiredStatus, boolean load) {
         return null;
      }

      public void tick(BooleanSupplier booleanSupplier, boolean bool) {
      }

      public String gatherStats() {
         return "Dummy";
      }

      public int getLoadedChunksCount() {
         return 0;
      }

      public LevelLightEngine getLightEngine() {
         return null;
      }

      public BlockGetter getLevel() {
         return null;
      }
   }
}
