package com.aetherteam.aether.data.generators.tags;

import com.aetherteam.aether.AetherTags;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AetherFluidTagData extends FluidTagsProvider {
   public AetherFluidTagData(PackOutput output, CompletableFuture<Provider> registries, @Nullable ExistingFileHelper helper) {
      super(output, registries, "aether", helper);
   }

   public void addTags(Provider provider) {
      this.tag(AetherTags.Fluids.ALLOWED_BUCKET_PICKUP).add(new Fluid[]{Fluids.WATER, Fluids.FLOWING_WATER});
   }
}
