package com.aetherteam.aether.data.providers;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.components.AetherDataComponents;
import com.aetherteam.aether.loot.functions.DoubleDrops;
import com.aetherteam.aether.loot.functions.SpawnTNT;
import com.aetherteam.aether.loot.functions.SpawnXP;
import com.aetherteam.nitrogen.data.providers.NitrogenBlockLootSubProvider;
import java.util.Set;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction.Source;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction.NameSource;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public abstract class AetherBlockLootSubProvider extends NitrogenBlockLootSubProvider {
   public AetherBlockLootSubProvider(Set<Item> items, FeatureFlagSet flags, Provider registries) {
      super(items, flags, registries);
   }

   public void dropDoubleWithSilk(Block block, ItemLike drop) {
      this.add(block, result -> this.droppingDoubleWithSilkTouch(result, drop));
   }

   public void dropSelfDouble(Block block) {
      this.add(block, this.droppingDouble(block));
   }

   public void dropDoubleWithFortune(Block block, Item drop) {
      this.add(block, result -> this.droppingDoubleItemsWithFortune(result, drop));
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingDoubleWithSilkTouch(Block block, ItemLike noSilkTouch) {
      return this.droppingDoubleWithSilkTouch(block, (Builder<?>)this.applyExplosionCondition(block, LootItem.lootTableItem(noSilkTouch)));
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingDoubleWithSilkTouch(Block block, Builder<?> builder) {
      return this.droppingDouble(block, this.hasSilkTouch(), builder);
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingDouble(ItemLike item) {
      return LootTable.lootTable()
         .withPool(
            (net.minecraft.world.level.storage.loot.LootPool.Builder)this.applyExplosionCondition(
               item, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(item))
            )
         )
         .apply(DoubleDrops.builder());
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingDouble(
      Block block, net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder conditionBuilder, Builder<?> builder
   ) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(block).when(conditionBuilder))
                     .otherwise(builder)
               )
         )
         .apply(DoubleDrops.builder());
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingWithChancesAndSkyrootSticks(Block block, Block sapling, float... chances) {
      return this.createForgeSilkTouchOrShearsDispatchTable(
            block,
            ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionCondition(
                  block, LootItem.lootTableItem(sapling)
               ))
               .when(BonusLevelTableCondition.bonusLevelFlatChance(this.registries.holderOrThrow(Enchantments.FORTUNE), chances))
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(HAS_SHEARS.or(this.hasSilkTouch()).invert())
               .add(
                  ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem((ItemLike)AetherItems.SKYROOT_STICK.get())
                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                     ))
                     .when(
                        BonusLevelTableCondition.bonusLevelFlatChance(
                           this.registries.holderOrThrow(Enchantments.FORTUNE), new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F}
                        )
                     )
               )
         )
         .apply(DoubleDrops.builder());
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingGoldenOakLeaves(Block block, Block sapling, float... chances) {
      return this.droppingWithChancesAndSkyrootSticks(block, sapling, chances)
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(HAS_SHEARS.or(this.hasSilkTouch()).invert())
               .add(
                  ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionCondition(
                        block, LootItem.lootTableItem(Items.GOLDEN_APPLE)
                     ))
                     .when(
                        BonusLevelTableCondition.bonusLevelFlatChance(
                           this.registries.holderOrThrow(Enchantments.FORTUNE), new float[]{5.0E-5F, 5.5555556E-5F, 6.25E-5F, 8.333334E-5F, 2.5E-4F}
                        )
                     )
               )
         );
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingDoubleItemsWithFortune(Block block, Item item) {
      return this.createSilkTouchDispatchTable(
            block,
            (Builder)this.applyExplosionDecay(
               block, LootItem.lootTableItem(item).apply(ApplyBonusCount.addOreBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE)))
            )
         )
         .apply(DoubleDrops.builder());
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingWithSkyrootSticks(Block block) {
      return this.createForgeSilkTouchOrShearsDispatchTable(
            block,
            ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionDecay(
                  block,
                  LootItem.lootTableItem((ItemLike)AetherItems.SKYROOT_STICK.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
               ))
               .when(
                  BonusLevelTableCondition.bonusLevelFlatChance(
                     this.registries.holderOrThrow(Enchantments.FORTUNE), new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F}
                  )
               )
         )
         .apply(DoubleDrops.builder());
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingWithFruitAndSkyrootSticks(Block block, Item fruit) {
      return this.createForgeSilkTouchOrShearsDispatchTable(block, (Builder)this.applyExplosionDecay(block, LootItem.lootTableItem(fruit)))
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(HAS_SHEARS.or(this.hasSilkTouch()).invert())
               .add(
                  ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem((ItemLike)AetherItems.SKYROOT_STICK.get())
                           .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                     ))
                     .when(
                        BonusLevelTableCondition.bonusLevelFlatChance(
                           this.registries.holderOrThrow(Enchantments.FORTUNE), new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F}
                        )
                     )
               )
         )
         .apply(DoubleDrops.builder());
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingDoubleGoldenOak(Block original, Block block, Item item) {
      return LootTable.lootTable()
         .withPool(
            (net.minecraft.world.level.storage.loot.LootPool.Builder)this.applyExplosionDecay(
               block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(original).when(this.hasSilkTouch()))
            )
         )
         .withPool(
            (net.minecraft.world.level.storage.loot.LootPool.Builder)this.applyExplosionDecay(
               block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block).when(this.hasSilkTouch().invert()))
            )
         )
         .withPool(
            (net.minecraft.world.level.storage.loot.LootPool.Builder)this.applyExplosionDecay(
               item,
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(
                                 item
                              )
                              .when(
                                 MatchTool.toolMatches(
                                    net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(AetherTags.Items.GOLDEN_AMBER_HARVESTERS)
                                 )
                              ))
                           .when(this.hasSilkTouch().invert()))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE)))
                  )
            )
         )
         .apply(DoubleDrops.builder());
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingBerryBush(Block block, Block stem, Item drop) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionDecay(
                        block, LootItem.lootTableItem(drop).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                     ))
                     .apply(ApplyBonusCount.addUniformBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE)))
               )
               .when(this.hasSilkTouch().invert())
               .apply(DoubleDrops.builder())
         )
         .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block)).when(this.hasSilkTouch()))
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(stem).when(LootItemEntityPropertyCondition.entityPresent(EntityTarget.THIS).invert()))
         );
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingTreasureChest(Block block) {
      return LootTable.lootTable()
         .withPool(
            (net.minecraft.world.level.storage.loot.LootPool.Builder)this.applyExplosionCondition(
               block,
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(block)
                        .apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY))
                        .apply(
                           CopyComponentsFunction.copyComponents(Source.BLOCK_ENTITY)
                              .include((DataComponentType)AetherDataComponents.LOCKED.get())
                              .include((DataComponentType)AetherDataComponents.DUNGEON_KIND.get())
                        )
                  )
            )
         );
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingPresentLoot(Block block) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(Items.AIR).setWeight(18).apply(SpawnTNT.builder()))
               .add(LootItem.lootTableItem(Items.AIR).setWeight(9).apply(SpawnXP.builder()))
               .add(
                  (Builder)this.applyExplosionDecay(
                     block,
                     LootItem.lootTableItem((ItemLike)AetherItems.GINGERBREAD_MAN.get())
                        .setWeight(8)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 6.0F)))
                  )
               )
               .add((Builder)this.applyExplosionDecay(block, LootItem.lootTableItem((ItemLike)AetherItems.CANDY_CANE_SWORD.get()).setWeight(1)))
               .when(this.hasSilkTouch().invert())
         )
         .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block)).when(this.hasSilkTouch()));
   }
}
