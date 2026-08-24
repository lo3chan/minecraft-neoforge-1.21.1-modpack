package vazkii.psi.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(
   modid = "psi"
)
public class DataGenerator {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
      net.minecraft.data.DataGenerator generator = event.getGenerator();
      CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
      PackOutput packOutput = generator.getPackOutput();
      if (event.includeServer()) {
         PsiBlockTagProvider blockTagProvider = new PsiBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
         generator.addProvider(true, blockTagProvider);
         generator.addProvider(true, new PsiDamageTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
         generator.addProvider(true, new PsiItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));
         generator.addProvider(true, new PsiRecipeGenerator(packOutput, lookupProvider));
         generator.addProvider(
            true,
            new LootTableProvider(
               packOutput, Collections.emptySet(), List.of(new SubProviderEntry(PsiBlockLootProvider::new, LootContextParamSets.BLOCK)), lookupProvider
            )
         );
      }

      if (event.includeClient()) {
         generator.addProvider(true, new PsiBlockModelGenerator(packOutput, existingFileHelper));
         generator.addProvider(true, new PsiItemModelGenerator(packOutput, existingFileHelper));
      }
   }
}
