package com.aetherteam.aether.data.generators.loot;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.AetherLoot;
import com.aetherteam.aether.loot.conditions.ConfigEnabled;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public record AetherAdvancementLoot(Provider registries) implements LootTableSubProvider {
   public void generate(BiConsumer<ResourceKey<LootTable>, Builder> builder) {
      builder.accept(
         AetherLoot.ENTER_AETHER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_PARACHUTE.get()))
                  .when(ConfigEnabled.isEnabled(AetherConfig.COMMON.enable_startup_loot))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.BOOK_OF_LORE.get()))
                  .when(ConfigEnabled.isEnabled(AetherConfig.COMMON.enable_startup_loot))
            )
      );
   }
}
