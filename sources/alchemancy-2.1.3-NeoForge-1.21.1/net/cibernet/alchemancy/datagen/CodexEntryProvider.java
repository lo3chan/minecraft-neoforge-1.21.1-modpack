package net.cibernet.alchemancy.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CodexEntryProvider implements DataProvider {
   public static final HashMap<Holder<Property>, CodexEntryReloadListenener.CodexEntry> ENTRIES = new HashMap<>();
   private final CompletableFuture<Provider> registries;
   private final PathProvider pathProvider;

   public CodexEntryProvider(CompletableFuture<Provider> registries, PackOutput packOutput) {
      this.registries = registries;
      this.pathProvider = packOutput.createPathProvider(Target.RESOURCE_PACK, "alchemancy/codex_entries");
   }

   public void populate() {
      for (Item item : BuiltInRegistries.ITEM) {
         if (item.components().has((DataComponentType)AlchemancyItems.Components.INNATE_PROPERTIES.get())) {
            ((InfusedPropertiesComponent)item.components().get((DataComponentType)AlchemancyItems.Components.INNATE_PROPERTIES.get()))
               .forEachProperty(propertyHolder -> addInnateItem(propertyHolder, BuiltInRegistries.ITEM.wrapAsHolder(item)));
         }
      }

      addRelatedProperties(AlchemancyProperties.FLAMMABLE, List.of(AlchemancyProperties.CHARRED));
      addRelatedProperties(AlchemancyProperties.ASSIMILATING, List.of(AlchemancyProperties.ASSEMBLING));
      addRelatedProperties(AlchemancyProperties.SANITIZED, List.of(AlchemancyProperties.INFECTED));
      addRelatedProperties(AlchemancyProperties.INFECTED, List.of(AlchemancyProperties.SANITIZED, AlchemancyProperties.DEAD));
      addRelatedProperties(AlchemancyProperties.SHATTERING, List.of(AlchemancyProperties.BRITTLE));
      addRelatedProperties(AlchemancyProperties.SADDLED, List.of(AlchemancyProperties.WEALTHY, AlchemancyProperties.SWEET));
      addRelatedProperties(AlchemancyProperties.SMELTING, List.of(AlchemancyProperties.HOLLOW, AlchemancyProperties.FLAMMABLE, AlchemancyProperties.CHARRED));
      addRelatedProperties(
         AlchemancyProperties.DRIPPING,
         List.of(AlchemancyProperties.HOLLOW, AlchemancyProperties.BUCKETING, AlchemancyProperties.CAPTURING, AlchemancyProperties.CALCAREOUS)
      );
      addRelatedProperties(AlchemancyProperties.ABSORBING, List.of(AlchemancyProperties.BUCKETING));
      addRelatedProperties(AlchemancyProperties.MUSICAL, List.of(AlchemancyProperties.SPARKLING));
      addRelatedProperties(AlchemancyProperties.MYCELLIC, List.of(AlchemancyProperties.SPARKLING));
      addRelatedProperties(AlchemancyProperties.ROCKET_POWERED, List.of(AlchemancyProperties.SPARKLING));
      addRelatedProperties(AlchemancyProperties.AIR_WALKER, List.of(AlchemancyProperties.SPARKLING));
      addRelatedProperties(AlchemancyProperties.WAVE_RIDER, List.of(AlchemancyProperties.SPARKLING));
      addRelatedProperties(AlchemancyProperties.MAGNETIC, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.FERROUS));
      addRelatedProperties(AlchemancyProperties.KINETIC_GRAB, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.EXTENDED));
      addRelatedProperties(AlchemancyProperties.BLINKING, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.EXTENDED));
      addRelatedProperties(AlchemancyProperties.GUST_JET, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.EXTENDED));
      addRelatedProperties(AlchemancyProperties.WORLD_OBLITERATOR, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.EXTENDED));
      addRelatedProperties(AlchemancyProperties.MAGIC_RESISTANT, List.of(AlchemancyProperties.ARCANE));
      addRelatedProperties(AlchemancyProperties.EDIBLE, List.of(AlchemancyProperties.SWIFT, AlchemancyProperties.EXTENDED, AlchemancyProperties.SLUGGISH));
      addRelatedProperties(
         AlchemancyProperties.CEASELESS_VOID, List.of(AlchemancyProperties.SWIFT, AlchemancyProperties.EXTENDED, AlchemancyProperties.SLUGGISH)
      );
      addRelatedProperties(AlchemancyProperties.ENERGIZED, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
      addRelatedProperties(AlchemancyProperties.CONDUCTIVE, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
      addRelatedProperties(AlchemancyProperties.INSULATED, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
      addRelatedProperties(AlchemancyProperties.WET, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
      addRelatedProperties(AlchemancyProperties.FERROUS, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
      addRelatedProperties(
         AlchemancyProperties.EXTENDED,
         List.of(AlchemancyProperties.GUST_JET, AlchemancyProperties.BLINKING, AlchemancyProperties.KINETIC_GRAB, AlchemancyProperties.WORLD_OBLITERATOR)
      );
      addRelatedProperties(
         AlchemancyProperties.INFUSION_CODEX, List.of(AlchemancyProperties.REVEALED, AlchemancyProperties.REVEALING, AlchemancyProperties.AWAKENED)
      );
      addRelatedProperties(
         AlchemancyProperties.AWKWARD,
         List.of(
            AlchemancyProperties.BLINDING,
            AlchemancyProperties.FIRE_RESISTANT,
            AlchemancyProperties.TIPSY,
            AlchemancyProperties.NOCTURNAL,
            AlchemancyProperties.SWIFT,
            AlchemancyProperties.SANITIZED,
            AlchemancyProperties.LEAPING,
            AlchemancyProperties.AQUATIC
         )
      );
      addRelatedProperties(
         AlchemancyProperties.SOULBIND,
         List.of(
            AlchemancyProperties.VENGEFUL,
            AlchemancyProperties.VAMPIRIC,
            AlchemancyProperties.LOYAL,
            AlchemancyProperties.CAPTURING,
            AlchemancyProperties.PHASING,
            AlchemancyProperties.LIGHT_SEEKING,
            AlchemancyProperties.RELENTLESS,
            AlchemancyProperties.SPIRIT_BOND,
            AlchemancyProperties.ENERGY_SAPPER
         )
      );
      addRelatedProperties(AlchemancyProperties.SPARKLING, SparklingProperty.getAllParticleProviders());
      addRelatedProperties(
         AlchemancyProperties.CLUELESS,
         AlchemancyProperties.REGISTRY
            .getEntries()
            .stream()
            .filter(p -> p.value() instanceof IDataHolder<?> dataHolder && dataHolder.cluelessCanReset())
            .collect(Collectors.toSet())
      );
   }

   public static void addRelatedProperties(Holder<Property> mainProperty, Collection<Holder<Property>> related) {
   }

   public static void addInnateItem(Holder<Property> propertyHolder, Holder<Item> innate) {
      if (ENTRIES.containsKey(propertyHolder)) {
         ENTRIES.get(propertyHolder).innates().add(innate);
      }
   }

   public CompletableFuture<?> run(CachedOutput output) {
      this.populate();
      return this.registries.thenCompose(lookup -> this.runLater(output));
   }

   protected CompletableFuture<?> runLater(CachedOutput output) {
      List<CompletableFuture<?>> list = new ArrayList<>();

      for (Entry<Holder<Property>, CodexEntryReloadListenener.CodexEntry> entry : ENTRIES.entrySet()) {
         list.add(this.getEntryCompletable(output, entry.getKey(), entry.getValue()));
      }

      return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
   }

   private CompletableFuture<?> getEntryCompletable(CachedOutput output, Holder<Property> propertyHolder, CodexEntryReloadListenener.CodexEntry codexEntry) {
      ResourceLocation key = propertyHolder.getKey().location();
      return DataProvider.saveStable(
         output,
         (JsonElement)CodexEntryReloadListenener.CodexEntry.CODEC.encodeStart(JsonOps.INSTANCE, codexEntry).getOrThrow(),
         this.pathProvider.json(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath()))
      );
   }

   public String getName() {
      return "Infusion Codex Entries";
   }
}
