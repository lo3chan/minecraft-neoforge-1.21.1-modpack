package io.github.razordevs.deep_aether.datagen.tags;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class DADamageTypeTags extends DamageTypeTagsProvider {
   public DADamageTypeTags(PackOutput output, CompletableFuture<Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, provider, "deep_aether", existingFileHelper);
   }

   @Nonnull
   public String getName() {
      return "Deep Aether Damage Type Tags";
   }

   protected void addTags(Provider provider) {
      this.tag(DATags.DamageTypes.EOTS_IMMUNE).add(DamageTypes.LIGHTNING_BOLT);
   }
}
