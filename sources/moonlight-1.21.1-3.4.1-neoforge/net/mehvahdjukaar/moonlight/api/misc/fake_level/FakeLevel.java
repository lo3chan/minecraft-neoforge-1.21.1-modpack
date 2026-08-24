package net.mehvahdjukaar.moonlight.api.misc.fake_level;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeLevel extends Level {
   private final Scoreboard scoreboard = new Scoreboard();
   private final RecipeManager recipeManager;
   private final MapId mapId = new MapId(0);
   private final TickRateManager tickRateManager = new TickRateManager();
   private final ChunkSource chunkManager = new FakeLevel.DummyChunkSource();
   private final FakeLevel.DummyLevelEntityGetter<Entity> entityGetter = new FakeLevel.DummyLevelEntityGetter();
   private final LevelTickAccess<Block> blockTicks = new FakeLevel.EmptyLevelTickAccess<>();
   private final LevelTickAccess<Fluid> fluidTicks = new FakeLevel.EmptyLevelTickAccess<>();

   @Deprecated(
      forRemoval = true
   )
   protected FakeLevel(String id, RegistryAccess registryAccess) {
      this(true, id, registryAccess);
   }

   protected FakeLevel(boolean clientside, String id, RegistryAccess registryAccess) {
      super(
         new FakeLevel.DummyData(),
         ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(id)),
         registryAccess,
         registryAccess.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD),
         () -> InactiveProfiler.INSTANCE,
         true,
         clientside,
         0L,
         0
      );
      this.recipeManager = new RecipeManager(registryAccess);
   }

   public void setDayTimePerTick(float dayTimePerTick) {
   }

   public float getDayTimePerTick() {
      return -1.0F;
   }

   public void setDayTimeFraction(float dayTimeFraction) {
   }

   public float getDayTimeFraction() {
      return 0.0F;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public ChunkSource getChunkSource() {
      return this.chunkManager;
   }

   @Nullable
   public MinecraftServer getServer() {
      return PlatHelper.getCurrentServer();
   }

   public void playSound(Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
   }

   public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
   }

   public void playSeededSound(
      @Nullable Player player, double d, double e, double f, Holder<SoundEvent> holder, SoundSource soundSource, float g, float h, long l
   ) {
   }

   public void playSeededSound(
      @Nullable Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float p_220369_, float p_220370_, long p_220371_
   ) {
   }

   public void playSeededSound(@Nullable Player player, Entity entity, Holder<SoundEvent> holder, SoundSource soundSource, float f, float g, long l) {
   }

   public void playSound(Player player, Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch) {
   }

   public String gatherChunkSourceStats() {
      return "";
   }

   public Entity getEntity(int id) {
      return null;
   }

   public TickRateManager tickRateManager() {
      return this.tickRateManager;
   }

   @Nullable
   public MapItemSavedData getMapData(MapId mapId) {
      return null;
   }

   public void setMapData(MapId mapId, MapItemSavedData mapData) {
   }

   public MapId getFreeMapId() {
      return this.mapId;
   }

   public void destroyBlockProgress(int entityId, BlockPos pos, int progress) {
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   protected LevelEntityGetter<Entity> getEntities() {
      return this.entityGetter;
   }

   public LevelTickAccess<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public LevelTickAccess<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   public void levelEvent(Player player, int eventId, BlockPos pos, int data) {
   }

   public void gameEvent(Holder<GameEvent> gameEvent, Vec3 pos, Context context) {
   }

   public float getShade(Direction direction, boolean shaded) {
      return 0.0F;
   }

   public List<? extends Player> players() {
      return List.of();
   }

   public PotionBrewing potionBrewing() {
      throw new UnsupportedOperationException("This level does not support potion brewing. Sorry...");
   }

   public FeatureFlagSet enabledFeatures() {
      return FeatureFlags.DEFAULT_FLAGS;
   }

   public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
      return getPlains(this.registryAccess());
   }

   @NotNull
   private static Reference<Biome> getPlains(RegistryAccess registryAccess) {
      return (Reference<Biome>)((Registry)registryAccess.registry(Registries.BIOME).get())
         .getHolder(ResourceKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("plains")))
         .get();
   }

   private class DummyChunkSource extends ChunkSource {
      private final LevelLightEngine lightEngine = new LevelLightEngine(this, true, FakeLevel.this.dimensionType().hasSkyLight());

      public DummyChunkSource() {
      }

      @Nullable
      public ChunkAccess getChunk(int x, int z, ChunkStatus chunkStatus, boolean requireChunk) {
         return new EmptyLevelChunk(
            FakeLevel.this, new ChunkPos(x, z), FakeLevel.this.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.FOREST)
         );
      }

      public void tick(BooleanSupplier supplier, boolean b) {
      }

      public String gatherStats() {
         return "";
      }

      public int getLoadedChunksCount() {
         return 0;
      }

      public LevelLightEngine getLightEngine() {
         return this.lightEngine;
      }

      public BlockGetter getLevel() {
         return FakeLevel.this;
      }
   }

   protected static class DummyData implements WritableLevelData {
      final GameRules gameRules = new GameRules();

      public BlockPos getSpawnPos() {
         return BlockPos.ZERO;
      }

      public float getSpawnAngle() {
         return 0.0F;
      }

      public long getGameTime() {
         return 0L;
      }

      public long getDayTime() {
         return 0L;
      }

      public boolean isThundering() {
         return false;
      }

      public boolean isRaining() {
         return false;
      }

      public void setRaining(boolean raining) {
      }

      public boolean isHardcore() {
         return false;
      }

      public GameRules getGameRules() {
         return this.gameRules;
      }

      public Difficulty getDifficulty() {
         return Difficulty.NORMAL;
      }

      public boolean isDifficultyLocked() {
         return false;
      }

      public void setSpawn(BlockPos spawnPoint, float spawnAngle) {
      }
   }

   public static class DummyLevelEntityGetter<T extends EntityAccess> implements LevelEntityGetter<T> {
      public T get(int id) {
         return null;
      }

      public T get(UUID pUuid) {
         return null;
      }

      public Iterable<T> getAll() {
         return Collections.emptyList();
      }

      public <U extends T> void get(EntityTypeTest<T, U> tuEntityTypeTest, AbortableIterationConsumer<U> uAbortableIterationConsumer) {
      }

      public void get(AABB boundingBox, Consumer<T> tConsumer) {
      }

      public <U extends T> void get(EntityTypeTest<T, U> tuEntityTypeTest, AABB bounds, AbortableIterationConsumer<U> uAbortableIterationConsumer) {
      }
   }

   private static class EmptyLevelTickAccess<T> implements LevelTickAccess<T> {
      public boolean willTickThisTick(BlockPos pos, T type) {
         return false;
      }

      public void schedule(ScheduledTick<T> tick) {
      }

      public boolean hasScheduledTick(BlockPos pos, T type) {
         return false;
      }

      public int count() {
         return 0;
      }
   }
}
