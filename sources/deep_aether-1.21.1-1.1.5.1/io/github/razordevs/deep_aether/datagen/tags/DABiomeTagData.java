package io.github.razordevs.deep_aether.datagen.tags;

import com.aetherteam.aether.AetherTags.Biomes;
import io.github.razordevs.deep_aether.world.biomes.DABiomes;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DABiomeTagData extends BiomeTagsProvider {
   public DABiomeTagData(PackOutput output, CompletableFuture<Provider> registries, @Nullable ExistingFileHelper helper) {
      super(output, registries, "deep_aether", helper);
   }

   public void addTags(Provider provider) {
      this.tag(Biomes.IS_AETHER)
         .add(
            new ResourceKey[]{
               DABiomes.AERLAVENDER_FIELDS,
               DABiomes.AERGLOW_FOREST,
               DABiomes.MYSTIC_AERGLOW_FOREST,
               DABiomes.BLUE_AERGLOW_FOREST,
               DABiomes.YAGROOT_SWAMP,
               DABiomes.GOLDEN_HEIGHTS,
               DABiomes.GOLDEN_GROVE,
               DABiomes.SACRED_LANDS,
               DABiomes.CLOUD,
               DABiomes.OVERGROWN_CLOUD,
               DABiomes.LUMINESCENT_FOREST
            }
         );
      this.tag(DATags.Biomes.IS_NOT_SWAMP)
         .add(
            new ResourceKey[]{
               DABiomes.AERLAVENDER_FIELDS,
               DABiomes.AERGLOW_FOREST,
               DABiomes.MYSTIC_AERGLOW_FOREST,
               DABiomes.BLUE_AERGLOW_FOREST,
               DABiomes.GOLDEN_HEIGHTS,
               DABiomes.GOLDEN_GROVE,
               DABiomes.SACRED_LANDS
            }
         );
      this.tag(DATags.Biomes.CAN_QUAIL_SPAWN)
         .add(
            new ResourceKey[]{
               DABiomes.AERLAVENDER_FIELDS,
               DABiomes.AERGLOW_FOREST,
               DABiomes.MYSTIC_AERGLOW_FOREST,
               DABiomes.BLUE_AERGLOW_FOREST,
               DABiomes.GOLDEN_HEIGHTS,
               DABiomes.GOLDEN_GROVE
            }
         );
      this.tag(DATags.Biomes.HAS_BRASS_DUNGEON).add(new ResourceKey[]{DABiomes.CLOUD, DABiomes.OVERGROWN_CLOUD});
      this.tag(DATags.Biomes.IS_CLOUD).add(new ResourceKey[]{DABiomes.OVERGROWN_CLOUD, DABiomes.CLOUD});
   }
}
