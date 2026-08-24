package net.astralya.hexalia.neoforge.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.MandrakeCropBlock;
import net.astralya.hexalia.block.custom.RabbageCropBlock;
import net.astralya.hexalia.block.custom.SaltsproutBlock;
import net.astralya.hexalia.block.custom.SunfireTomatoCropBlock;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;

public final class ModBlockLootTableProvider extends LootTableProvider {
   public ModBlockLootTableProvider(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, Set.of(), List.of(new SubProviderEntry(ModBlockLootTableProvider.ModBlockLootSubProvider::new, LootContextParamSets.BLOCK)), registries);
   }

   private static final class ModBlockLootSubProvider extends BlockLootSubProvider {
      private static final List<Block> KNOWN_BLOCKS = List.of(
         (Block)ModBlocks.INFUSED_DIRT.get(),
         (Block)ModBlocks.INFUSED_FARMLAND.get(),
         (Block)ModBlocks.SILKWORM_COCOON.get(),
         (Block)ModBlocks.EGG_CLUSTER.get(),
         (Block)ModBlocks.RITUAL_TABLE.get(),
         (Block)ModBlocks.RITUAL_BRAZIER.get(),
         (Block)ModBlocks.SMALL_CAULDRON.get(),
         (Block)ModBlocks.MORTAR_AND_PESTLE.get(),
         (Block)ModBlocks.CENSER.get(),
         (Block)ModBlocks.NESTING_BLOCK.get(),
         (Block)ModBlocks.SHELF.get(),
         (Block)ModBlocks.DREAMCATCHER.get(),
         (Block)ModBlocks.CANDLE_SKULL.get(),
         (Block)ModBlocks.WITHER_CANDLE_SKULL.get(),
         (Block)ModBlocks.MORPHORA.get(),
         (Block)ModBlocks.POTTED_MORPHORA.get(),
         (Block)ModBlocks.GRIMSHADE.get(),
         (Block)ModBlocks.POTTED_GRIMSHADE.get(),
         (Block)ModBlocks.NAUTILITE.get(),
         (Block)ModBlocks.WINDSONG.get(),
         (Block)ModBlocks.POTTED_WINDSONG.get(),
         (Block)ModBlocks.ASTRYLIS.get(),
         (Block)ModBlocks.POTTED_ASTRYLIS.get(),
         (Block)ModBlocks.LOURDES.get(),
         (Block)ModBlocks.POTTED_LOURDES.get(),
         (Block)ModBlocks.AEGIFLORA.get(),
         (Block)ModBlocks.POTTED_AEGIFLORA.get(),
         (Block)ModBlocks.WITHERED_AEGIFLORA.get(),
         (Block)ModBlocks.POTTED_WITHERED_AEGIFLORA.get(),
         (Block)ModBlocks.BEGONIA.get(),
         (Block)ModBlocks.POTTED_BEGONIA.get(),
         (Block)ModBlocks.LAVENDER.get(),
         (Block)ModBlocks.POTTED_LAVENDER.get(),
         (Block)ModBlocks.DAHLIA.get(),
         (Block)ModBlocks.POTTED_DAHLIA.get(),
         (Block)ModBlocks.NIGHTSHADE_BUSH.get(),
         (Block)ModBlocks.POTTED_NIGHTSHADE_BUSH.get(),
         (Block)ModBlocks.SPIRIT_BLOOM.get(),
         (Block)ModBlocks.POTTED_SPIRIT_BLOOM.get(),
         (Block)ModBlocks.DREAMSHROOM.get(),
         (Block)ModBlocks.POTTED_DREAMSHROOM.get(),
         (Block)ModBlocks.PALE_MUSHROOM.get(),
         (Block)ModBlocks.SIREN_KELP.get(),
         (Block)ModBlocks.GHOST_FERN.get(),
         (Block)ModBlocks.POTTED_GHOST_FERN.get(),
         (Block)ModBlocks.CELESTIAL_BLOOM.get(),
         (Block)ModBlocks.POTTED_CELESTIAL_BLOOM.get(),
         (Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get(),
         (Block)ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM.get(),
         (Block)ModBlocks.LOTUS_FLOWER.get(),
         (Block)ModBlocks.WITCHWEED.get(),
         (Block)ModBlocks.MANDRAKE_CROP.get(),
         (Block)ModBlocks.SUNFIRE_TOMATO_CROP.get(),
         (Block)ModBlocks.RABBAGE_CROP.get(),
         (Block)ModBlocks.SALTSPROUT.get(),
         (Block)ModBlocks.SALT_BLOCK.get(),
         (Block)ModBlocks.SALT_LAMP.get(),
         (Block)ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get(),
         (Block)ModBlocks.RUSTIC_OVEN.get(),
         (Block)ModBlocks.COTTONWOOD_CATKIN.get(),
         (Block)ModBlocks.COTTONWOOD_LEAVES.get(),
         (Block)ModBlocks.COTTONWOOD_SAPLING.get(),
         (Block)ModBlocks.POTTED_COTTONWOOD_SAPLING.get(),
         (Block)ModBlocks.COTTONWOOD_LOG.get(),
         (Block)ModBlocks.STRIPPED_COTTONWOOD_LOG.get(),
         (Block)ModBlocks.COTTONWOOD_WOOD.get(),
         (Block)ModBlocks.STRIPPED_COTTONWOOD_WOOD.get(),
         (Block)ModBlocks.COTTONWOOD_PLANKS.get(),
         (Block)ModBlocks.COTTONWOOD_STAIRS.get(),
         (Block)ModBlocks.COTTONWOOD_SLAB.get(),
         (Block)ModBlocks.COTTONWOOD_BUTTON.get(),
         (Block)ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(),
         (Block)ModBlocks.COTTONWOOD_FENCE.get(),
         (Block)ModBlocks.COTTONWOOD_FENCE_GATE.get(),
         (Block)ModBlocks.COTTONWOOD_TRAPDOOR.get(),
         (Block)ModBlocks.COTTONWOOD_DOOR.get(),
         (Block)ModBlocks.COTTONWOOD_SIGN.get(),
         (Block)ModBlocks.COTTONWOOD_WALL_SIGN.get(),
         (Block)ModBlocks.COTTONWOOD_HANGING_SIGN.get(),
         (Block)ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(),
         (Block)ModBlocks.WILLOW_LEAVES.get(),
         (Block)ModBlocks.WILLOW_SAPLING.get(),
         (Block)ModBlocks.POTTED_WILLOW_SAPLING.get(),
         (Block)ModBlocks.WILLOW_LOG.get(),
         (Block)ModBlocks.STRIPPED_WILLOW_LOG.get(),
         (Block)ModBlocks.WILLOW_WOOD.get(),
         (Block)ModBlocks.STRIPPED_WILLOW_WOOD.get(),
         (Block)ModBlocks.WILLOW_PLANKS.get(),
         (Block)ModBlocks.WILLOW_STAIRS.get(),
         (Block)ModBlocks.WILLOW_SLAB.get(),
         (Block)ModBlocks.WILLOW_BUTTON.get(),
         (Block)ModBlocks.WILLOW_PRESSURE_PLATE.get(),
         (Block)ModBlocks.WILLOW_FENCE.get(),
         (Block)ModBlocks.WILLOW_FENCE_GATE.get(),
         (Block)ModBlocks.WILLOW_TRAPDOOR.get(),
         (Block)ModBlocks.WILLOW_DOOR.get(),
         (Block)ModBlocks.WILLOW_SIGN.get(),
         (Block)ModBlocks.WILLOW_WALL_SIGN.get(),
         (Block)ModBlocks.WILLOW_HANGING_SIGN.get(),
         (Block)ModBlocks.WILLOW_HANGING_WALL_SIGN.get()
      );

      protected ModBlockLootSubProvider(Provider registries) {
         super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
      }

      protected void generate() {
         this.dropSelf((Block)ModBlocks.INFUSED_DIRT.get());
         this.add((Block)ModBlocks.INFUSED_FARMLAND.get(), this.createSingleItemTable((ItemLike)ModItems.INFUSED_DIRT.get()));
         this.add((Block)ModBlocks.SILKWORM_COCOON.get(), this.createSingleItemTable((ItemLike)ModItems.SILKWORM.get()));
         this.add((Block)ModBlocks.EGG_CLUSTER.get(), this.createSingleItemTable((ItemLike)ModItems.SILKWORM.get()));
         this.dropSelf((Block)ModBlocks.RITUAL_TABLE.get());
         this.dropSelf((Block)ModBlocks.RITUAL_BRAZIER.get());
         this.dropSelf((Block)ModBlocks.SMALL_CAULDRON.get());
         this.dropSelf((Block)ModBlocks.MORTAR_AND_PESTLE.get());
         this.dropSelf((Block)ModBlocks.CENSER.get());
         this.dropSelf((Block)ModBlocks.NESTING_BLOCK.get());
         this.dropSelf((Block)ModBlocks.SHELF.get());
         this.dropSelf((Block)ModBlocks.DREAMCATCHER.get());
         this.dropSelf((Block)ModBlocks.CANDLE_SKULL.get());
         this.dropSelf((Block)ModBlocks.WITHER_CANDLE_SKULL.get());
         this.dropSelf((Block)ModBlocks.MORPHORA.get());
         this.add((Block)ModBlocks.POTTED_MORPHORA.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.MORPHORA.get()));
         this.dropSelf((Block)ModBlocks.GRIMSHADE.get());
         this.add((Block)ModBlocks.POTTED_GRIMSHADE.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.GRIMSHADE.get()));
         this.add((Block)ModBlocks.NAUTILITE.get(), this.createSingleItemTable((ItemLike)ModItems.NAUTILITE.get()));
         this.dropSelf((Block)ModBlocks.WINDSONG.get());
         this.add((Block)ModBlocks.POTTED_WINDSONG.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.WINDSONG.get()));
         this.dropSelf((Block)ModBlocks.ASTRYLIS.get());
         this.add((Block)ModBlocks.POTTED_ASTRYLIS.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.ASTRYLIS.get()));
         this.dropSelf((Block)ModBlocks.LOURDES.get());
         this.add((Block)ModBlocks.POTTED_LOURDES.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.LOURDES.get()));
         this.dropSelf((Block)ModBlocks.AEGIFLORA.get());
         this.add((Block)ModBlocks.POTTED_AEGIFLORA.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.AEGIFLORA.get()));
         this.dropSelf((Block)ModBlocks.WITHERED_AEGIFLORA.get());
         this.add((Block)ModBlocks.POTTED_WITHERED_AEGIFLORA.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.WITHERED_AEGIFLORA.get()));
         this.dropSelf((Block)ModBlocks.BEGONIA.get());
         this.add((Block)ModBlocks.POTTED_BEGONIA.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.BEGONIA.get()));
         this.dropSelf((Block)ModBlocks.LAVENDER.get());
         this.add((Block)ModBlocks.POTTED_LAVENDER.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.LAVENDER.get()));
         this.dropSelf((Block)ModBlocks.DAHLIA.get());
         this.add((Block)ModBlocks.POTTED_DAHLIA.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.DAHLIA.get()));
         this.dropSelf((Block)ModBlocks.NIGHTSHADE_BUSH.get());
         this.add((Block)ModBlocks.POTTED_NIGHTSHADE_BUSH.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.NIGHTSHADE_BUSH.get()));
         this.dropSelf((Block)ModBlocks.SPIRIT_BLOOM.get());
         this.add((Block)ModBlocks.POTTED_SPIRIT_BLOOM.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.SPIRIT_BLOOM.get()));
         this.dropSelf((Block)ModBlocks.DREAMSHROOM.get());
         this.add((Block)ModBlocks.POTTED_DREAMSHROOM.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.DREAMSHROOM.get()));
         this.add((Block)ModBlocks.PALE_MUSHROOM.get(), this.createPetalsDrops((Block)ModBlocks.PALE_MUSHROOM.get()));
         this.add((Block)ModBlocks.SIREN_KELP.get(), this.createSingleItemTable((ItemLike)ModItems.SIREN_KELP.get()));
         this.dropSelf((Block)ModBlocks.GHOST_FERN.get());
         this.add((Block)ModBlocks.POTTED_GHOST_FERN.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.GHOST_FERN.get()));
         this.dropSelf((Block)ModBlocks.CELESTIAL_BLOOM.get());
         this.add((Block)ModBlocks.POTTED_CELESTIAL_BLOOM.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.CELESTIAL_BLOOM.get()));
         this.dropSelf((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get());
         this.add((Block)ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.WITHERED_CELESTIAL_BLOOM.get()));
         this.dropSelf((Block)ModBlocks.LOTUS_FLOWER.get());
         this.dropSelf((Block)ModBlocks.WITCHWEED.get());
         this.add(
            (Block)ModBlocks.MANDRAKE_CROP.get(),
            this.createCropDrops(
               (Block)ModBlocks.MANDRAKE_CROP.get(),
               (Item)ModItems.MANDRAKE.get(),
               (Item)ModItems.MANDRAKE_SEEDS.get(),
               this.cropAge((Block)ModBlocks.MANDRAKE_CROP.get(), MandrakeCropBlock.AGE, 3)
            )
         );
         this.add(
            (Block)ModBlocks.SUNFIRE_TOMATO_CROP.get(),
            this.createCropDrops(
               (Block)ModBlocks.SUNFIRE_TOMATO_CROP.get(),
               (Item)ModItems.SUNFIRE_TOMATO.get(),
               (Item)ModItems.SUNFIRE_TOMATO_SEEDS.get(),
               this.cropAge((Block)ModBlocks.SUNFIRE_TOMATO_CROP.get(), SunfireTomatoCropBlock.AGE, 3)
            )
         );
         this.add(
            (Block)ModBlocks.RABBAGE_CROP.get(),
            this.createCropDrops(
               (Block)ModBlocks.RABBAGE_CROP.get(),
               (Item)ModItems.RABBAGE.get(),
               (Item)ModItems.RABBAGE_SEEDS.get(),
               this.cropAge((Block)ModBlocks.RABBAGE_CROP.get(), RabbageCropBlock.AGE, 3)
            )
         );
         this.add(
            (Block)ModBlocks.SALTSPROUT.get(),
            this.createMatureSingleCropDrops(
               (Block)ModBlocks.SALTSPROUT.get(), (Item)ModItems.SALTSPROUT.get(), this.cropAge((Block)ModBlocks.SALTSPROUT.get(), SaltsproutBlock.AGE, 2)
            )
         );
         this.add((Block)ModBlocks.SALT_BLOCK.get(), this.createSingleItemTable((ItemLike)ModItems.SALT.get()));
         this.dropSelf((Block)ModBlocks.SALT_LAMP.get());
         this.dropSelf((Block)ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get());
         this.dropSelf((Block)ModBlocks.RUSTIC_OVEN.get());
         this.dropSelf((Block)ModBlocks.COTTONWOOD_CATKIN.get());
         this.add(
            (Block)ModBlocks.COTTONWOOD_LEAVES.get(),
            this.createLeavesDrops((Block)ModBlocks.COTTONWOOD_LEAVES.get(), (Block)ModBlocks.COTTONWOOD_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES)
         );
         this.dropSelf((Block)ModBlocks.COTTONWOOD_SAPLING.get());
         this.add((Block)ModBlocks.POTTED_COTTONWOOD_SAPLING.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.COTTONWOOD_SAPLING.get()));
         this.dropWoodSet(
            (Block)ModBlocks.COTTONWOOD_LOG.get(),
            (Block)ModBlocks.STRIPPED_COTTONWOOD_LOG.get(),
            (Block)ModBlocks.COTTONWOOD_WOOD.get(),
            (Block)ModBlocks.STRIPPED_COTTONWOOD_WOOD.get(),
            (Block)ModBlocks.COTTONWOOD_PLANKS.get(),
            (Block)ModBlocks.COTTONWOOD_STAIRS.get(),
            (Block)ModBlocks.COTTONWOOD_SLAB.get(),
            (Block)ModBlocks.COTTONWOOD_BUTTON.get(),
            (Block)ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(),
            (Block)ModBlocks.COTTONWOOD_FENCE.get(),
            (Block)ModBlocks.COTTONWOOD_FENCE_GATE.get(),
            (Block)ModBlocks.COTTONWOOD_TRAPDOOR.get(),
            (Block)ModBlocks.COTTONWOOD_DOOR.get(),
            (Block)ModBlocks.COTTONWOOD_SIGN.get(),
            (Block)ModBlocks.COTTONWOOD_WALL_SIGN.get(),
            (Block)ModBlocks.COTTONWOOD_HANGING_SIGN.get(),
            (Block)ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get()
         );
         this.add(
            (Block)ModBlocks.WILLOW_LEAVES.get(),
            this.createLeavesDrops((Block)ModBlocks.WILLOW_LEAVES.get(), (Block)ModBlocks.WILLOW_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES)
         );
         this.dropSelf((Block)ModBlocks.WILLOW_SAPLING.get());
         this.add((Block)ModBlocks.POTTED_WILLOW_SAPLING.get(), this.createPotFlowerItemTable((ItemLike)ModBlocks.WILLOW_SAPLING.get()));
         this.dropWoodSet(
            (Block)ModBlocks.WILLOW_LOG.get(),
            (Block)ModBlocks.STRIPPED_WILLOW_LOG.get(),
            (Block)ModBlocks.WILLOW_WOOD.get(),
            (Block)ModBlocks.STRIPPED_WILLOW_WOOD.get(),
            (Block)ModBlocks.WILLOW_PLANKS.get(),
            (Block)ModBlocks.WILLOW_STAIRS.get(),
            (Block)ModBlocks.WILLOW_SLAB.get(),
            (Block)ModBlocks.WILLOW_BUTTON.get(),
            (Block)ModBlocks.WILLOW_PRESSURE_PLATE.get(),
            (Block)ModBlocks.WILLOW_FENCE.get(),
            (Block)ModBlocks.WILLOW_FENCE_GATE.get(),
            (Block)ModBlocks.WILLOW_TRAPDOOR.get(),
            (Block)ModBlocks.WILLOW_DOOR.get(),
            (Block)ModBlocks.WILLOW_SIGN.get(),
            (Block)ModBlocks.WILLOW_WALL_SIGN.get(),
            (Block)ModBlocks.WILLOW_HANGING_SIGN.get(),
            (Block)ModBlocks.WILLOW_HANGING_WALL_SIGN.get()
         );
      }

      protected Iterable<Block> getKnownBlocks() {
         return KNOWN_BLOCKS;
      }

      private Builder cropAge(Block block, IntegerProperty age, int value) {
         return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
            .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(age, value));
      }

      private void dropWoodSet(
         Block log,
         Block strippedLog,
         Block wood,
         Block strippedWood,
         Block planks,
         Block stairs,
         Block slab,
         Block button,
         Block pressurePlate,
         Block fence,
         Block fenceGate,
         Block trapdoor,
         Block door,
         Block sign,
         Block wallSign,
         Block hangingSign,
         Block hangingWallSign
      ) {
         this.dropSelf(log);
         this.dropSelf(strippedLog);
         this.dropSelf(wood);
         this.dropSelf(strippedWood);
         this.dropSelf(planks);
         this.dropSelf(stairs);
         this.add(slab, this.createSlabItemTable(slab));
         this.dropSelf(button);
         this.dropSelf(pressurePlate);
         this.dropSelf(fence);
         this.dropSelf(fenceGate);
         this.dropSelf(trapdoor);
         this.add(door, this.createDoorTable(door));
         this.dropSelf(sign);
         this.dropOther(wallSign, sign);
         this.dropSelf(hangingSign);
         this.dropOther(hangingWallSign, hangingSign);
      }

      private net.minecraft.world.level.storage.loot.LootTable.Builder createMatureSingleCropDrops(Block block, Item crop, Builder mature) {
         RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
         return (net.minecraft.world.level.storage.loot.LootTable.Builder)this.applyExplosionDecay(
            block,
            LootTable.lootTable()
               .withPool(
                  LootPool.lootPool()
                     .add(
                        ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(crop).when(mature))
                           .apply(ApplyBonusCount.addBonusBinomialDistributionCount(enchantments.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                     )
               )
         );
      }
   }
}
