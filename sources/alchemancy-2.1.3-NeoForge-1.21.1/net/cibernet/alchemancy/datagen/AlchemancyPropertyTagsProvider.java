package net.cibernet.alchemancy.datagen;

import java.util.concurrent.CompletableFuture;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class AlchemancyPropertyTagsProvider extends IntrinsicHolderTagsProvider<Property> {
   public AlchemancyPropertyTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, AlchemancyProperties.REGISTRY_KEY, lookupProvider, block -> block.asHolder().getKey(), "alchemancy", existingFileHelper);
   }

   protected void addTags(Provider provider) {
      Property[] properties = AlchemancyProperties.REGISTRY
         .getEntries()
         .stream()
         .filter(p -> !CodexEntryProvider.ENTRIES.containsKey(p) && !AlchemancyDatagenHandler.UNINFUSABLE_PROPERTIES.contains(p))
         .map(DeferredHolder::value)
         .toArray(Property[]::new);
      this.tag(TagKey.create(AlchemancyProperties.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath("alchemancy", "missing_codex_entries"))).add(properties);
   }
}
