package com.aetherteam.aether.data.generators;

import com.aetherteam.aether.data.generators.loot.AetherAdvancementLoot;
import com.aetherteam.aether.data.generators.loot.AetherBlockLoot;
import com.aetherteam.aether.data.generators.loot.AetherChestLoot;
import com.aetherteam.aether.data.generators.loot.AetherEntityLoot;
import com.aetherteam.aether.data.generators.loot.AetherSelectorLoot;
import com.aetherteam.aether.data.generators.loot.AetherStrippingLoot;
import com.aetherteam.aether.loot.AetherLoot;
import com.aetherteam.aether.loot.AetherLootContexts;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class AetherLootTableData {
   public static LootTableProvider create(PackOutput output, CompletableFuture<Provider> registries) {
      return new LootTableProvider(
         output,
         AetherLoot.IMMUTABLE_LOOT_TABLES,
         List.of(
            new SubProviderEntry(AetherChestLoot::new, LootContextParamSets.CHEST),
            new SubProviderEntry(AetherEntityLoot::new, LootContextParamSets.ENTITY),
            new SubProviderEntry(AetherBlockLoot::new, LootContextParamSets.BLOCK),
            new SubProviderEntry(AetherAdvancementLoot::new, LootContextParamSets.ADVANCEMENT_REWARD),
            new SubProviderEntry(AetherSelectorLoot::new, LootContextParamSets.SELECTOR),
            new SubProviderEntry(AetherStrippingLoot::new, AetherLootContexts.STRIPPING)
         ),
         registries
      );
   }
}
