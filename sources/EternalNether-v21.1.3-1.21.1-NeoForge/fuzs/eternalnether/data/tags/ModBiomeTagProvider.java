package fuzs.eternalnether.data.tags;

import fuzs.eternalnether.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class ModBiomeTagProvider extends AbstractTagProvider<Biome> {
   public ModBiomeTagProvider(DataProviderContext context) {
      super(Registries.BIOME, context);
   }

   public void addTags(Provider provider) {
      this.add(ModRegistry.HAS_CATACOMB_BIOME_TAG_KEY)
         .add(Biomes.SOUL_SAND_VALLEY)
         .addOptionalTag(new String[]{"biomesoplenty:withered_abyss", "incendium:weeping_valley", "incendium:withered_forest", "byg:wailing_garth"});
      this.add(ModRegistry.HAS_CITADEL_BIOME_TAG_KEY)
         .add(Biomes.WARPED_FOREST)
         .addOptionalTag(new String[]{"incendium:inverted_forest", "byg:glowstone_gardens"});
      this.add(ModRegistry.HAS_PIGLIN_MANOR_BIOME_TAG_KEY)
         .add(Biomes.CRIMSON_FOREST)
         .addOptionalTag(new String[]{"incendium:ash_barrens", "byg:crimson_gardens"});
   }
}
