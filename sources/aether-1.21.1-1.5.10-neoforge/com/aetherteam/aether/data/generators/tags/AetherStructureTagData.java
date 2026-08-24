package com.aetherteam.aether.data.generators.tags;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.data.resources.registries.AetherStructures;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AetherStructureTagData extends StructureTagsProvider {
   public AetherStructureTagData(PackOutput output, CompletableFuture<Provider> registries, @Nullable ExistingFileHelper helper) {
      super(output, registries, "aether", helper);
   }

   public void addTags(Provider provider) {
      this.tag(AetherTags.Structures.DUNGEONS)
         .add(new ResourceKey[]{AetherStructures.BRONZE_DUNGEON, AetherStructures.SILVER_DUNGEON, AetherStructures.GOLD_DUNGEON});
   }
}
