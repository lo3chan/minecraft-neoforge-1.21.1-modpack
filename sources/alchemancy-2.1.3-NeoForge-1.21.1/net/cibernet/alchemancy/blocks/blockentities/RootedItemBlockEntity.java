package net.cibernet.alchemancy.blocks.blockentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.cibernet.alchemancy.registries.AlchemancyPoiTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ticket.SimpleTicket;
import org.jetbrains.annotations.Nullable;

public class RootedItemBlockEntity extends ItemStackHolderBlockEntity {
   private int tickCount = 0;
   @Nullable
   private SimpleTicket<Vec3> farmlandWaterManager;
   private static final TreeMap<ResourceKey<Level>, Collection<RootedItemBlockEntity>> CACHED_ROOTED_ITEMS = new TreeMap<>();

   public static void cahceRootedItems(ServerLevel level, List<LevelChunk> chunks) {
      ResourceKey<Level> key = level.dimension();
      Collection<RootedItemBlockEntity> roots = CACHED_ROOTED_ITEMS.getOrDefault(key, new ArrayList<>());
      roots.clear();

      for (LevelChunk chunk : chunks) {
         level.getPoiManager().getInChunk(type -> type.equals(AlchemancyPoiTypes.ROOTED_ITEM), chunk.getPos(), Occupancy.ANY).forEach(poiRecord -> {
            if (level.getBlockEntity(poiRecord.getPos()) instanceof RootedItemBlockEntity root) {
               roots.add(root);
            }
         });
      }

      CACHED_ROOTED_ITEMS.put(key, roots);
   }

   public static Collection<RootedItemBlockEntity> getCachedRoots(ServerLevel level) {
      return CACHED_ROOTED_ITEMS.getOrDefault(level.dimension(), List.of());
   }

   public RootedItemBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType<? extends ItemStackHolderBlockEntity>)AlchemancyBlockEntities.ROOTED_ITEM.get(), pPos, pBlockState);
   }

   public void invalidateCapabilities() {
      if (this.farmlandWaterManager != null && this.farmlandWaterManager.isValid()) {
         this.farmlandWaterManager.invalidate();
      }

      super.invalidateCapabilities();
   }

   public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, RootedItemBlockEntity root) {
      List<LivingEntity> entitiesInBounds = pLevel.getEntitiesOfClass(LivingEntity.class, root.getBlockState().getShape(pLevel, pPos).bounds().move(pPos));
      InfusedPropertiesHelper.forEachProperty(root.getItem(), propertyHolder -> ((Property)propertyHolder.value()).onRootedTick(root, entitiesInBounds));
      if (root.getItem().isEmpty()) {
         pLevel.destroyBlock(pPos, true);
      }

      root.tickCount++;
   }

   public void setFarmlandWaterManager(SimpleTicket<Vec3> ticket) {
      if (this.farmlandWaterManager != null) {
         this.farmlandWaterManager = ticket;
      }
   }

   public int getTickCount() {
      return this.tickCount;
   }
}
