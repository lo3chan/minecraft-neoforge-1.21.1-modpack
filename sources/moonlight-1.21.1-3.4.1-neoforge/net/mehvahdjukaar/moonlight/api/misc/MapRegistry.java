package net.mehvahdjukaar.moonlight.api.misc;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.core.IdMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class MapRegistry<T> implements IdMap<T>, Codec<T> {
   private final String name;
   private final BiMap<ResourceLocation, T> map = HashBiMap.create();
   private final Reference2IntMap<T> tToId;
   private final List<T> idToT;

   public MapRegistry(String name) {
      this.name = name;
      this.idToT = Lists.newArrayListWithExpectedSize(32);
      this.tToId = new Reference2IntOpenHashMap(32);
      this.tToId.defaultReturnValue(-1);
   }

   public static <B> CodecMapRegistry<B> ofCodec(String name) {
      return new CodecMapRegistry(name);
   }

   public static <B> CodecMapRegistry<B> ofCodec() {
      return new CodecMapRegistry("unnamed codec registry");
   }

   public <B extends T> T register(ResourceLocation name, B value) {
      if (this.map.containsKey(name)) {
         throw new IllegalStateException("Cannot register duplicate value " + name);
      } else {
         this.map.put(name, value);
         this.recomputeIdMappings();
         return (T)value;
      }
   }

   public <B extends T> T register(String name, B value) {
      this.register(ResourceLocation.parse(name), value);
      return (T)value;
   }

   protected void recomputeIdMappings() {
      this.tToId.clear();
      this.idToT.clear();
      List<ResourceLocation> orderedKeys = this.map.keySet().stream().sorted().toList();
      int id = 0;

      for (ResourceLocation k : orderedKeys) {
         T value = (T)this.map.get(k);
         if (value != null) {
            this.tToId.put(value, id);
            this.idToT.add(value);
            id++;
         }
      }
   }

   @Nullable
   public T getValue(ResourceLocation name) {
      return (T)this.map.get(name);
   }

   @Nullable
   public T getValue(String name) {
      return this.getValue(ResourceLocation.parse(name));
   }

   @Nullable
   public ResourceLocation getKey(T value) {
      return (ResourceLocation)this.map.inverse().get(value);
   }

   public Set<ResourceLocation> keySet() {
      return this.map.keySet();
   }

   public Set<T> getValues() {
      return this.map.values();
   }

   public T getValueOrDefault(ResourceLocation parse, T defaultType) {
      return (T)this.map.getOrDefault(parse, defaultType);
   }

   public Set<Entry<ResourceLocation, T>> getEntries() {
      return this.map.entrySet();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public int size() {
      return this.map.size();
   }

   public boolean containsKey(ResourceLocation name) {
      return this.map.containsKey(name);
   }

   public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> ops, U json) {
      return ResourceLocation.CODEC
         .decode(ops, json)
         .flatMap(
            pair -> {
               ResourceLocation id = (ResourceLocation)pair.getFirst();
               T value = this.getValue(id);
               return value == null
                  ? DataResult.error(() -> "Could not find any entry with key '" + id + "' in registry [" + this.name + "] \n Known keys: " + this.keySet())
                  : DataResult.success(Pair.of(value, pair.getSecond()));
            }
         );
   }

   public <U> DataResult<U> encode(T object, DynamicOps<U> ops, U prefix) {
      ResourceLocation id = this.getKey(object);
      return id == null
         ? DataResult.error(() -> "Could not find element " + object + " in registry" + this.name)
         : ops.mergeToPrimitive(prefix, ops.createString(id.toString()));
   }

   public void clear() {
      this.map.clear();
   }

   public <E> Codec<E> dispatch(Function<? super E, ? extends T> type) {
      return super.dispatch(type, c -> (MapCodec)c);
   }

   public int getId(T value) {
      return this.tToId.getInt(value);
   }

   @Nullable
   public final T byId(int id) {
      return id >= 0 && id < this.idToT.size() ? this.idToT.get(id) : null;
   }

   public Iterator<T> iterator() {
      return Iterators.filter(this.idToT.iterator(), Objects::nonNull);
   }

   public boolean contains(int id) {
      return this.byId(id) != null;
   }

   @Deprecated(
      forRemoval = true
   )
   public StreamCodec<ByteBuf, T> getStreamCodec() {
      return ByteBufCodecs.fromCodec(this);
   }
}
