package net.joefoxe.hexerei.light;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.item.custom.BroomBrushItem;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightManager {
   private static final Set<LambHexereiDynamicLight> dynamicLightSources = new HashSet<>();
   private static final ReentrantReadWriteLock lightSourcesLock = new ReentrantReadWriteLock();
   public static long lastUpdate = System.currentTimeMillis();
   public static int lastUpdateCount = 0;
   private static final Map<EntityType<?>, List<Function<?, Integer>>> LIGHT_REGISTRY = new HashMap<>();
   private static final double MAX_RADIUS = 7.75;
   private static final double MAX_RADIUS_SQUARED = 60.0625;

   public static void init() {
      register(EntityType.FALLING_BLOCK, p -> p.getBlockState().getLightEmission(p.level(), p.blockPosition()));
      register(EntityType.ENDERMAN, enderMan -> enderMan.getCarriedBlock() != null ? DynamicLightUtil.fromItemLike(enderMan.getCarriedBlock().getBlock()) : 0);
      register(EntityType.ITEM, p -> DynamicLightUtil.fromItemLike(p.getItem().getItem()));
      register((EntityType<BroomEntity>)ModEntityTypes.BROOM.get(), LightManager::broomLightCheck);
      register(EntityType.PLAYER, p -> p.getVehicle() instanceof BroomEntity broom ? broomLightCheck(broom) : 0);
      register(EntityType.ITEM_FRAME, p -> DynamicLightUtil.fromItemLike(p.getItem().getItem()));
      register(EntityType.GLOW_ITEM_FRAME, p -> Math.max(14, DynamicLightUtil.fromItemLike(p.getItem().getItem())));
      register(EntityType.GLOW_SQUID, p -> (int)Mth.clampedLerp(0.0F, 12.0F, 1.0F - p.getDarkTicksRemaining() / 10.0F));
   }

   private static int broomLightCheck(BroomEntity broom) {
      if (broom.getModule(BroomEntity.BroomSlot.BRUSH).getItem() instanceof BroomBrushItem brushItem
         && brushItem.shouldGlow(broom.level(), broom.getModule(BroomEntity.BroomSlot.BRUSH))) {
         return 15;
      } else if (broom.getModule(BroomEntity.BroomSlot.MISC).getItem() instanceof KeychainItem keychainItem) {
         NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
         CompoundTag tag = ((CustomData)broom.getModule(BroomEntity.BroomSlot.MISC).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         if (!tag.isEmpty()) {
            ContainerHelper.loadAllItems(tag, items, broom.level().registryAccess());
         }

         return DynamicLightUtil.fromItemLike(((ItemStack)items.get(0)).getItem());
      } else {
         return 0;
      }
   }

   public static <T extends Entity> void register(EntityType<T> type, Function<T, Integer> luminanceFunction) {
      if (!LIGHT_REGISTRY.containsKey(type)) {
         LIGHT_REGISTRY.put(type, new ArrayList<>());
      }

      LIGHT_REGISTRY.get(type).add(luminanceFunction);
   }

   public static <T extends Entity> Map<EntityType<?>, List<Function<?, Integer>>> getLightRegistry() {
      return LIGHT_REGISTRY;
   }

   public static <T extends Entity> int getValue(T entity) {
      int val = 0;
      if (!LIGHT_REGISTRY.containsKey(entity.getType())) {
         return val;
      } else {
         EntityType<?> type = entity.getType();

         for (Function<?, Integer> function : LIGHT_REGISTRY.get(type)) {
            Integer value = ((Function<T, Integer>)function).apply(entity);
            if (value > val) {
               val = value;
            }
         }

         return val;
      }
   }

   public static boolean containsEntity(EntityType<? extends Entity> type) {
      return LIGHT_REGISTRY.containsKey(type) || HexConfig.ENTITY_LIGHT_MAP.containsKey(BuiltInRegistries.ENTITY_TYPE.getKey(type));
   }

   public static void addLightSource(LambHexereiDynamicLight lightSource) {
      if (lightSource.getDynamicLightWorldH().isClientSide()) {
         if (shouldUpdateDynamicLight()) {
            if (!containsLightSource(lightSource)) {
               lightSourcesLock.writeLock().lock();
               dynamicLightSources.add(lightSource);
               lightSourcesLock.writeLock().unlock();
            }
         }
      }
   }

   public static boolean containsLightSource(@NotNull LambHexereiDynamicLight lightSource) {
      if (!lightSource.getDynamicLightWorldH().isClientSide()) {
         return false;
      } else {
         lightSourcesLock.readLock().lock();
         boolean result = dynamicLightSources.contains(lightSource);
         lightSourcesLock.readLock().unlock();
         return result;
      }
   }

   public int getLightSourcesCount() {
      lightSourcesLock.readLock().lock();
      int result = dynamicLightSources.size();
      lightSourcesLock.readLock().unlock();
      return result;
   }

   public static void removeLightSource(LambHexereiDynamicLight lightSource) {
      lightSourcesLock.writeLock().lock();
      Iterator<LambHexereiDynamicLight> sourceIterator = dynamicLightSources.iterator();

      while (sourceIterator.hasNext()) {
         LambHexereiDynamicLight it = sourceIterator.next();
         if (it.equals(lightSource)) {
            sourceIterator.remove();
            if (Minecraft.getInstance().level != null) {
               lightSource.lambdynlights$scheduleTrackedChunksRebuildH(Minecraft.getInstance().levelRenderer);
            }
            break;
         }
      }

      lightSourcesLock.writeLock().unlock();
   }

   public static void clearLightSources() {
      lightSourcesLock.writeLock().lock();
      Iterator<LambHexereiDynamicLight> sourceIterator = dynamicLightSources.iterator();

      while (sourceIterator.hasNext()) {
         LambHexereiDynamicLight it = sourceIterator.next();
         sourceIterator.remove();
         if (Minecraft.getInstance().levelRenderer != null) {
            if (it.getLuminanceH() > 0) {
               it.resetDynamicLightH();
            }

            it.lambdynlights$scheduleTrackedChunksRebuildH(Minecraft.getInstance().levelRenderer);
         }
      }

      lightSourcesLock.writeLock().unlock();
   }

   public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, @NotNull BlockPos chunkPos) {
      scheduleChunkRebuild(renderer, chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
   }

   public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, long chunkPos) {
      scheduleChunkRebuild(renderer, BlockPos.getX(chunkPos), BlockPos.getY(chunkPos), BlockPos.getZ(chunkPos));
   }

   public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, int x, int y, int z) {
      if (Minecraft.getInstance().level != null) {
         renderer.setSectionDirty(x, y, z);
      }
   }

   public static void updateAll(LevelRenderer renderer) {
      long now = System.currentTimeMillis();
      lastUpdate = now;
      lastUpdateCount = 0;
      lightSourcesLock.readLock().lock();

      for (LambHexereiDynamicLight lightSource : dynamicLightSources) {
         if (lightSource.lambdynlights$updateDynamicLightH(renderer)) {
            lastUpdateCount++;
         }
      }

      lightSourcesLock.readLock().unlock();
   }

   public static void updateTrackedChunks(@NotNull BlockPos chunkPos, @Nullable LongOpenHashSet old, @Nullable LongOpenHashSet newPos) {
      if (old != null || newPos != null) {
         long pos = chunkPos.asLong();
         if (old != null) {
            old.remove(pos);
         }

         if (newPos != null) {
            newPos.add(pos);
         }
      }
   }

   public static int getLightmapWithDynamicLight(@NotNull BlockPos pos, int lightmap) {
      return getLightmapWithDynamicLight(getDynamicLightLevelWorld(pos), lightmap);
   }

   public static int getLightmapWithDynamicLight(double dynamicLightLevel, int lightmap) {
      if (dynamicLightLevel > 0.0) {
         int blockLevel = getBlockLightNoPatch(lightmap);
         if (dynamicLightLevel > blockLevel) {
            int luminance = (int)(dynamicLightLevel * 16.0);
            lightmap &= -1048576;
            lightmap |= luminance & 1048575;
         }
      }

      return lightmap;
   }

   public static int getBlockLightNoPatch(int light) {
      return light >> 4 & 65535;
   }

   public static double getDynamicLightLevel(@NotNull BlockPos pos) {
      double result = 0.0;
      lightSourcesLock.readLock().lock();

      for (LambHexereiDynamicLight lightSource : dynamicLightSources) {
         result = maxDynamicLightLevel(pos, lightSource, result);
      }

      lightSourcesLock.readLock().unlock();
      return Mth.clamp(result, 0.0, 15.0);
   }

   public static double getDynamicLightLevelWorld(@NotNull BlockPos pos) {
      double result = 0.0;
      lightSourcesLock.readLock().lock();

      for (LambHexereiDynamicLight lightSource : dynamicLightSources) {
         result = maxDynamicLightLevel(pos, lightSource, result);
      }

      lightSourcesLock.readLock().unlock();
      return Mth.clamp(result, 0.0, 15.0);
   }

   public static double maxDynamicLightLevel(@NotNull BlockPos pos, @NotNull LambHexereiDynamicLight lightSource, double currentLightLevel) {
      int luminance = lightSource.getLuminanceH();
      if (luminance > 0) {
         double dx = pos.getX() - lightSource.getDynamicLightXH() + 0.5;
         double dy = pos.getY() - lightSource.getDynamicLightYH() + 0.5;
         double dz = pos.getZ() - lightSource.getDynamicLightZH() + 0.5;
         double distanceSquared = dx * dx + dy * dy + dz * dz;
         if (distanceSquared <= 60.0625) {
            double multiplier = 1.0 - Math.sqrt(distanceSquared) / 7.75;
            double lightLevel = multiplier * luminance;
            if (lightLevel > currentLightLevel) {
               return lightLevel;
            }
         }
      }

      return currentLightLevel;
   }

   public static void updateLightTracking(@NotNull LambHexereiDynamicLight lightSource) {
      boolean enabled = lightSource.isDynamicLightEnabledH();
      int luminance = lightSource.getLuminanceH();
      if (!enabled && luminance > 0) {
         lightSource.setHexereiDynamicLightEnabled(true);
      } else if (enabled && luminance < 1) {
         lightSource.setHexereiDynamicLightEnabled(false);
      }
   }

   public static boolean shouldUpdateDynamicLight() {
      return (Boolean)HexConfig.DYNAMIC_LIGHT_TOGGLE.get();
   }

   public static void toggleLightsAndConfig(boolean enabled) {
      if (ModList.get().isLoaded("ars_nouveau")) {
         enabled = false;
      }

      HexConfig.DYNAMIC_LIGHT_TOGGLE.set(enabled);
      HexConfig.DYNAMIC_LIGHT_TOGGLE.save();
      if (!enabled) {
         clearLightSources();
      }
   }
}
