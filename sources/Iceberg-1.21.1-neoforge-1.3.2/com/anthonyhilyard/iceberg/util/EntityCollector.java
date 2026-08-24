package com.anthonyhilyard.iceberg.util;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.events.common.LevelEvents;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;

public class EntityCollector extends Level {
   private final Level wrappedLevel;
   private final List<Entity> collectedEntities = Lists.newArrayList();
   private BlockState blockState = Blocks.AIR.defaultBlockState();
   private static final Map<Level, EntityCollector> wrappedLevelsMap = Maps.newHashMap();
   private static final Map<EntityCollector.ItemClassPair, Boolean> itemCreatesEntityResultCache = Maps.newHashMap();
   private static Map<Pair<Item, DataComponentMap>, List<Entity>> entityCache = Maps.newHashMap();
   private static boolean listenerRegistered = false;

   protected EntityCollector(Level wrapped) {
      super(new WritableLevelData() {
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

         public void setRaining(boolean isRaining) {
         }

         public boolean isHardcore() {
            return false;
         }

         public GameRules getGameRules() {
            return new GameRules();
         }

         public Difficulty getDifficulty() {
            return Difficulty.EASY;
         }

         public boolean isDifficultyLocked() {
            return false;
         }

         public void setSpawn(BlockPos blockPos, float f) {
         }
      }, wrapped.dimension(), wrapped.registryAccess(), wrapped.dimensionTypeRegistration(), wrapped.getProfilerSupplier(), false, wrapped.isDebug(), 0L, 0);
      this.wrappedLevel = wrapped;
      if (!listenerRegistered) {
         LevelEvents.UNLOAD.register(level -> wrappedLevelsMap.clear());
         listenerRegistered = true;
      }
   }

   public static EntityCollector of(Level wrappedLevel) {
      if (!wrappedLevelsMap.containsKey(wrappedLevel)) {
         wrappedLevelsMap.put(wrappedLevel, new EntityCollector(wrappedLevel));
      }

      return wrappedLevelsMap.get(wrappedLevel);
   }

   public static List<Entity> collectEntitiesFromItem(ItemStack itemStack) {
      Pair<Item, DataComponentMap> key = Pair.of(itemStack.getItem(), ItemUtil.getItemComponents(itemStack));
      if (!entityCache.containsKey(key)) {
         Minecraft minecraft = Minecraft.getInstance();
         List<Entity> entities = Lists.newArrayList();
         Item item = itemStack.getItem();
         ItemStack dummyStack = new ItemStack(item, itemStack.getCount());
         DataComponentMap itemComponents = ItemUtil.getItemComponents(itemStack);
         dummyStack.applyComponents(itemComponents);

         try {
            EntityCollector levelWrapper = of(minecraft.player.level());
            Player dummyPlayer = new Player(levelWrapper, BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "_dummy")) {
               public boolean isSpectator() {
                  return false;
               }

               public boolean isCreative() {
                  return false;
               }
            };
            dummyPlayer.setItemInHand(InteractionHand.MAIN_HAND, dummyStack);
            if (item instanceof SpawnEggItem spawnEggItem) {
               entities.add(spawnEggItem.getType(dummyStack).create(levelWrapper));
            } else {
               dummyStack.use(levelWrapper, dummyPlayer, InteractionHand.MAIN_HAND);
            }

            entities.addAll(levelWrapper.getCollectedEntities());
            if (entities.isEmpty()) {
               levelWrapper.setBlockState(Blocks.RAIL.defaultBlockState());
               dummyStack.useOn(
                  new UseOnContext(
                     levelWrapper, dummyPlayer, InteractionHand.MAIN_HAND, dummyStack, new BlockHitResult(Vec3.ZERO, Direction.DOWN, BlockPos.ZERO, false)
                  )
               );
               levelWrapper.setBlockState(Blocks.AIR.defaultBlockState());
               entities.addAll(levelWrapper.getCollectedEntities());
            }

            if (entities.isEmpty()) {
               levelWrapper.setBlockState(Blocks.STONE.defaultBlockState());
               dummyStack.useOn(
                  new UseOnContext(
                     levelWrapper, dummyPlayer, InteractionHand.MAIN_HAND, dummyStack, new BlockHitResult(Vec3.ZERO, Direction.NORTH, BlockPos.ZERO, false)
                  )
               );
               levelWrapper.setBlockState(Blocks.AIR.defaultBlockState());
               entities.addAll(levelWrapper.getCollectedEntities());
               CustomData customData = (CustomData)itemComponents.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
               if (!customData.isEmpty()) {
                  for (Entity entity : entities) {
                     customData.loadInto(entity);
                  }
               } else {
                  entities.clear();
               }
            }

            entities.removeIf(entityx -> entityx instanceof Projectile);
         } catch (Exception var12) {
            Iceberg.LOGGER.info("Unable to collect entities from \"" + itemStack.getItem().getName(itemStack).getString() + "\": " + var12.getMessage());
         }

         entityCache.put(key, entities);
      }

      return entityCache.get(key);
   }

   public static <T extends Entity> boolean itemCreatesEntity(ItemStack itemStack, Class<T> targetClass) {
      EntityCollector.ItemClassPair key = new EntityCollector.ItemClassPair(itemStack.getItem(), ItemUtil.getItemComponents(itemStack), targetClass);
      boolean result = false;
      if (!itemCreatesEntityResultCache.containsKey(key)) {
         for (Entity entity : collectEntitiesFromItem(itemStack)) {
            if (targetClass.isInstance(entity)) {
               result = true;
               break;
            }
         }

         itemCreatesEntityResultCache.put(key, result);
      }

      return itemCreatesEntityResultCache.get(key);
   }

   public List<Entity> getCollectedEntities() {
      List<Entity> entities = Lists.newArrayList();
      entities.addAll(this.collectedEntities);
      this.collectedEntities.clear();
      return entities;
   }

   public void setBlockState(BlockState blockState) {
      this.blockState = blockState;
   }

   public BlockState getBlockState(BlockPos blockPos) {
      return this.blockState;
   }

   public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
      return 15;
   }

   public boolean noCollision(Entity entity, AABB boundingBox) {
      return true;
   }

   public boolean addFreshEntity(Entity entity) {
      entity.setYRot(180.0F);
      this.collectedEntities.add(entity);
      return false;
   }

   public LevelTickAccess<Block> getBlockTicks() {
      return this.wrappedLevel.getBlockTicks();
   }

   public LevelTickAccess<Fluid> getFluidTicks() {
      return this.wrappedLevel.getFluidTicks();
   }

   public ChunkSource getChunkSource() {
      return this.wrappedLevel.getChunkSource();
   }

   public void levelEvent(Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
   }

   public void gameEvent(Holder<GameEvent> holder, Vec3 vec3, Context context) {
   }

   public List<? extends Player> players() {
      return this.wrappedLevel.players();
   }

   public Holder<Biome> getUncachedNoiseBiome(int p_204159_, int p_204160_, int p_204161_) {
      return this.wrappedLevel.getUncachedNoiseBiome(p_204159_, p_204160_, p_204161_);
   }

   public FeatureFlagSet enabledFeatures() {
      return this.wrappedLevel.enabledFeatures();
   }

   public float getShade(Direction p_45522_, boolean p_45523_) {
      return this.wrappedLevel.getShade(p_45522_, p_45523_);
   }

   public void sendBlockUpdated(BlockPos p_46612_, BlockState p_46613_, BlockState p_46614_, int p_46615_) {
   }

   public void playSeededSound(
      Player p_262953_,
      double p_263004_,
      double p_263398_,
      double p_263376_,
      Holder<SoundEvent> p_263359_,
      SoundSource p_263020_,
      float p_263055_,
      float p_262914_,
      long p_262991_
   ) {
   }

   public void playSeededSound(
      Player p_220372_, Entity p_220373_, Holder<SoundEvent> p_263500_, SoundSource p_220375_, float p_220376_, float p_220377_, long p_220378_
   ) {
   }

   public String gatherChunkSourceStats() {
      return this.wrappedLevel.gatherChunkSourceStats();
   }

   public Entity getEntity(int p_46492_) {
      return null;
   }

   public MapItemSavedData getMapData(MapId mapId) {
      return this.wrappedLevel.getMapData(mapId);
   }

   public void setMapData(MapId mapId, MapItemSavedData mapItemSavedData) {
   }

   public MapId getFreeMapId() {
      return this.wrappedLevel.getFreeMapId();
   }

   public void destroyBlockProgress(int p_46506_, BlockPos p_46507_, int p_46508_) {
   }

   public Scoreboard getScoreboard() {
      return this.wrappedLevel.getScoreboard();
   }

   public RecipeManager getRecipeManager() {
      return this.wrappedLevel.getRecipeManager();
   }

   public TickRateManager tickRateManager() {
      return this.wrappedLevel.tickRateManager();
   }

   public PotionBrewing potionBrewing() {
      return this.wrappedLevel.potionBrewing();
   }

   public LevelEntityGetter<Entity> getEntities() {
      return new LevelEntityGetter<Entity>() {
         public Entity get(int p_156931_) {
            return null;
         }

         public Entity get(UUID p_156939_) {
            return null;
         }

         public Iterable<Entity> getAll() {
            return List.of();
         }

         public <U extends Entity> void get(EntityTypeTest<Entity, U> p_156935_, AbortableIterationConsumer<U> p_261602_) {
         }

         public void get(AABB p_156937_, Consumer<Entity> p_156938_) {
         }

         public <U extends Entity> void get(EntityTypeTest<Entity, U> p_156932_, AABB p_156933_, AbortableIterationConsumer<U> p_261542_) {
         }
      };
   }

   private record ItemClassPair(Item item, DataComponentMap components, Class<?> targetClass) {
   }
}
