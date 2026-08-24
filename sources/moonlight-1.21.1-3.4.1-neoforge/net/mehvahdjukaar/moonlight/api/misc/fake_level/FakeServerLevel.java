package net.mehvahdjukaar.moonlight.api.misc.fake_level;

import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder.Settings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class FakeServerLevel extends ServerLevel {
   private final ServerScoreboard scoreboard;

   public FakeServerLevel(String name, ServerLevel original) {
      super(
         original.getServer(),
         Util.backgroundExecutor(),
         original.getServer().storageSource,
         new FakeServerLevel.ReadOlyServerLevelData(name, original.serverLevelData),
         ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(name)),
         new LevelStem(original.dimensionTypeRegistration(), original.getChunkSource().getGenerator()),
         new FakeServerLevel.DummyProgressListener(),
         false,
         0L,
         Collections.emptyList(),
         false,
         new RandomSequences(0L)
      );
      this.players().clear();
      this.scoreboard = new ServerScoreboard(original.getServer());
   }

   @Internal
   public static ServerChunkCache createDummyChunkCache(
      ServerLevel level,
      LevelStorageAccess levelStorageAccess,
      DataFixer fixerUpper,
      StructureTemplateManager structureManager,
      Executor dispatcher,
      ChunkGenerator generator,
      int viewDistance,
      int simulationDistance,
      boolean sync,
      ChunkProgressListener progressListener,
      ChunkStatusUpdateListener chunkStatusListener,
      Supplier<DimensionDataStorage> dataStorage
   ) {
      return new FakeServerLevel.DummyServerChunkCache(
         level,
         levelStorageAccess,
         fixerUpper,
         structureManager,
         Util.backgroundExecutor(),
         generator,
         viewDistance,
         simulationDistance,
         sync,
         new FakeServerLevel.DummyProgressListener(),
         chunkStatusListener,
         dataStorage
      );
   }

   public static <A extends EntityAccess> PersistentEntitySectionManager<A> createDummyEntityManager(
      Class<A> entityClass, LevelCallback callbacks, EntityPersistentStorage permanentStorage
   ) {
      return new FakeServerLevel.DummyEntityManager<>(entityClass, callbacks, permanentStorage);
   }

   public BlockPos getSharedSpawnPos() {
      return BlockPos.ZERO;
   }

   public float getSharedSpawnAngle() {
      return 0.0F;
   }

   public boolean noCollision(Entity entity) {
      return super.noCollision(entity);
   }

   public Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, AABB collisionBox) {
      return Collections.emptyList();
   }

   public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB collisionBox) {
      return Collections.emptyList();
   }

   public void playSound(Player player, double x, double y, double z, SoundEvent soundIn, SoundSource category, float volume, float pitch) {
   }

   public void playSound(Player player, Entity entity, SoundEvent soundEvent, SoundSource category, float volume, float pitch) {
   }

   public void playSeededSound(@Nullable Player player, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {
   }

   public void playSeededSound(
      @Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, long seed
   ) {
   }

   public void playSeededSound(
      @Nullable Player player, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed
   ) {
   }

   public void levelEvent(Player player, int type, BlockPos pos, int data) {
   }

   public void globalLevelEvent(int id, BlockPos pos, int data) {
   }

   public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
   }

   public void gameEvent(Holder<GameEvent> gameEvent, BlockPos pos, Context context) {
   }

   public void setDefaultSpawnPos(BlockPos pos, float angle) {
   }

   protected void tickTime() {
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public void save(@Nullable ProgressListener progress, boolean flush, boolean skipSave) {
   }

   @Nullable
   public BlockPos findNearestMapStructure(TagKey<Structure> structureTag, BlockPos pos, int radius, boolean skipExistingChunks) {
      return null;
   }

   public void setMapData(MapId mapId, MapItemSavedData mapData) {
      super.setMapData(mapId, mapData);
   }

   @Nullable
   public MapItemSavedData getMapData(MapId mapId) {
      return null;
   }

   public boolean setChunkForced(int chunkX, int chunkZ, boolean add) {
      return false;
   }

   public void setBlockEntity(BlockEntity blockEntity) {
   }

   public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
      return false;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos) {
      return null;
   }

   public BlockState getBlockState(BlockPos pos) {
      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos pos) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Nullable
   public Entity getEntity(int id) {
      return null;
   }

   public void tick(BooleanSupplier hasTimeLeft) {
   }

   private static class DummyEntityManager<A extends EntityAccess> extends PersistentEntitySectionManager<A> {
      public DummyEntityManager(Class entityClass, LevelCallback callbacks, EntityPersistentStorage permanentStorage) {
         super(entityClass, callbacks, permanentStorage);
      }

      public void saveAll() {
      }

      public void close() throws IOException {
         super.close();
      }
   }

   public static class DummyProgressListener implements ChunkProgressListener {
      public void updateSpawnPos(ChunkPos center) {
      }

      public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
      }

      public void start() {
      }

      public void stop() {
      }
   }

   private static class DummyServerChunkCache extends ServerChunkCache {
      private EmptyLevelChunk emptyChunkInstance;

      public DummyServerChunkCache(
         ServerLevel level,
         LevelStorageAccess levelStorageAccess,
         DataFixer fixerUpper,
         StructureTemplateManager structureManager,
         Executor dispatcher,
         ChunkGenerator generator,
         int viewDistance,
         int simulationDistance,
         boolean sync,
         ChunkProgressListener progressListener,
         ChunkStatusUpdateListener chunkStatusListener,
         Supplier<DimensionDataStorage> overworldDataStorage
      ) {
         super(
            level,
            levelStorageAccess,
            fixerUpper,
            structureManager,
            dispatcher,
            generator,
            viewDistance,
            simulationDistance,
            sync,
            progressListener,
            chunkStatusListener,
            overworldDataStorage
         );
      }

      public void tick(BooleanSupplier hasTimeLeft, boolean tickChunks) {
      }

      public ChunkAccess getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
         return this.getEmptyChunk(x, z);
      }

      @Nullable
      public LevelChunk getChunkNow(int chunkX, int chunkZ) {
         return this.getEmptyChunk(chunkX, chunkZ);
      }

      public CompletableFuture<ChunkResult<ChunkAccess>> getChunkFuture(int x, int z, ChunkStatus chunkStatus, boolean requireChunk) {
         return CompletableFuture.completedFuture(ChunkResult.of(this.getEmptyChunk(x, z)));
      }

      public boolean hasChunk(int chunkX, int chunkZ) {
         return true;
      }

      @Nullable
      public LightChunk getChunkForLighting(int chunkX, int chunkZ) {
         return this.getEmptyChunk(chunkX, chunkZ);
      }

      @NotNull
      private EmptyLevelChunk getEmptyChunk(int x, int z) {
         if (this.emptyChunkInstance == null) {
            this.emptyChunkInstance = new EmptyLevelChunk(
               this.getLevel(), new ChunkPos(0, 0), this.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.FOREST)
            );
         }

         return this.emptyChunkInstance;
      }

      public void close() throws IOException {
         super.close();
      }

      public void save(boolean flush) {
      }
   }

   public static class ReadOlyServerLevelData implements ServerLevelData {
      public final String name;
      public final ServerLevelData wrapped;
      private final TimerQueue<MinecraftServer> timerQueue = new TimerQueue(TimerCallbacks.SERVER_CALLBACKS);

      public ReadOlyServerLevelData(String name, ServerLevelData wrapped) {
         this.name = name;
         this.wrapped = wrapped;
      }

      public void setDayTimePerTick(float dayTimePerTick) {
      }

      public void setDayTimeFraction(float dayTimeFraction) {
      }

      public float getDayTimeFraction() {
         return 0.0F;
      }

      public float getDayTimePerTick() {
         return -1.0F;
      }

      public String getLevelName() {
         return this.name;
      }

      public void setThundering(boolean thundering) {
      }

      public int getRainTime() {
         return this.wrapped.getRainTime();
      }

      public void setRainTime(int time) {
      }

      public void setThunderTime(int time) {
      }

      public int getThunderTime() {
         return this.wrapped.getThunderTime();
      }

      public int getClearWeatherTime() {
         return this.wrapped.getClearWeatherTime();
      }

      public void setClearWeatherTime(int time) {
      }

      public int getWanderingTraderSpawnDelay() {
         return this.wrapped.getWanderingTraderSpawnDelay();
      }

      public void setWanderingTraderSpawnDelay(int delay) {
      }

      public int getWanderingTraderSpawnChance() {
         return this.wrapped.getWanderingTraderSpawnChance();
      }

      public void setWanderingTraderSpawnChance(int chance) {
      }

      @Nullable
      public UUID getWanderingTraderId() {
         return this.wrapped.getWanderingTraderId();
      }

      public void setWanderingTraderId(UUID id) {
      }

      public GameType getGameType() {
         return this.wrapped.getGameType();
      }

      public void setWorldBorder(Settings serializer) {
      }

      public Settings getWorldBorder() {
         return this.wrapped.getWorldBorder();
      }

      public boolean isInitialized() {
         return this.wrapped.isInitialized();
      }

      public void setInitialized(boolean initialized) {
      }

      public boolean isAllowCommands() {
         return this.wrapped.isAllowCommands();
      }

      public void setGameType(GameType type) {
      }

      public TimerQueue<MinecraftServer> getScheduledEvents() {
         return this.timerQueue;
      }

      public void setGameTime(long time) {
      }

      public void setDayTime(long time) {
      }

      public BlockPos getSpawnPos() {
         return this.wrapped.getSpawnPos();
      }

      public float getSpawnAngle() {
         return this.wrapped.getSpawnAngle();
      }

      public long getGameTime() {
         return this.wrapped.getGameTime();
      }

      public long getDayTime() {
         return this.wrapped.getDayTime();
      }

      public boolean isThundering() {
         return this.wrapped.isThundering();
      }

      public boolean isRaining() {
         return this.wrapped.isRaining();
      }

      public void setRaining(boolean raining) {
      }

      public boolean isHardcore() {
         return this.wrapped.isHardcore();
      }

      public GameRules getGameRules() {
         return this.wrapped.getGameRules();
      }

      public Difficulty getDifficulty() {
         return this.wrapped.getDifficulty();
      }

      public boolean isDifficultyLocked() {
         return this.wrapped.isDifficultyLocked();
      }

      public void setSpawn(BlockPos spawnPoint, float spawnAngle) {
      }
   }
}
