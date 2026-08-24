package io.wispforest.owo.util;

import com.google.common.collect.ForwardingMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class TagInjector {
   @Internal
   public static final HashMap<TagInjector.TagLocation, Set<TagEntry>> ADDITIONS = new HashMap<>();
   private static final Map<TagInjector.TagLocation, Set<TagEntry>> ADDITIONS_VIEW = new ForwardingMap<TagInjector.TagLocation, Set<TagEntry>>() {
      @NotNull
      protected Map<TagInjector.TagLocation, Set<TagEntry>> delegate() {
         return Collections.unmodifiableMap(TagInjector.ADDITIONS);
      }

      public Set<TagEntry> get(@Nullable Object key) {
         return Collections.unmodifiableSet(this.delegate().get(key));
      }
   };

   private TagInjector() {
   }

   public static Map<TagInjector.TagLocation, Set<TagEntry>> getInjections() {
      return ADDITIONS_VIEW;
   }

   public static void injectRaw(
      Registry<?> registry, ResourceLocation tag, Function<ResourceLocation, TagEntry> entryMaker, Collection<ResourceLocation> values
   ) {
      ADDITIONS.computeIfAbsent(new TagInjector.TagLocation(Registries.tagsDirPath(registry.key()), tag), identifier -> new HashSet<>())
         .addAll(values.stream().map(entryMaker).toList());
   }

   public static void injectRaw(Registry<?> registry, ResourceLocation tag, Function<ResourceLocation, TagEntry> entryMaker, ResourceLocation... values) {
      injectRaw(registry, tag, entryMaker, Arrays.asList(values));
   }

   public static <T> void inject(Registry<T> registry, ResourceLocation tag, Collection<T> values) {
      injectDirectReference(registry, tag, values.stream().<ResourceLocation>map(registry::getKey).toList());
   }

   @SafeVarargs
   public static <T> void inject(Registry<T> registry, ResourceLocation tag, T... values) {
      inject(registry, tag, Arrays.asList(values));
   }

   public static void injectDirectReference(Registry<?> registry, ResourceLocation tag, Collection<ResourceLocation> values) {
      injectRaw(registry, tag, TagEntry::element, values);
   }

   public static void injectDirectReference(Registry<?> registry, ResourceLocation tag, ResourceLocation... values) {
      injectDirectReference(registry, tag, Arrays.asList(values));
   }

   public static void injectTagReference(Registry<?> registry, ResourceLocation tag, Collection<ResourceLocation> values) {
      injectRaw(registry, tag, TagEntry::tag, values);
   }

   public static void injectTagReference(Registry<?> registry, ResourceLocation tag, ResourceLocation... values) {
      injectTagReference(registry, tag, Arrays.asList(values));
   }

   public record TagLocation(String type, ResourceLocation tagId) {
   }
}
