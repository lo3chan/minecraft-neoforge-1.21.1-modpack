package dev.worldgen.lithostitched.api.event;

import dev.worldgen.lithostitched.impl.event.LithostitchedEvent;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public interface AddRegionsEvent {
   LithostitchedEvent<AddRegionsEvent> EVENT = new LithostitchedEvent<>(callbacks -> (registries, consumer) -> {
      for (AddRegionsEvent callback : callbacks) {
         callback.addRegions(registries, consumer);
      }
   });

   void addRegions(RegistryAccess var1, AddRegionsEvent.RegionConsumer var2);

   public interface RegionConsumer {
      void accept(ResourceKey<Region> var1, ResourceKey<Level> var2, HolderSet<Biome> var3, int var4);
   }
}
