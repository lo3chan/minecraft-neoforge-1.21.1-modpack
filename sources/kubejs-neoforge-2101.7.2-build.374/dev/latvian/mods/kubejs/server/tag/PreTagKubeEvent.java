package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PreTagKubeEvent extends TagKubeEvent {
   public final Map<ResourceLocation, PreTagWrapper> tags = new ConcurrentHashMap<>();
   public final List<Consumer<TagKubeEvent>> actions = new ArrayList<>();
   public boolean invalid;

   public static void handle(Map<ResourceKey<?>, PreTagKubeEvent> tagEventHolders) {
      tagEventHolders.clear();
      if (ServerEvents.TAGS.hasListeners()) {
         for (ResourceKey<Registry<?>> id : ServerEvents.TAGS.findUniqueExtraIds(ScriptType.SERVER)) {
            PreTagKubeEvent e = new PreTagKubeEvent(id);

            try {
               ServerEvents.TAGS.post(ScriptType.SERVER, id, e);
            } catch (Exception var5) {
               e.invalid = true;
               if (DevProperties.get().logEventErrorStackTrace) {
                  KubeJS.LOGGER.warn("Pre Tag event for {} failed:", e.registryKey.location());
                  var5.printStackTrace();
               }
            }

            if (!e.invalid) {
               tagEventHolders.put(e.registryKey, e);
            }
         }
      }
   }

   public PreTagKubeEvent(ResourceKey<?> registryKey) {
      super(registryKey, null);
   }

   @Override
   protected TagWrapper createTagWrapper(ResourceLocation id) {
      return new PreTagWrapper(this, id);
   }

   @Override
   public void removeAllTagsFrom(Object... ignored) {
      this.actions.add(new PreTagKubeEvent.RemoveAllTagsFromAction(ignored));
   }

   @Override
   public Set<ResourceLocation> getElementIds() {
      return Set.of();
   }

   public record RemoveAllTagsFromAction(Object[] ignored) implements Consumer<TagKubeEvent> {
      public void accept(TagKubeEvent e) {
         e.removeAllTagsFrom(this.ignored);
      }
   }
}
