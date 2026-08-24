package net.blay09.mods.balm.neoforge.provider;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import net.blay09.mods.balm.api.provider.BalmProviders;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class NeoForgeBalmProviders implements BalmProviders {
   private final Multimap<Class<?>, BiFunction<BlockEntity, Direction, ?>> fallbackBlockCapabilities = Multimaps.synchronizedListMultimap(
      ArrayListMultimap.create()
   );
   private final Map<Class<?>, BaseCapability<?, ?>> blockCapabilities = new ConcurrentHashMap<>();
   private final Map<Class<?>, ItemCapability<?, ?>> itemCapabilities = new ConcurrentHashMap<>();
   private final Map<Class<?>, EntityCapability<?, ?>> entityCapabilities = new ConcurrentHashMap<>();

   @Override
   public <T> T getProvider(BlockEntity blockEntity, Class<T> clazz) {
      return this.getProvider(blockEntity, null, clazz);
   }

   @Override
   public <T> T getProvider(BlockEntity blockEntity, @Nullable Direction direction, Class<T> clazz) {
      BlockCapability<T, Direction> capability = (BlockCapability<T, Direction>)this.getBlockCapability(clazz);
      T provider = (T)blockEntity.getLevel().getCapability(capability, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction);
      if (provider != null) {
         return provider;
      } else {
         for (BiFunction<BlockEntity, Direction, ?> fallback : this.fallbackBlockCapabilities.get(clazz)) {
            Object fallbackProvider = fallback.apply(blockEntity, direction);
            if (fallbackProvider != null) {
               return (T)fallbackProvider;
            }
         }

         return null;
      }
   }

   @Override
   public <T> T getProvider(Entity entity, Class<T> clazz) {
      EntityCapability<T, ?> capability = this.getEntityCapability(clazz);
      return (T)entity.getCapability(capability, null);
   }

   public <T> void registerBlockProvider(Class<T> clazz, BlockCapability<T, ?> capability) {
      this.blockCapabilities.put(clazz, capability);
   }

   public void registerFallbackBlockProvider(Class<?> clazz, BiFunction<BlockEntity, Direction, ?> fallback) {
      this.fallbackBlockCapabilities.put(clazz, fallback);
   }

   public <T> void registerItemProvider(Class<T> clazz, ItemCapability<T, ?> capability) {
      this.itemCapabilities.put(clazz, capability);
   }

   public <T> void registerEntityProvider(Class<T> clazz, EntityCapability<T, ?> capability) {
      this.entityCapabilities.put(clazz, capability);
   }

   public <T> BlockCapability<T, ?> getBlockCapability(Class<T> clazz) {
      return (BlockCapability<T, ?>)this.blockCapabilities.get(clazz);
   }

   public <T> ItemCapability<T, ?> getItemCapability(Class<T> clazz) {
      return (ItemCapability<T, ?>)this.itemCapabilities.get(clazz);
   }

   public <T> EntityCapability<T, ?> getEntityCapability(Class<T> clazz) {
      return (EntityCapability<T, ?>)this.entityCapabilities.get(clazz);
   }
}
