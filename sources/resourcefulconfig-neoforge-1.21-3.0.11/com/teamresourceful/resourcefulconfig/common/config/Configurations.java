package com.teamresourceful.resourcefulconfig.common.config;

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public record Configurations(Map<String, Set<String>> modToConfigs, Map<String, ResourcefulConfig> configs) implements Iterable<ResourcefulConfig> {
   public static final Configurations INSTANCE = new Configurations();

   private Configurations() {
      this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
   }

   private void addModConfigs(String modid, String configId) {
      this.modToConfigs.computeIfAbsent(modid, s -> new HashSet<>()).add(configId);
   }

   public void addConfig(ResourcefulConfig config, String modid) {
      if (modid != null) {
         this.addModConfigs(modid, config.id());
      }

      this.configs.put(config.id(), config);
   }

   @NotNull
   @Override
   public Iterator<ResourcefulConfig> iterator() {
      return this.configs.values().iterator();
   }

   public ResourcefulConfig getConfig(String id) {
      return this.configs.get(id);
   }

   public Set<String> getConfigsForMod(String modid) {
      return modid == null ? Set.of() : this.modToConfigs.getOrDefault(modid, Set.of());
   }

   public Collection<String> getModIds() {
      return this.modToConfigs.keySet();
   }
}
