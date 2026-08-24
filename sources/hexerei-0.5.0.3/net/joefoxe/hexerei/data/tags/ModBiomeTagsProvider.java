package net.joefoxe.hexerei.data.tags;

import java.util.concurrent.CompletableFuture;
import net.joefoxe.hexerei.world.biome.ModBiomes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.neoforged.neoforge.common.Tags.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBiomeTagsProvider extends BiomeTagsProvider {
   public ModBiomeTagsProvider(PackOutput packOutput, CompletableFuture<Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(packOutput, pProvider, "hexerei", existingFileHelper);
   }

   protected void addTags(Provider pProvider) {
      this.tag(BiomeTags.IS_OVERWORLD).add(ModBiomes.WILLOW_SWAMP);
      this.tag(Biomes.IS_WET_OVERWORLD).add(ModBiomes.WILLOW_SWAMP);
      this.tag(Biomes.IS_SWAMP).add(ModBiomes.WILLOW_SWAMP);
      this.tag(BiomeTags.WATER_ON_MAP_OUTLINES).add(ModBiomes.WILLOW_SWAMP);
      this.tag(BiomeTags.HAS_SWAMP_HUT).add(ModBiomes.WILLOW_SWAMP);
      this.tag(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS).add(ModBiomes.WILLOW_SWAMP);
      this.tag(BiomeTags.HAS_CLOSER_WATER_FOG).add(ModBiomes.WILLOW_SWAMP);
   }
}
