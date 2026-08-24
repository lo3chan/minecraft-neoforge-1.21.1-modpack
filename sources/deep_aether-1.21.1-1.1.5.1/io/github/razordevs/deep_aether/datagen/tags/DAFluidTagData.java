package io.github.razordevs.deep_aether.datagen.tags;

import com.aetherteam.aether.AetherTags.Fluids;
import io.github.razordevs.deep_aether.init.DAFluids;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class DAFluidTagData extends FluidTagsProvider {
   public DAFluidTagData(PackOutput output, CompletableFuture<Provider> registries, @Nullable ExistingFileHelper helper) {
      super(output, registries, "deep_aether", helper);
   }

   @Nonnull
   public String getName() {
      return "Deep Aether Fluid Tags";
   }

   protected void addTags(Provider p_256380_) {
      this.tag(DATags.Fluids.POISON).add((Fluid)DAFluids.POISON_FLUID.get());
      this.tag(Fluids.ALLOWED_BUCKET_PICKUP).add((Fluid)DAFluids.POISON_FLUID.get());
   }
}
