package io.github.razordevs.deep_aether.datagen.loot;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.util.ProblemReporter.Collector;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class DALootTableData extends LootTableProvider {
   public DALootTableData(PackOutput output, CompletableFuture<Provider> registries) {
      super(
         output,
         DALoot.IMMUTABLE_LOOT_TABLES,
         List.of(new SubProviderEntry(DABlockLoot::new, LootContextParamSets.BLOCK), new SubProviderEntry(DAChestLoot::new, LootContextParamSets.CHEST)),
         registries
      );
   }

   protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, Collector problemreporter$collector) {
   }
}
