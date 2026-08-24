package net.cibernet.alchemancy.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PropertyEntryProvider implements DataProvider {
   private final CompletableFuture<Provider> registries;
   private final PathProvider pathProvider;
   private final String wikiLink;
   private final Collection<DeferredHolder<Property, ? extends Property>> entries;

   protected PropertyEntryProvider(PackOutput output, CompletableFuture<Provider> registries) {
      this(output, registries, "https://github.com/Cibernet83/Alchemancy/wiki/Infusion-Properties#", AlchemancyProperties.REGISTRY.getEntries());
   }

   protected PropertyEntryProvider(PackOutput output, CompletableFuture<Provider> registries, DeferredHolder<Property, ? extends Property>... entries) {
      this(output, registries, "https://github.com/Cibernet83/Alchemancy/wiki/Infusion-Properties#", List.of(entries));
   }

   public PropertyEntryProvider(
      PackOutput output, CompletableFuture<Provider> registries, String wikiLink, Collection<DeferredHolder<Property, ? extends Property>> entries
   ) {
      this.registries = registries;
      this.pathProvider = output.createPathProvider(Target.RESOURCE_PACK, "patchouli_books/alchemancer_journal/en_us/entries/properties");
      this.wikiLink = wikiLink;
      this.entries = entries.stream().filter(propertyDeferredHolder -> ((Property)propertyDeferredHolder.get()).hasJournalEntry()).toList();
   }

   public CompletableFuture<?> run(CachedOutput output) {
      return this.registries.thenCompose(lookup -> this.run(output, lookup));
   }

   protected CompletableFuture<?> run(CachedOutput output, Provider registries) {
      List<CompletableFuture<?>> list = new ArrayList<>();

      for (DeferredHolder<Property, ? extends Property> property : this.entries) {
         list.add(this.getEntryCompletable(output, property));
      }

      return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
   }

   public CompletableFuture<?> getEntryCompletable(CachedOutput output, Holder<Property> property) {
      ResourceLocation id = ((Property)property.value()).getKey();
      JsonObject jsonElement = new JsonObject();
      jsonElement.addProperty("name", ((Property)property.value()).getLanguageKey());
      jsonElement.addProperty("icon", "alchemancy:property_capsule[alchemancy:stored_properties=[\"" + id + "\"], alchemancy:ingredient_display={}]");
      jsonElement.addProperty("advancement", id.getNamespace() + ":discovery/" + id.getPath());
      jsonElement.addProperty("category", "alchemancy:properties");
      JsonArray pages = new JsonArray(2);
      JsonObject spotlightPage = new JsonObject();
      spotlightPage.addProperty("type", "patchouli:spotlight");
      spotlightPage.addProperty("title", ((Property)property.value()).getLanguageKey());
      spotlightPage.addProperty("item", "alchemancy:property_capsule[alchemancy:stored_properties=[\"" + id + "\"], alchemancy:ingredient_display={}]");
      spotlightPage.addProperty("text", "alchemancy.entry.infusion_property.missing");
      pages.add(spotlightPage);
      JsonObject wikiPage = new JsonObject();
      wikiPage.addProperty("type", "patchouli:link");
      wikiPage.addProperty("url", this.wikiLink + id.getPath().replace("_", "-"));
      wikiPage.addProperty("link_text", "alchemancy.entry.infusion_property.wiki_button");
      wikiPage.addProperty("text", "alchemancy.entry.infusion_property.wiki");
      pages.add(wikiPage);
      jsonElement.add("pages", pages);
      return DataProvider.saveStable(
         output, jsonElement, this.pathProvider.json(ResourceLocation.fromNamespaceAndPath("alchemancy", id.getNamespace() + "/" + id.getPath()))
      );
   }

   public String getName() {
      return "Infusion Property Journal Entries";
   }
}
