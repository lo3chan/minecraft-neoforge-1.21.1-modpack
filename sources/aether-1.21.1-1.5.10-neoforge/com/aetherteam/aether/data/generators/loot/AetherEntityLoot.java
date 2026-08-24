package com.aetherteam.aether.data.generators.loot;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.AetherLoot;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class AetherEntityLoot extends EntityLootSubProvider {
   public AetherEntityLoot(Provider registries) {
      super(FeatureFlags.REGISTRY.allFlags(), registries);
   }

   public void generate() {
      this.add(
         (EntityType)AetherEntityTypes.PHYG.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.PORKCHOP)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.FEATHER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.FLYING_COW.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.LEATHER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BEEF)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.SHEEPUFF.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.MUTTON)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_BLACK, createSheepuffTable(Blocks.BLACK_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_BLUE, createSheepuffTable(Blocks.BLUE_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_BROWN, createSheepuffTable(Blocks.BROWN_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_CYAN, createSheepuffTable(Blocks.CYAN_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_GRAY, createSheepuffTable(Blocks.GRAY_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_GREEN, createSheepuffTable(Blocks.GREEN_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_LIGHT_BLUE, createSheepuffTable(Blocks.LIGHT_BLUE_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_LIGHT_GRAY, createSheepuffTable(Blocks.LIGHT_GRAY_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_LIME, createSheepuffTable(Blocks.LIME_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_MAGENTA, createSheepuffTable(Blocks.MAGENTA_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_ORANGE, createSheepuffTable(Blocks.ORANGE_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_PINK, createSheepuffTable(Blocks.PINK_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_PURPLE, createSheepuffTable(Blocks.PURPLE_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_RED, createSheepuffTable(Blocks.RED_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_WHITE, createSheepuffTable(Blocks.WHITE_WOOL));
      this.add((EntityType)AetherEntityTypes.SHEEPUFF.get(), AetherLoot.ENTITIES_SHEEPUFF_YELLOW, createSheepuffTable(Blocks.YELLOW_WOOL));
      this.add(
         (EntityType)AetherEntityTypes.MOA.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.FEATHER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
      );
      this.add((EntityType)AetherEntityTypes.AERWHALE.get(), LootTable.lootTable());
      this.add(
         (EntityType)AetherEntityTypes.AERBUNNY.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.STRING)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.BLUE_SWET.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherItems.SWET_BALL.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.BLUE_AERCLOUD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.GOLDEN_SWET.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Blocks.GLOWSTONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add((EntityType)AetherEntityTypes.WHIRLWIND.get(), LootTable.lootTable());
      this.add((EntityType)AetherEntityTypes.EVIL_WHIRLWIND.get(), LootTable.lootTable());
      this.add(
         (EntityType)AetherEntityTypes.AECHOR_PLANT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherItems.AECHOR_PETAL.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.COCKATRICE.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.FEATHER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.ZEPHYR.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.COLD_AERCLOUD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.SENTRY.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.CARVED_STONE.get())
                        .setWeight(4)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.SENTRY_STONE.get())
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.MIMIC.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Blocks.CHEST)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 3.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_DART.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.ENCHANTED_DART.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.POISON_DART.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.SKYROOT_PICKAXE.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.IRON_RING.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_AMBER.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.ZANITE_GEMSTONE.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.HOLYSTONE_PICKAXE.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherBlocks.ICESTONE.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.AMBROSIUM_SHARD.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherBlocks.AMBROSIUM_TORCH.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.VALKYRIE.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.VICTORY_MEDAL.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
            )
      );
      this.add((EntityType)AetherEntityTypes.FIRE_MINION.get(), LootTable.lootTable());
      this.add(
         (EntityType)AetherEntityTypes.SLIDER.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.BRONZE_DUNGEON_KEY.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.CARVED_STONE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(7.0F, 9.0F)))
                  )
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.SILVER_DUNGEON_KEY.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.GOLDEN_SWORD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
            )
      );
      this.add(
         (EntityType)AetherEntityTypes.SUN_SPIRIT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.GOLD_DUNGEON_KEY.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherBlocks.SUN_ALTAR.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
            )
      );
   }

   private static Builder createSheepuffTable(ItemLike wool) {
      return LootTable.lootTable()
         .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(wool)))
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(NestedLootTable.lootTableReference(((EntityType)AetherEntityTypes.SHEEPUFF.get()).getDefaultLootTable()))
         );
   }

   public Stream<EntityType<?>> getKnownEntityTypes() {
      return AetherEntityTypes.ENTITY_TYPES.getEntries().stream().flatMap(entityType -> Stream.of((EntityType<?>)entityType.get()));
   }
}
