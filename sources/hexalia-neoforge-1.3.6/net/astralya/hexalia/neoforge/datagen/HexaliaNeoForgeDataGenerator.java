package net.astralya.hexalia.neoforge.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(
   modid = "hexalia"
)
public final class HexaliaNeoForgeDataGenerator {
   private HexaliaNeoForgeDataGenerator() {
   }

   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      PackOutput output = generator.getPackOutput();
      ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
      CompletableFuture<Provider> registries = event.getLookupProvider();
      ModBlockTagProvider blockTags = new ModBlockTagProvider(output, registries, existingFileHelper);
      generator.addProvider(true, new ModItemModelProvider(output, existingFileHelper));
      generator.addProvider(true, new ModBlockStateProvider(output, existingFileHelper));
      generator.addProvider(true, blockTags);
      generator.addProvider(true, new ModBiomeTagsProvider(output, registries, existingFileHelper));
      generator.addProvider(true, new ModEntityTypeTagProvider(output, registries, existingFileHelper));
      generator.addProvider(true, new ModItemTagProvider(output, registries, blockTags.contentsGetter(), existingFileHelper));
      generator.addProvider(true, new ModRecipeProvider(output, registries));
      generator.addProvider(true, new AdvancementProvider(output, registries, List.of(new ModAdvancementsProvider())));
      generator.addProvider(true, new ModBlockLootTableProvider(output, registries));
      generator.addProvider(true, new ModLanguageProvider(output));
      generator.addProvider(true, new ModWorldGenProvider(output, registries));
      generator.addProvider(true, new DatagenExitProvider());
   }
}
