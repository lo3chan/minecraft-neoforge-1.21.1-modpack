package net.mehvahdjukaar.moonlight.core.set;

import com.google.common.base.Stopwatch;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.misc.MapRegistry;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.set.platform.BlockSetInternalImpl;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class BlockSetInternal {
   private static final Queue<Runnable> FINDER_ADDER = new ArrayDeque<>();
   private static final Queue<Runnable> REMOVER_ADDER = new ArrayDeque<>();
   private static final Map<Class<? extends BlockType>, BlockTypeRegistry<?>> REGISTRIES_BY_CLASS = new LinkedHashMap<>();
   private static final MapRegistry<BlockTypeRegistry<?>> REGISTRIES_BY_NAME = new MapRegistry<>("block_set_registry");
   private static final StreamCodec<ByteBuf, BlockTypeRegistry<?>> EXPLICIT_STREAM_CODEC = ByteBufCodecs.fromCodec(REGISTRIES_BY_NAME);
   private static final List<BlockTypeRegistry<?>> ORDERED_REGISTRIES = new ArrayList<>();

   public static void initializeBlockSets() {
      Stopwatch sw = Stopwatch.createStarted();
      if (hasFilledBlockSets()) {
         throw new UnsupportedOperationException("block sets have already bee initialized");
      } else {
         FINDER_ADDER.forEach(Runnable::run);
         FINDER_ADDER.clear();
         REMOVER_ADDER.forEach(Runnable::run);
         REMOVER_ADDER.clear();
         Collection<BlockTypeRegistry<?>> regs = getRegistries();
         regs.forEach(BlockTypeRegistry::buildAll);
         regs.forEach(BlockTypeRegistry::finalizeAndFreeze);
         Moonlight.LOGGER.info("Initialized block sets in {}ms", sw.elapsed().toMillis());
      }
   }

   public static synchronized <T extends BlockType> void registerBlockSetDefinition(BlockTypeRegistry<T> typeRegistry) {
      if (hasFilledBlockSets()) {
         throw new UnsupportedOperationException(String.format("Tried to register block set definition %s after registry events", typeRegistry));
      } else {
         REGISTRIES_BY_CLASS.put(typeRegistry.getType(), typeRegistry);
         REGISTRIES_BY_NAME.register(typeRegistry.typeName(), typeRegistry);
      }
   }

   @Deprecated
   public static synchronized <T extends BlockType> void addBlockTypeFinder(Class<T> type, BlockType.SetFinder<T> blockFinder) {
      if (hasFilledBlockSets()) {
         throw new UnsupportedOperationException(String.format("Tried to register block %s finder %s after registry events", type, blockFinder));
      } else {
         FINDER_ADDER.add(() -> {
            BlockTypeRegistry<T> container = getBlockSet(type);
            container.addFinder(blockFinder);
         });
      }
   }

   @Deprecated
   public static synchronized <T extends BlockType> void addBlockTypeRemover(Class<T> type, ResourceLocation id) {
      if (hasFilledBlockSets()) {
         throw new UnsupportedOperationException(String.format("Tried to remove block type %s for type %s after registry events", id, type));
      } else {
         REMOVER_ADDER.add(() -> {
            BlockTypeRegistry<T> container = getBlockSet(type);
            container.addRemover(id);
         });
      }
   }

   public static <T extends BlockType> BlockTypeRegistry<T> getBlockSet(Class<T> type) {
      return (BlockTypeRegistry<T>)REGISTRIES_BY_CLASS.get(type);
   }

   public static void addTranslations(AfterLanguageLoadEvent event) {
      BlockSetAPI.getRegistries().forEach(r -> r.addTypeTranslations(event));
   }

   public static Collection<BlockTypeRegistry<?>> getRegistries() {
      if (ORDERED_REGISTRIES.isEmpty()) {
         synchronized (ORDERED_REGISTRIES) {
            Comparator<BlockTypeRegistry<?>> comparator = Comparator.comparingInt(BlockTypeRegistry::priority);
            ORDERED_REGISTRIES.addAll(REGISTRIES_BY_NAME.getValues().stream().sorted(comparator.reversed()).toList());
         }
      }

      return ORDERED_REGISTRIES;
   }

   @Nullable
   public static <T extends BlockType> BlockTypeRegistry<T> getRegistry(Class<T> typeClass) {
      return (BlockTypeRegistry<T>)REGISTRIES_BY_CLASS.get(typeClass);
   }

   public static BlockTypeRegistry<?> getRegistry(String name) {
      return REGISTRIES_BY_NAME.getValue(name);
   }

   @Nullable
   public static <T extends BlockType> T getBlockTypeOf(ItemLike itemLike, Class<T> typeClass) {
      BlockTypeRegistry<T> r = getRegistry(typeClass);
      return r != null ? r.getBlockTypeOf(itemLike) : null;
   }

   public static Codec<BlockTypeRegistry<?>> getRegistriesCodec() {
      return REGISTRIES_BY_NAME;
   }

   public static StreamCodec<ByteBuf, BlockTypeRegistry<?>> getRegistriesStreamCodec() {
      return EXPLICIT_STREAM_CODEC;
   }

   protected static boolean hasFilledBlockSets() {
      return BlockSetInternalImpl.hasFilledBlockSets();
   }

   public static <T extends BlockType, E> void addDynamicRegistration(BlockSetAPI.BlockTypeRegistryCallback<E, T> var0, Class<T> var1, Registry<E> var2) {
      BlockSetInternalImpl.addDynamicRegistration(var0, var1, var2);
   }

   public static <T> void addDynamicRegistration(String var0, Consumer<Registrator<T>> var1, Registry<T> var2) {
      BlockSetInternalImpl.addDynamicRegistration(var0, var1, var2);
   }
}
