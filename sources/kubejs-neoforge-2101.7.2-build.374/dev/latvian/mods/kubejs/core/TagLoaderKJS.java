package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.server.tag.PreTagKubeEvent;
import dev.latvian.mods.kubejs.server.tag.TagEventFilter;
import dev.latvian.mods.kubejs.server.tag.TagKubeEvent;
import dev.latvian.mods.kubejs.server.tag.TagWrapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader.EntryWithSource;
import org.jetbrains.annotations.Nullable;

public interface TagLoaderKJS<T> {
   default void kjs$customTags(ReloadableServerResourcesKJS kjs$resources, Map<ResourceLocation, List<EntryWithSource>> map) {
      Registry<T> reg = this.kjs$getRegistry();
      if (reg != null) {
         RegistryObjectStorage objStorage = RegistryObjectStorage.of(reg.key());
         boolean hasDefaultTags = false;

         for (BuilderBase<?> builder : objStorage.objects.values()) {
            if (!builder.defaultTags.isEmpty()) {
               hasDefaultTags = true;
               break;
            }
         }

         ServerScriptManager ssm = this.kjs$getResources().kjs$getServerScriptManager();
         hasDefaultTags |= !ssm.serverRegistryTags.isEmpty();
         if (hasDefaultTags || ServerEvents.TAGS.hasListeners(objStorage.key)) {
            PreTagKubeEvent preEvent = ssm.preTagEvents.get(reg.key());
            TagKubeEvent event = new TagKubeEvent(objStorage.key, reg);

            for (Entry<ResourceLocation, List<EntryWithSource>> entry : map.entrySet()) {
               TagWrapper w = new TagWrapper(event, entry.getKey(), entry.getValue());
               event.tags.put(w.id, w);
               if (ConsoleJS.SERVER.shouldPrintDebug()) {
                  ConsoleJS.SERVER.debug("Tags %s/#%s; %d".formatted(objStorage, w.id, w.entries.size()));
               }
            }

            for (BuilderBase<?> builderx : objStorage.objects.values()) {
               for (ResourceLocation s : builderx.defaultTags) {
                  event.add(s, new TagEventFilter.ID(builderx.id));
               }
            }

            for (Entry<ResourceLocation, Set<ResourceLocation>> e : ssm.serverRegistryTags.entrySet()) {
               for (ResourceLocation tag : e.getValue()) {
                  event.add(tag, new TagEventFilter.ID(e.getKey()));
               }
            }

            if (preEvent == null) {
               ServerEvents.TAGS.post(event, objStorage.key);
            } else {
               for (Consumer<TagKubeEvent> a : preEvent.actions) {
                  a.accept(event);
               }
            }

            map.clear();

            for (Entry<ResourceLocation, TagWrapper> entryx : event.tags.entrySet()) {
               map.put(entryx.getKey(), entryx.getValue().entries);
            }

            if (event.totalAdded > 0 || event.totalRemoved > 0 || ConsoleJS.SERVER.shouldPrintDebug()) {
               ConsoleJS.SERVER
                  .info(
                     "[%s] Found %d tags, added %d objects, removed %d objects".formatted(objStorage, event.tags.size(), event.totalAdded, event.totalRemoved)
                  );
            }
         }

         ssm.getRegistries().cacheTags(reg, map);
      }
   }

   void kjs$init(ReloadableServerResourcesKJS resources, Registry<T> registry);

   ReloadableServerResourcesKJS kjs$getResources();

   @Nullable
   Registry<T> kjs$getRegistry();
}
