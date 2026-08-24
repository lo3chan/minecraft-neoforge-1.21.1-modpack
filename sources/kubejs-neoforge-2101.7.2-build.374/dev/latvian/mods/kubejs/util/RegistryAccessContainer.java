package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonArray;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.RegistryWrapper;
import dev.latvian.mods.kubejs.recipe.CachedItemTagLookup;
import dev.latvian.mods.kubejs.recipe.CachedTagLookup;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.rhino.Context;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader.EntryWithSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class RegistryAccessContainer extends RegistryOpsContainer implements IContext {
   public static final RegistryAccessContainer BUILTIN = new RegistryAccessContainer(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
   @Internal
   public static RegistryAccessContainer current = BUILTIN;
   private final Frozen access;
   private DamageSources damageSources;
   private final Map<String, ItemStack> itemStackParseCache;
   public final Map<ResourceKey<?>, CachedTagLookup.Entry<?>> cachedRegistryTags;
   public CachedItemTagLookup cachedItemTags;
   public CachedTagLookup<Block> cachedBlockTags;
   public CachedTagLookup<Fluid> cachedFluidTags;
   private Map<ResourceLocation, RegistryWrapper> cachedRegistryWrappers;

   public static RegistryAccessContainer of(Context cx) {
      return cx instanceof KubeJSContext kcx ? kcx.getRegistries() : current;
   }

   public RegistryAccessContainer(Frozen access) {
      super(
         access.createSerializationContext(NbtOps.INSTANCE),
         access.createSerializationContext(JsonOps.INSTANCE),
         access.createSerializationContext(JavaOps.INSTANCE)
      );
      this.access = access;
      this.damageSources = null;
      this.itemStackParseCache = new HashMap<>();
      this.cachedRegistryTags = new Reference2ObjectOpenHashMap();
   }

   public Frozen access() {
      return this.access;
   }

   public DamageSources damageSources() {
      if (this.damageSources == null) {
         this.damageSources = new DamageSources(this.access);
      }

      return this.damageSources;
   }

   public Map<String, ItemStack> itemStackParseCache() {
      return this.itemStackParseCache;
   }

   public synchronized <T> void cacheTags(Registry<T> registry, Map<ResourceLocation, List<EntryWithSource>> map) {
      ResourceKey key1 = registry == null ? null : registry.key();
      if (key1 != null) {
         try {
            if (key1 == Registries.ITEM) {
               this.cachedItemTags = Cast.to(new CachedItemTagLookup(registry, map));
               this.cachedRegistryTags.put(key1, new CachedTagLookup.Entry<>(key1, registry, this.cachedItemTags));
            } else if (key1 == Registries.BLOCK) {
               this.cachedBlockTags = Cast.to(new CachedTagLookup(registry, map));
               this.cachedRegistryTags.put(key1, new CachedTagLookup.Entry<>(key1, registry, this.cachedBlockTags));
            } else if (key1 == Registries.FLUID) {
               this.cachedFluidTags = Cast.to(new CachedTagLookup(registry, map));
               this.cachedRegistryTags.put(key1, new CachedTagLookup.Entry<>(key1, registry, this.cachedFluidTags));
            } else {
               this.cachedRegistryTags.put(key1, new CachedTagLookup.Entry(key1, registry, new CachedTagLookup<>(registry, map)));
            }
         } catch (Exception var11) {
            ConsoleJS.SERVER.error("Error caching tags for " + key1, var11);
         }

         if (DataExport.export != null) {
            String loc = "tags/" + key1.location() + "/";

            for (Entry<ResourceLocation, List<EntryWithSource>> entry : map.entrySet()) {
               ArrayList<String> list = new ArrayList<>();

               for (EntryWithSource e : entry.getValue()) {
                  list.add(e.entry().toString());
               }

               list.sort(String.CASE_INSENSITIVE_ORDER);
               JsonArray arr = new JsonArray();

               for (String e : list) {
                  arr.add(e);
               }

               DataExport.export.addJson(loc + entry.getKey() + ".json", arr);
            }
         }
      }
   }

   private <T> RegistryWrapper<T> createRegistryWrapper(ResourceLocation id) {
      ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(id);
      return new RegistryWrapper<>(this.access.registryOrThrow(key), ResourceKey.create(key, ID.UNKNOWN));
   }

   public RegistryWrapper<?> wrapRegistry(ResourceLocation id) {
      if (this.cachedRegistryWrappers == null) {
         this.cachedRegistryWrappers = new HashMap<>();
      }

      return (RegistryWrapper<?>)this.cachedRegistryWrappers.computeIfAbsent(id, this::createRegistryWrapper);
   }

   public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> key) {
      CachedTagLookup.Entry<?> cached = this.cachedRegistryTags.get(key);
      return (Map<ResourceLocation, Collection<Holder<T>>>)(cached != null ? cached.lookup().tagMap() : Map.of());
   }
}
