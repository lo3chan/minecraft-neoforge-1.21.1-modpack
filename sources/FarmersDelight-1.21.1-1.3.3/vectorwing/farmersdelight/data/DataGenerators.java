package vectorwing.farmersdelight.data;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import vectorwing.farmersdelight.common.registry.ModBiomeModifiers;
import vectorwing.farmersdelight.common.registry.ModDamageTypes;
import vectorwing.farmersdelight.common.world.WildCropGeneration;
import vectorwing.farmersdelight.data.loot.FDBlockLoot;
import vectorwing.farmersdelight.data.loot.FDChestLoot;
import vectorwing.farmersdelight.data.tools.StructureUpdater;

@EventBusSubscriber(
   modid = "farmersdelight"
)
public class DataGenerators {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      PackOutput output = generator.getPackOutput();
      ExistingFileHelper helper = event.getExistingFileHelper();
      RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder()
         .add(Registries.CONFIGURED_FEATURE, WildCropGeneration::bootstrapConfiguredFeatures)
         .add(Registries.PLACED_FEATURE, WildCropGeneration::bootstrapPlacedFeatures)
         .add(Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrapBiomeModifiers)
         .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrapDamageTypes)
         .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap);
      DatapackBuiltinEntriesProvider datapackProvider = new DatapackBuiltinEntriesProvider(
         output, event.getLookupProvider(), registrySetBuilder, Set.of("farmersdelight")
      );
      CompletableFuture<Provider> lookupProvider = datapackProvider.getRegistryProvider();
      generator.addProvider(event.includeServer(), datapackProvider);
      BlockTags blockTags = new BlockTags(output, lookupProvider, helper);
      generator.addProvider(event.includeServer(), blockTags);
      generator.addProvider(event.includeServer(), new ItemTags(output, lookupProvider, blockTags.contentsGetter(), helper));
      generator.addProvider(event.includeServer(), new EntityTags(output, lookupProvider, helper));
      generator.addProvider(event.includeServer(), new DamageTypeTags(output, lookupProvider, "farmersdelight", helper));
      generator.addProvider(event.includeServer(), new EnchantmentTags(output, lookupProvider, helper));
      generator.addProvider(event.includeServer(), new Recipes(output, lookupProvider));
      generator.addProvider(event.includeServer(), new LootModifiers(output, lookupProvider));
      generator.addProvider(event.includeServer(), new DataMaps(output, lookupProvider));
      generator.addProvider(event.includeServer(), new Advancements(output, lookupProvider, helper));
      generator.addProvider(
         event.includeServer(),
         new LootTableProvider(
            output,
            Collections.emptySet(),
            List.of(new SubProviderEntry(FDBlockLoot::new, LootContextParamSets.BLOCK), new SubProviderEntry(FDChestLoot::new, LootContextParamSets.CHEST)),
            lookupProvider
         )
      );
      generator.addProvider(event.includeServer(), new StructureUpdater("structures/village/houses", "farmersdelight", helper, output));
      BlockStates blockStates = new BlockStates(output, helper);
      generator.addProvider(event.includeClient(), blockStates);
      generator.addProvider(event.includeClient(), new ItemModels(output, blockStates.models().existingFileHelper));
      generator.addProvider(event.includeClient(), new SoundDefinitions(output, helper));
   }
}
