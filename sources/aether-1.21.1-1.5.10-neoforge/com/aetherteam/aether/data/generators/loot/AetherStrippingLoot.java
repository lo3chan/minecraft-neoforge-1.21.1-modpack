package com.aetherteam.aether.data.generators.loot;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.AetherLoot;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public record AetherStrippingLoot(Provider registries) implements LootTableSubProvider {
   public void generate(BiConsumer<ResourceKey<LootTable>, Builder> builder) {
      builder.accept(
         AetherLoot.STRIP_GOLDEN_OAK,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_AMBER.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE)))
                  )
            )
      );
   }
}
