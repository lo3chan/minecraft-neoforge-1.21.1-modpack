package net.mehvahdjukaar.moonlight.core.set.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.core.set.BlockSetInternal;
import net.mehvahdjukaar.moonlight.platform.MoonlightForge;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSetInternalImpl {
   private static final Map<String, Map<ResourceKey<? extends Registry<?>>, List<Runnable>>> LATE_REGISTRATION_QUEUE = new ConcurrentHashMap<>();
   private static boolean hasFilledBlockSets = false;

   public static <T> void addDynamicRegistration(String modId, Consumer<Registrator<T>> registrationFunction, Registry<T> registry) {
      IEventBus bus = ((ModContainer)ModList.get()
            .getModContainerById(modId)
            .orElseThrow(() -> new IllegalStateException("Could not find mod container for mod " + modId + ". How in the world is this possible?")))
         .getEventBus();
      if (registry == BuiltInRegistries.BLOCK) {
         addEvent(bus, modId, BuiltInRegistries.BLOCK, registrationFunction);
      } else if (registry == BuiltInRegistries.ITEM) {
         addEvent(bus, modId, BuiltInRegistries.ITEM, registrationFunction);
      } else {
         if (registry == BuiltInRegistries.FLUID || registry == BuiltInRegistries.SOUND_EVENT) {
            throw new IllegalArgumentException("Fluid and Sound Events registry not supported here");
         }

         getOrAddQueue(bus, modId, Registries.POTION);
         RegHelper.registerInBatch(registry, registrationFunction);
      }
   }

   @Deprecated
   public static <T extends BlockType, E> void addDynamicRegistration(
      BlockSetAPI.BlockTypeRegistryCallback<E, T> registrationFunction, Class<T> blockType, Registry<E> registry
   ) {
      IEventBus bus = MoonlightForge.getCurrentBus();
      String modId = ModLoadingContext.get().getActiveContainer().getModId();
      if (registry == BuiltInRegistries.BLOCK) {
         addEvent(bus, modId, BuiltInRegistries.BLOCK, registrationFunction, blockType);
      } else if (registry == BuiltInRegistries.ITEM) {
         addEvent(bus, modId, BuiltInRegistries.ITEM, registrationFunction, blockType);
      } else {
         if (registry == BuiltInRegistries.FLUID || registry == BuiltInRegistries.SOUND_EVENT) {
            throw new IllegalArgumentException("Fluid and Sound Events registry not supported here");
         }

         getOrAddQueue(bus, modId, Registries.POTION);
         RegHelper.registerInBatch(registry, e -> registrationFunction.accept(e, BlockSetAPI.getBlockSet(blockType).getValues()));
      }
   }

   private static <E> void addEvent(IEventBus bus, String modId, Registry<E> reg, Consumer<Registrator<E>> registrationFunction) {
      List<Runnable> registrationQueues = getOrAddQueue(bus, modId, reg.key());
      Runnable lateRegistration = () -> registrationFunction.accept((r, o) -> Registry.register(reg, r, o));
      registrationQueues.add(lateRegistration);
   }

   @Deprecated
   private static <T extends BlockType, E> void addEvent(
      IEventBus bus, String modId, Registry<E> reg, BlockSetAPI.BlockTypeRegistryCallback<E, T> registrationFunction, Class<T> blockType
   ) {
      List<Runnable> registrationQueues = getOrAddQueue(bus, modId, reg.key());
      Runnable lateRegistration = () -> registrationFunction.accept((r, o) -> Registry.register(reg, r, o), BlockSetAPI.getBlockSet(blockType).getValues());
      registrationQueues.add(lateRegistration);
   }

   @NotNull
   private static List<Runnable> getOrAddQueue(IEventBus bus, String modId, @Nullable ResourceKey<? extends Registry<?>> regKey) {
      return LATE_REGISTRATION_QUEUE.computeIfAbsent(modId, m -> {
         Map<ResourceKey<? extends Registry<?>>, List<Runnable>> map = new HashMap<>();
         Consumer<RegisterEvent> eventConsumer = r -> registerLateBlockAndItems(r, map);
         bus.addListener(EventPriority.HIGHEST, eventConsumer);
         return map;
      }).computeIfAbsent(regKey, c -> new ArrayList<>());
   }

   protected static void registerLateBlockAndItems(RegisterEvent event, Map<ResourceKey<? extends Registry<?>>, List<Runnable>> toRun) {
      if (event.getRegistryKey().equals(Registries.POTION)) {
         if (!hasFilledBlockSets) {
            BlockSetInternal.initializeBlockSets();
            hasFilledBlockSets = true;
         }

         List<Runnable> blockQueue = toRun.remove(Registries.BLOCK);
         if (blockQueue != null) {
            blockQueue.forEach(Runnable::run);
         }

         List<Runnable> itemQueue = toRun.remove(Registries.ITEM);
         if (itemQueue != null) {
            itemQueue.forEach(Runnable::run);
         }

         for (Entry<ResourceKey<? extends Registry<?>>, List<Runnable>> e : toRun.entrySet()) {
            e.getValue().forEach(Runnable::run);
         }

         toRun.clear();
      }
   }

   public static boolean hasFilledBlockSets() {
      return hasFilledBlockSets;
   }
}
