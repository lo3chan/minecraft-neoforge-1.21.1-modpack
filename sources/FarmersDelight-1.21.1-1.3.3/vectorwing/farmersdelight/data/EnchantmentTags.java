package vectorwing.farmersdelight.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EnchantmentTags extends EnchantmentTagsProvider {
   public EnchantmentTags(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "farmersdelight", existingFileHelper);
   }

   protected void addTags(Provider provider) {
      this.tag(net.minecraft.tags.EnchantmentTags.NON_TREASURE).add(ModEnchantments.BACKSTABBING);
   }
}
