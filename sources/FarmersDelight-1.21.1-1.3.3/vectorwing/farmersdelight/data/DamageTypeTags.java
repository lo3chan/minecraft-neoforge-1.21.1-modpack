package vectorwing.farmersdelight.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.registry.ModDamageTypes;

public class DamageTypeTags extends TagsProvider<DamageType> {
   public DamageTypeTags(PackOutput output, CompletableFuture<Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, Registries.DAMAGE_TYPE, lookupProvider, modId, existingFileHelper);
   }

   protected void addTags(Provider provider) {
      this.tag(net.minecraft.tags.DamageTypeTags.IS_FIRE).add(ModDamageTypes.STOVE_BURN);
      this.tag(net.minecraft.tags.DamageTypeTags.NO_KNOCKBACK).add(ModDamageTypes.STOVE_BURN);
      this.tag(net.minecraft.tags.DamageTypeTags.BURN_FROM_STEPPING).add(ModDamageTypes.STOVE_BURN);
      this.tag(net.minecraft.tags.DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES).add(ModDamageTypes.STOVE_BURN);
   }
}
