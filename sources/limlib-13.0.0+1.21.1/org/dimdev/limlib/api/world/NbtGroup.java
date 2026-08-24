package org.dimdev.limlib.api.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class NbtGroup {
   public static final Codec<NbtGroup> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").stable().forGetter(group -> group.id),
            Codec.unboundedMap(Codec.STRING, Codec.list(Codec.STRING)).fieldOf("groups").stable().forGetter(group -> group.groups)
         )
         .apply(instance, instance.stable(NbtGroup::new))
   );
   ResourceLocation id;
   Map<String, List<String>> groups;

   public NbtGroup(ResourceLocation id, Map<String, List<String>> groups) {
      this.id = id;
      this.groups = groups;
   }

   public ResourceLocation nbtId(String group, String nbt) {
      return ResourceLocation.fromNamespaceAndPath(this.id.getNamespace(), "structures/nbt/" + this.id.getPath() + "/" + group + "/" + nbt + ".nbt");
   }

   public ResourceLocation pick(String key, RandomSource random) {
      if (!this.groups.containsKey(key)) {
         throw new NullPointerException();
      } else {
         List<String> group = this.groups.get(key);
         return this.nbtId(key, group.get(random.nextInt(group.size())));
      }
   }

   public String chooseGroup(RandomSource random, String... keys) {
      int[] sizes = new int[keys.length];

      for (int i = 0; i < keys.length; i++) {
         int extra = 0;
         if (i > 0) {
            extra = sizes[i - 1];
         }

         sizes[i] = extra + this.groups.get(keys[i]).size();
      }

      int g = random.nextInt(sizes[keys.length - 1]);

      for (int i = 0; i < keys.length; i++) {
         if (g < sizes[i]) {
            return keys[i];
         }
      }

      throw new UnsupportedOperationException("Failed to retrieve key");
   }

   public boolean contains(String key, String nbt) {
      if (!this.groups.containsKey(key)) {
         return false;
      } else {
         List<String> group = this.groups.get(key);
         return group.contains(nbt);
      }
   }

   public boolean contains(String key) {
      return this.groups.containsKey(key);
   }

   public void forEach(Consumer<ResourceLocation> runnable) {
      for (Entry<String, List<String>> entry : this.groups.entrySet()) {
         for (String nbt : entry.getValue()) {
            runnable.accept(this.nbtId(entry.getKey(), nbt));
         }
      }
   }

   public <A, V> void fill(FunctionMap<ResourceLocation, A, V> map) {
      this.forEach(map::put);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public Map<String, List<String>> getGroups() {
      return this.groups;
   }

   public static class Builder {
      ResourceLocation id;
      Map<String, List<String>> groups = Maps.newHashMap();

      public static NbtGroup.Builder create(ResourceLocation id) {
         NbtGroup.Builder builder = new NbtGroup.Builder();
         builder.id = id;
         return builder;
      }

      public NbtGroup.Builder with(String group, String base, int from, int to) {
         List<String> list = Lists.newArrayList();

         for (int i = from; i <= to; i++) {
            list.add(base + "_" + i);
         }

         this.groups.put(group, list);
         return this;
      }

      public NbtGroup.Builder with(String group, String... base) {
         this.groups.put(group, List.of(base));
         return this;
      }

      public NbtGroup.Builder with(String base, int from, int to) {
         return this.with(base, base, from, to);
      }

      public NbtGroup.Builder with(String base) {
         return this.with(base, base);
      }

      public NbtGroup build() {
         return new NbtGroup(this.id, this.groups);
      }
   }
}
