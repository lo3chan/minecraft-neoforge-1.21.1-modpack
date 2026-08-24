package io.github.razordevs.deep_aether.datagen.loot;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import io.github.razordevs.deep_aether.datagen.registry.DAEnchantments;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.init.DAMobEffects;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.component.MoaFodder;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public record DAChestLoot(Provider registries) implements LootTableSubProvider {
   public void generate(BiConsumer<ResourceKey<LootTable>, Builder> builder) {
      RegistryLookup<Enchantment> lookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      builder.accept(
         DALoot.BRASS_DUNGEON_COMBINDER_LOOT,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 1.0F))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_COMBINDER_LOOT_SUB_0).setWeight(1))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_COMBINDER_LOOT_SUB_1).setWeight(1))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_COMBINDER_LOOT_SUB_2).setWeight(1))
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_COMBINDER_LOOT_SUB_0,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(AetherItems.GOLDEN_AMBER)))
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(DAItems.FROZEN_GOLDEN_BERRIES)))
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(DAItems.QUAIL_EGG)))
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_COMBINDER_LOOT_SUB_1,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(AetherItems.GOLDEN_AMBER)))
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(DAItems.FROZEN_GOLDEN_BERRIES)))
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(AetherBlocks.ICESTONE)))
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_COMBINDER_LOOT_SUB_2,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(AetherItems.GOLDEN_AMBER)))
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(DAItems.FROZEN_GOLDEN_BERRIES)))
            .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(4.0F, 6.0F)).add(LootItem.lootTableItem(AetherItems.BLUE_BERRY)))
      );
      builder.accept(
         DALoot.BRASS_DUNGEON,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 1.0F))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_LOOT).setWeight(8))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_DISC).setWeight(1))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_TRASH).setWeight(1))
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_FODDER_LOOT,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemRandomChanceCondition.randomChance(0.3F))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.MOA_FODDER.get()))
                  .apply(
                     SetComponentsFunction.setComponent(
                        (DataComponentType)DADataComponentTypes.MOA_FODDER.get(), new MoaFodder(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 14400, 1))
                     )
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemRandomChanceCondition.randomChance(0.3F))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.MOA_FODDER.get()))
                  .apply(
                     SetComponentsFunction.setComponent(
                        (DataComponentType)DADataComponentTypes.MOA_FODDER.get(), new MoaFodder(new MobEffectInstance(MobEffects.JUMP, 14400, 1))
                     )
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemRandomChanceCondition.randomChance(0.3F))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.MOA_FODDER.get()))
                  .apply(
                     SetComponentsFunction.setComponent(
                        (DataComponentType)DADataComponentTypes.MOA_FODDER.get(), new MoaFodder(new MobEffectInstance(DAMobEffects.MOA_BONUS_JUMPS, 14400, 1))
                     )
                  )
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_LOOT,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 1.0F))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_TOOLS_PICKAXE.get()).setWeight(4))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_TOOLS_AXE.get()).setWeight(3))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_TOOLS_SWORD.get()).setWeight(3))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.BLUE_GUMMY_SWET.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DABlocks.STERLING_AERCLOUD.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.ICE_PENDANT.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_TOOLS_SHOVEL.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_TOOLS_HOE.get()).setWeight(1))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_FODDER_LOOT).setWeight(2))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(2.0F, 6.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_DART.get())
                        .setWeight(3)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 10.0F)))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherItems.ENCHANTED_DART.get())
                        .setWeight(3)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F)))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherItems.POISON_DART.get())
                        .setWeight(3)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                  )
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.SKYROOT_PICKAXE.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.IRON_RING.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.ZANITE_GEMSTONE.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.HOLYSTONE_PICKAXE.get()).setWeight(1))
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.COLD_AERCLOUD.get())
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F)))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherItems.AMBROSIUM_SHARD.get())
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.BLUE_AERCLOUD.get())
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 4.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)DAItems.AERGLOW_BLOSSOM.get())
                        .setWeight(6)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.AMBROSIUM_TORCH.get())
                        .setWeight(5)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F)))
                  )
                  .add(LootItem.lootTableItem((ItemLike)DAItems.GOLDEN_BERRIES.get()).setWeight(3))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.FROZEN_GOLDEN_BERRIES.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.ANTIDOTE.get()).setWeight(2))
                  .add(LootItem.lootTableItem(Items.ARROW).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F))))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_RING.get()).setWeight(2))
                  .add(
                     LootItem.lootTableItem((ItemLike)DABlocks.ROSEROOT_PLANKS.get())
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_TRASH,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(2.0F, 6.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)DAItems.AERGLOW_BLOSSOM.get())
                        .setWeight(6)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.AMBROSIUM_TORCH.get())
                        .setWeight(5)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))
                  )
                  .add(LootItem.lootTableItem(AetherBlocks.COLD_AERCLOUD).setWeight(3))
                  .add(LootItem.lootTableItem(AetherBlocks.BLUE_AERCLOUD).setWeight(2))
                  .add(LootItem.lootTableItem(AetherBlocks.GOLDEN_AERCLOUD).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE.get()).setWeight(2))
                  .add(
                     LootItem.lootTableItem((ItemLike)DABlocks.ROSEROOT_PLANKS.get())
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.SKYROOT_PICKAXE.get()).setWeight(1))
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_DISC,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 1.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.MUSIC_DISC_AETHER_TUNE.get()).setWeight(2))
                  .add(LootItem.lootTableItem(Items.MUSIC_DISC_CAT).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(3.0F, 10.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)DAItems.AERGLOW_BLOSSOM.get())
                        .setWeight(4)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 8.0F)))
                  )
                  .add(
                     LootItem.lootTableItem((ItemLike)AetherBlocks.AMBROSIUM_TORCH.get())
                        .setWeight(4)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 16.0F)))
                  )
                  .add(
                     LootItem.lootTableItem(AetherBlocks.COLD_AERCLOUD).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_TOOLS_PICKAXE.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.SKYROOT_PICKAXE.get()).setWeight(2))
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_REWARD,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 2.0F)).add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_FODDER_LOOT))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 1.0F))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_STORM_FORGED).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 1.0F))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_TREASURE).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 1.0F))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_GUMMIES).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(3.0F, 5.0F))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_HELMET.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_CHESTPLATE.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_LEGGINGS.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_BOOTS.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_GLOVES.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_RING.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.WIND_SHIELD.get()).setWeight(4))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.CLOUD_STAFF.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.STORM_BOW.get()).setWeight(4))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.STORM_SWORD.get()).setWeight(4))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.CLOUD_CAPE.get()).setWeight(4))
                  .add(
                     LootItem.lootTableItem((ItemLike)DAItems.SKYJADE.get())
                        .setWeight(3)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                  )
                  .add(LootItem.lootTableItem((ItemLike)AetherBlocks.COLD_AERCLOUD.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.BLADE_OF_LUCK.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.MUSIC_DISC_CYCLONE.get()).setWeight(2))
                  .add(NestedLootTable.lootTableReference(DALoot.BRASS_DUNGEON_STORM_FORGED).setWeight(10))
                  .add(LootItem.lootTableItem((ItemLike)DABlocks.NIMBUS_STONE.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DABlocks.NIMBUS_PILLAR.get()).setWeight(1))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(3.0F, 6.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_DART.get()).setWeight(3))
                  .add(LootItem.lootTableItem(Items.ARROW).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DABlocks.NIMBUS_STONE.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DABlocks.NIMBUS_PILLAR.get()).setWeight(1))
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_TREASURE,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 2.0F))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.CLOUD_CAPE.get()).setWeight(3))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.AERCLOUD_NECKLACE.get()).setWeight(3))
                  .add(
                     LootItem.lootTableItem(Items.BOOK)
                        .apply(
                           new net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction.Builder()
                              .withEnchantment(lookup.getOrThrow(DAEnchantments.GLOVES_REACH))
                        )
                        .setWeight(3)
                  )
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_STORM_FORGED,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.STORMFORGED_HELMET.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.STORMFORGED_CHESTPLATE.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.STORMFORGED_LEGGINGS.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.STORMFORGED_BOOTS.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.STORMFORGED_GLOVES.get()).setWeight(1))
            )
      );
      builder.accept(
         DALoot.BRASS_DUNGEON_GUMMIES,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(2.0F, 4.0F))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.BLUE_GUMMY_SWET.get()).setWeight(4))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_GUMMY_SWET.get()).setWeight(2))
            )
      );
      builder.accept(
         DALoot.ALTAR_CAMP,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(1.0F, 1.0F))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE.get()).setWeight(4))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_TOOLS_AXE.get()).setWeight(3))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.BLUE_GUMMY_SWET.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)DAItems.SKYJADE_TOOLS_HOE.get()).setWeight(1))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.COLD_PARACHUTE.get()).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)AetherItems.GOLDEN_DART_SHOOTER.get()).setWeight(1))
            )
      );
   }
}
