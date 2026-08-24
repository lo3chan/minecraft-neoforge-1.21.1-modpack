package fuzs.eternalnether.data.loot;

import fuzs.eternalnether.init.ModEntityTypes;
import fuzs.eternalnether.init.ModItems;
import fuzs.puzzleslib.api.data.v2.AbstractLootProvider.EntityTypes;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import java.util.stream.Stream;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModEntityTypeLootProvider extends EntityTypes {
   public ModEntityTypeLootProvider(DataProviderContext context) {
      super(context);
   }

   protected Stream<Reference<EntityType<?>>> getRegistryEntries() {
      return Stream.concat(super.getRegistryEntries(), Stream.of(EntityType.WITHER_SKELETON.builtInRegistryHolder()));
   }

   public void addLootTables() {
      this.add(
         EntityType.WITHER_SKELETON,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.COAL)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(-1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)ModItems.WITHERED_BONE.value())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Blocks.WITHER_SKELETON_SKULL))
                  .when(LootItemKilledByPlayerCondition.killedByPlayer())
                  .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries(), 0.025F, 0.01F))
            )
      );
      this.add((EntityType)ModEntityTypes.WEX.value(), LootTable.lootTable());
      this.add((EntityType)ModEntityTypes.PIGLIN_HUNTER.value(), LootTable.lootTable());
      this.add((EntityType)ModEntityTypes.PIGLIN_PRISONER.value(), LootTable.lootTable());
      this.add(
         (EntityType)ModEntityTypes.WARPED_ENDERMAN.value(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)ModItems.WARPED_ENDER_PEARL.value()))
                  .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries(), 0.5F, 0.0625F))
            )
      );
      this.add(
         (EntityType)ModEntityTypes.WRAITHER.value(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.COAL)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(-1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)ModItems.WITHERED_BONE.value())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Blocks.WITHER_SKELETON_SKULL))
                  .when(LootItemKilledByPlayerCondition.killedByPlayer())
                  .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries(), 0.025F, 0.01F))
            )
      );
      this.add(
         (EntityType)ModEntityTypes.WITHER_SKELETON_KNIGHT.value(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.COAL)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(-1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)ModItems.WITHERED_BONE.value())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Blocks.WITHER_SKELETON_SKULL))
                  .when(LootItemKilledByPlayerCondition.killedByPlayer())
                  .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries(), 0.025F, 0.01F))
            )
      );
      this.add(
         (EntityType)ModEntityTypes.CORPOR.value(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.COAL)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(-1.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)ModItems.WITHERED_BONE.value())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Blocks.WITHER_SKELETON_SKULL))
                  .when(LootItemKilledByPlayerCondition.killedByPlayer())
                  .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries(), 0.025F, 0.01F))
            )
      );
      this.add(
         (EntityType)ModEntityTypes.WITHER_SKELETON_HORSE.value(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)ModItems.WITHERED_BONE.value())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(), UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
   }
}
