package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.error.EmptyTagTargetException;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader.EntryWithSource;
import net.minecraft.util.ExtraCodecs.TagOrElementLocation;

public interface TagEventFilter {
   static TagEventFilter of(TagKubeEvent event, Object o) {
      if (o instanceof TagEventFilter f) {
         return f;
      } else if (o instanceof Collection<?> list) {
         List<TagEventFilter> filters = list.stream()
            .map(o1 -> of(event, o1))
            .flatMap(TagEventFilter::unwrap)
            .filter(fx -> fx != TagEventFilter.Empty.INSTANCE)
            .toList();
         return (TagEventFilter)(filters.isEmpty()
            ? TagEventFilter.Empty.INSTANCE
            : (filters.size() == 1 ? (TagEventFilter)filters.getFirst() : new TagEventFilter.Or(filters)));
      } else {
         Pattern regex = RegExpKJS.wrap(o);
         if (regex != null) {
            return new TagEventFilter.RegEx(regex);
         } else {
            String s = o.toString().trim();
            if (!s.isEmpty()) {
               return (TagEventFilter)(switch (s.charAt(0)) {
                  case '#' -> new TagEventFilter.Tag(event.get(ResourceLocation.parse(s.substring(1))));
                  case '@' -> new TagEventFilter.Namespace(s.substring(1));
                  default -> new TagEventFilter.ID(ResourceLocation.parse(s));
               });
            } else {
               return TagEventFilter.Empty.INSTANCE;
            }
         }
      }
   }

   static TagEventFilter unwrap(TagKubeEvent event, Object[] array) {
      return array.length == 1 ? of(event, array[0]) : of(event, Arrays.asList(array));
   }

   boolean testElementId(ResourceLocation id);

   default boolean testTagOrElementLocation(TagOrElementLocation element) {
      return !element.tag() && this.testElementId(element.id());
   }

   default Stream<TagEventFilter> unwrap() {
      return Stream.of(this);
   }

   default int add(TagWrapper wrapper) {
      int count = 0;

      for (ResourceLocation id : wrapper.event.getElementIds()) {
         if (this.testElementId(id)) {
            wrapper.entries.add(new EntryWithSource(TagEntry.element(id), "KubeJS Custom Tags"));
            count++;
         }
      }

      return count;
   }

   default int remove(TagWrapper wrapper) {
      int count = 0;
      Iterator<EntryWithSource> itr = wrapper.entries.iterator();

      while (itr.hasNext()) {
         EntryWithSource it = itr.next();
         if (!it.entry().tag && this.testElementId(it.entry().id)) {
            itr.remove();
            count++;
         }
      }

      return count;
   }

   public static class Empty implements TagEventFilter {
      public static final TagEventFilter.Empty INSTANCE = new TagEventFilter.Empty();

      @Override
      public boolean testElementId(ResourceLocation resourceLocation) {
         return false;
      }

      @Override
      public boolean testTagOrElementLocation(TagOrElementLocation element) {
         return false;
      }

      @Override
      public int add(TagWrapper wrapper) {
         return 0;
      }

      @Override
      public int remove(TagWrapper wrapper) {
         return 0;
      }
   }

   public record ID(ResourceLocation id) implements TagEventFilter {
      @Override
      public boolean testElementId(ResourceLocation id) {
         return this.id.equals(id);
      }

      @Override
      public int add(TagWrapper wrapper) {
         if (wrapper.event.getElementIds().contains(this.id)) {
            wrapper.entries.add(new EntryWithSource(TagEntry.element(this.id), "KubeJS Custom Tags"));
            return 1;
         } else {
            String msg = "No such element %s in registry %s".formatted(this.id, wrapper.event.registryKey.location());
            if (DevProperties.get().strictTags) {
               throw new EmptyTagTargetException(msg);
            } else {
               if (DevProperties.get().logSkippedTags) {
                  ConsoleJS.SERVER.warn(msg);
               }

               return 0;
            }
         }
      }
   }

   public record Namespace(String namespace) implements TagEventFilter {
      @Override
      public boolean testElementId(ResourceLocation id) {
         return id.getNamespace().equals(this.namespace);
      }
   }

   public record Or(List<TagEventFilter> filters) implements TagEventFilter {
      @Override
      public boolean testElementId(ResourceLocation resourceLocation) {
         for (TagEventFilter filter : this.filters) {
            if (filter.testElementId(resourceLocation)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean testTagOrElementLocation(TagOrElementLocation element) {
         for (TagEventFilter filter : this.filters) {
            if (filter.testTagOrElementLocation(element)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public Stream<TagEventFilter> unwrap() {
         return this.filters.stream();
      }

      @Override
      public int add(TagWrapper wrapper) {
         int count = 0;

         for (TagEventFilter filter : this.filters) {
            count += filter.add(wrapper);
         }

         return count;
      }

      @Override
      public int remove(TagWrapper wrapper) {
         int count = 0;

         for (TagEventFilter filter : this.filters) {
            count += filter.remove(wrapper);
         }

         return count;
      }
   }

   public record RegEx(Pattern pattern) implements TagEventFilter {
      @Override
      public boolean testElementId(ResourceLocation id) {
         return this.pattern.matcher(id.toString()).find();
      }
   }

   public record Tag(TagWrapper tag) implements TagEventFilter {
      @Override
      public boolean testElementId(ResourceLocation id) {
         return false;
      }

      @Override
      public boolean testTagOrElementLocation(TagOrElementLocation element) {
         return element.tag() && this.tag.id.equals(element.id());
      }

      @Override
      public int add(TagWrapper wrapper) {
         wrapper.entries.add(new EntryWithSource(TagEntry.tag(this.tag.id), "KubeJS Custom Tags"));
         return 1;
      }

      @Override
      public int remove(TagWrapper wrapper) {
         int count = 0;
         Iterator<EntryWithSource> itr = wrapper.entries.iterator();

         while (itr.hasNext()) {
            EntryWithSource it = itr.next();
            if (it.entry().tag && it.entry().id.equals(this.tag.id)) {
               itr.remove();
               count++;
            }
         }

         return count;
      }
   }
}
