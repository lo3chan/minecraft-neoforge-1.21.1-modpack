package net.mehvahdjukaar.moonlight.api.set;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.misc.MapRegistry;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.INamedSupplier;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.CompatHandler;
import net.mehvahdjukaar.moonlight.core.integration.PolymerCompat;
import net.mehvahdjukaar.moonlight.core.set.BlockSetInternal;
import net.minecraft.core.IdMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class BlockTypeRegistry<T extends BlockType> implements IdMap<T> {
   protected boolean frozen = false;
   private final String name;
   private final List<BlockType.SetFinder<T>> finders = new ArrayList<>();
   private final Set<ResourceLocation> notInclude = new HashSet<>();
   protected final MapRegistry<T> valuesReg;
   private final Class<T> typeClass;
   private final Object2ObjectOpenHashMap<Object, T> childrenToType = new Object2ObjectOpenHashMap();
   private final StreamCodec<ByteBuf, T> streamCodecSlow;
   @Deprecated(
      forRemoval = true
   )
   boolean isBeingFrozenHack = false;

   public static Codec<BlockTypeRegistry<?>> getRegistryCodec() {
      return BlockSetInternal.getRegistriesCodec();
   }

   public static StreamCodec<ByteBuf, BlockTypeRegistry<?>> getRegistryStreamCodec() {
      return BlockSetInternal.getRegistriesStreamCodec();
   }

   protected BlockTypeRegistry(Class<T> typeClass, String name) {
      this.typeClass = typeClass;
      this.name = name;
      this.valuesReg = new MapRegistry<>(name);
      this.streamCodecSlow = ByteBufCodecs.fromCodec(this.getCodec());
   }

   @NotNull
   public Iterator<T> iterator() {
      return this.valuesReg.iterator();
   }

   public int size() {
      return this.valuesReg.size();
   }

   @Nullable
   public T byId(int id) {
      return this.valuesReg.byId(id);
   }

   public int getId(T value) {
      return this.valuesReg.getId(value);
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public Class<T> getType() {
      return this.typeClass;
   }

   @Deprecated(
      forRemoval = true
   )
   public T getFromNBT(String name) {
      return this.valuesReg.getValueOrDefault(ResourceLocation.parse(name), this.getDefaultType());
   }

   @Nullable
   public T get(ResourceLocation res) {
      if (this.frozen || this.isBeingFrozenHack && !PlatHelper.isDev()) {
         return this.valuesReg.getValue(res);
      } else {
         throw new AssertionError("Tried to get an object from block set registry before the registry was finalized.");
      }
   }

   public T getOrDefault(ResourceLocation res) {
      if (this.frozen || this.isBeingFrozenHack && !PlatHelper.isDev()) {
         return this.valuesReg.getValueOrDefault(res, this.getDefaultType());
      } else {
         throw new AssertionError("Tried to get an object from block set registry before the registry was finalized.");
      }
   }

   public ResourceLocation getKey(T input) {
      return this.valuesReg.getKey(input);
   }

   public Codec<T> getCodec() {
      return this.valuesReg;
   }

   @Deprecated(
      forRemoval = true
   )
   public StreamCodec<ByteBuf, T> getStreamCodec() {
      return this.streamCodecSlow;
   }

   public StreamCodec<ByteBuf, T> getStreamCodecExplicit() {
      return this.streamCodecSlow;
   }

   public abstract T getDefaultType();

   public Collection<T> getValues() {
      return this.valuesReg.getValues();
   }

   public String typeName() {
      return this.name;
   }

   protected abstract Optional<T> detectTypeFromBlock(Block var1, ResourceLocation var2);

   protected T register(T newType) {
      if (this.frozen) {
         throw new UnsupportedOperationException("Tried to register a block types after registry events");
      } else {
         if (!this.valuesReg.containsKey(newType.id)) {
            this.valuesReg.register(newType.id, newType);
         }

         return newType;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public Collection<BlockType.SetFinder<T>> getFinders() {
      return List.of();
   }

   public synchronized void addFinder(BlockType.SetFinder<T> finder) {
      if (this.frozen) {
         throw new UnsupportedOperationException("Tried to register a block type finder after registry events");
      } else {
         this.finders.add(finder);
      }
   }

   public synchronized void addRemover(ResourceLocation id) {
      if (this.frozen) {
         throw new UnsupportedOperationException("Tried remove a block type after registry events");
      } else {
         this.notInclude.add(id);
      }
   }

   @Internal
   public void finalizeAndFreeze() {
      if (this.frozen) {
         throw new UnsupportedOperationException("Block types are already finalized");
      } else {
         this.frozen = true;
         this.getValues().forEach(BlockType::initializeChildrenBlocks);
         this.getValues().forEach(BlockType::initializeChildrenItems);
      }
   }

   @Internal
   public void buildAll() {
      if (!this.frozen) {
         this.isBeingFrozenHack = true;
         T defaultType = this.getDefaultType();
         if (defaultType != null) {
            this.register(defaultType);
         }

         this.finders.stream().map(BlockType.SetFinder::get).forEach(f -> f.ifPresent(this::register));

         for (Block b : BuiltInRegistries.BLOCK) {
            if (!CompatHandler.POLYMER || !PolymerCompat.isPolymerObj(b)) {
               this.detectTypeFromBlock(b, Utils.getID(b)).ifPresent(t -> {
                  if (!this.notInclude.contains(t.getId())) {
                     this.register((T)t);
                  }
               });
            }
         }

         this.isBeingFrozenHack = false;
         this.finders.clear();
         this.notInclude.clear();
      }
   }

   @Internal
   public void addTypeTranslations(AfterLanguageLoadEvent language) {
      this.getValues().forEach(blockType -> {
         if (language.isDefault()) {
            language.addEntry(blockType.getTranslationKey(), blockType.getReadableName());
         }
      });
   }

   @Nullable
   public T getBlockTypeOf(ItemLike itemLike) {
      T blockType = (T)this.childrenToType.get(itemLike);
      if (blockType != null) {
         return blockType;
      } else if (itemLike != Items.AIR && itemLike != Blocks.AIR) {
         if (itemLike instanceof BlockItem bi) {
            T ofBlock = (T)this.childrenToType.get(bi.getBlock());
            if (ofBlock != null) {
               return ofBlock;
            }
         }

         if (itemLike instanceof Block block) {
            Item item = block.asItem();
            if (item == Items.AIR) {
               throw new IllegalStateException("Block " + block + " has no item. This likely means getBlockTypeOf was called too early. This is a bug");
            } else {
               return (T)this.childrenToType.get(item);
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   protected void mapObjectToType(Object itemLike, BlockType type) {
      this.childrenToType.put(itemLike, type);
      if (itemLike instanceof BlockItem bi && !this.childrenToType.containsKey(bi.getBlock())) {
         this.childrenToType.put(bi.getBlock(), type);
      }
   }

   public int priority() {
      return 100;
   }

   public INamedSupplier<T> makeFutureHolder(ResourceLocation id) {
      return INamedSupplier.memoize(id, () -> this.get(id));
   }
}
