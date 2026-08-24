package vazkii.psi.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.lib.LibResources;

public class PsiDamageTypeTagsProvider extends TagsProvider<DamageType> {
   public PsiDamageTypeTagsProvider(PackOutput pOutput, CompletableFuture<Provider> pLookupProvider, ExistingFileHelper existingFileHelper) {
      super(pOutput, Registries.DAMAGE_TYPE, pLookupProvider, "psi", existingFileHelper);
   }

   protected void addTags(Provider pProvider) {
      this.tag(DamageTypeTags.BYPASSES_ARMOR).add(LibResources.PSI_OVERLOAD);
      this.tag(DamageTypeTags.BYPASSES_SHIELD).add(LibResources.PSI_OVERLOAD);
      this.tag(DamageTypeTags.BYPASSES_RESISTANCE).add(LibResources.PSI_OVERLOAD);
      this.tag(DamageTypeTags.BYPASSES_EFFECTS).add(LibResources.PSI_OVERLOAD);
      this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(LibResources.PSI_OVERLOAD);
   }

   @NotNull
   public String getName() {
      return "Psi damage type tags";
   }
}
