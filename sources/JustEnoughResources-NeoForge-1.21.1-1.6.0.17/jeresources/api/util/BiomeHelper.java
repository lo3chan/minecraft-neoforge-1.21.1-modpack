package jeresources.api.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeHelper {
   public static List<Biome> getAllBiomes() {
      List<Biome> biomes = new ArrayList<>();
      VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME).listElements().map(Reference::value).forEach(biomes::add);
      return biomes;
   }

   public static Biome getBiome(ResourceKey<Biome> key) {
      return (Biome)VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME).getOrThrow(key).value();
   }

   public static List<Biome> getBiomes(ResourceKey<Biome> category) {
      List<Biome> biomes = new ArrayList<>();
      VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME).listElements().forEach(biome_entry -> {
         if (biome_entry.key().equals(category)) {
            biomes.add((Biome)biome_entry.value());
         }
      });
      return biomes;
   }
}
