package vectorwing.farmersdelight.data.loot;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootPool.Builder;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction.Source;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;
import vectorwing.farmersdelight.common.block.RicePaniclesBlock;
import vectorwing.farmersdelight.common.block.TatamiMatBlock;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.common.loot.function.CopySkilletFunction;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModDataComponents;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ModTags;

public class FDBlockLoot extends BlockLootSubProvider {
   public FDBlockLoot(Provider holder) {
      super(Set.of(), FeatureFlags.REGISTRY.allFlags(), holder);
   }

   protected void generate() {
      RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      this.dropSelf(ModBlocks.STOVE.get());
      this.dropNamedContainer(ModBlocks.WOODEN_BASKET.get());
      this.dropNamedContainer(ModBlocks.BAMBOO_BASKET.get());
      this.add(
         ModBlocks.COOKING_POT.get(),
         block -> LootTable.lootTable()
            .withPool(
               (Builder)this.applyExplosionCondition(
                  block,
                  LootPool.lootPool()
                     .setRolls(ConstantValue.exactly(1.0F))
                     .add(
                        LootItem.lootTableItem(block)
                           .apply(
                              CopyComponentsFunction.copyComponents(Source.BLOCK_ENTITY)
                                 .include(DataComponents.CUSTOM_NAME)
                                 .include((DataComponentType)ModDataComponents.MEAL.get())
                                 .include((DataComponentType)ModDataComponents.CONTAINER.get())
                           )
                     )
               )
            )
      );
      this.add(
         ModBlocks.SKILLET.get(),
         block -> LootTable.lootTable()
            .withPool((Builder)this.applyExplosionCondition(block, LootPool.lootPool().add(LootItem.lootTableItem(block).apply(CopySkilletFunction.builder()))))
      );
      this.dropSelf(ModBlocks.CUTTING_BOARD.get());
      this.dropOther(ModBlocks.BUDDING_TOMATO_CROP.get(), (ItemLike)ModItems.TOMATO_SEEDS.get());
      this.add(
         ModBlocks.CABBAGE_CROP.get(), this.createCropDrops(ModBlocks.CABBAGE_CROP.get(), ModItems.CABBAGE.get(), ModItems.CABBAGE_SEEDS.get(), registryLookup)
      );
      this.add(ModBlocks.ONION_CROP.get(), this.createSeedlessCropDrops(ModBlocks.ONION_CROP.get(), ModItems.ONION.get(), registryLookup));
      this.dropOther(ModBlocks.RICE_CROP.get(), (ItemLike)ModItems.RICE.get());
      this.add(
         ModBlocks.RICE_CROP_PANICLES.get(),
         block -> LootTable.lootTable()
            .withPool(
               (Builder)this.applyExplosionDecay(
                  block,
                  LootPool.lootPool()
                     .setRolls(ConstantValue.exactly(1.0F))
                     .add(
                        AlternativesEntry.alternatives(
                           new net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder[]{
                              ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(
                                       (ItemLike)ModItems.RICE.get()
                                    )
                                    .when(
                                       LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                          .setProperties(
                                             net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(RicePaniclesBlock.RICE_AGE, 3)
                                          )
                                    ))
                                 .when(MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ModTags.Items.KNIVES))),
                              LootItem.lootTableItem((ItemLike)ModItems.RICE_PANICLE.get())
                                 .when(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                       .setProperties(
                                          net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                             .hasProperty(RicePaniclesBlock.RICE_AGE, 3)
                                       )
                                 )
                           }
                        )
                     )
               )
            )
      );
      this.add(
         (Block)ModBlocks.TOMATO_CROP.get(),
         block -> (net.minecraft.world.level.storage.loot.LootTable.Builder)this.applyExplosionDecay(
            block,
            LootTable.lootTable()
               .withPool(
                  LootPool.lootPool()
                     .setRolls(ConstantValue.exactly(1.0F))
                     .add(LootItem.lootTableItem((ItemLike)ModItems.TOMATO.get()))
                     .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                           .setProperties(
                              net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(TomatoBlock.VINE_AGE, 3)
                           )
                     )
                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                     .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 1))
               )
               .withPool(
                  LootPool.lootPool()
                     .setRolls(ConstantValue.exactly(1.0F))
                     .add(LootItem.lootTableItem((ItemLike)ModItems.TOMATO_SEEDS.get()))
                     .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                           .setProperties(
                              net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(TomatoBlock.ROPELOGGED, false)
                           )
                     )
               )
               .withPool(
                  LootPool.lootPool()
                     .setRolls(ConstantValue.exactly(1.0F))
                     .add(LootItem.lootTableItem((ItemLike)ModItems.ROTTEN_TOMATO.get()))
                     .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                           .setProperties(
                              net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(TomatoBlock.VINE_AGE, 3)
                           )
                     )
                     .when(LootItemRandomChanceCondition.randomChance(0.05F))
               )
         )
      );
      this.add(
         (Block)ModBlocks.TOMATO_CROP_ON_ROPE.get(),
         block -> (net.minecraft.world.level.storage.loot.LootTable.Builder)this.applyExplosionDecay(
            block,
            LootTable.lootTable()
               .withPool(
                  LootPool.lootPool()
                     .setRolls(ConstantValue.exactly(1.0F))
                     .add(LootItem.lootTableItem((ItemLike)ModItems.TOMATO.get()))
                     .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                           .setProperties(
                              net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(TomatoBlock.VINE_AGE, 3)
                           )
                     )
                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                     .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 1))
               )
               .withPool(
                  LootPool.lootPool()
                     .setRolls(ConstantValue.exactly(1.0F))
                     .add(LootItem.lootTableItem((ItemLike)ModItems.ROTTEN_TOMATO.get()))
                     .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                           .setProperties(
                              net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(TomatoBlock.VINE_AGE, 3)
                           )
                     )
                     .when(LootItemRandomChanceCondition.randomChance(0.05F))
               )
         )
      );
      this.add(
         ModBlocks.SANDY_SHRUB.get(),
         block -> LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     AlternativesEntry.alternatives(
                        new net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder[]{
                           LootItem.lootTableItem(block).when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST)),
                           ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BEETROOT_SEEDS)
                                 .when(LootItemRandomChanceCondition.randomChance(0.125F)))
                              .apply(ApplyExplosionDecay.explosionDecay())
                              .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 2))
                        }
                     )
                  )
            )
      );
      this.add(ModBlocks.WILD_BEETROOTS.get(), block -> this.wildCrop(block, Items.BEETROOT, Items.BEETROOT_SEEDS, registryLookup));
      this.add(ModBlocks.WILD_CABBAGES.get(), block -> this.wildCrop(block, ModItems.CABBAGE.get(), ModItems.CABBAGE_SEEDS.get(), registryLookup));
      this.add(ModBlocks.WILD_CARROTS.get(), block -> this.wildCropNoSeeds(block, Items.CARROT, registryLookup));
      this.add(
         ModBlocks.WILD_ONIONS.get(),
         block -> this.wildCropNoSeeds(block, ModItems.ONION.get(), registryLookup)
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.ALLIUM))
                  .when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST).invert())
                  .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
            )
      );
      this.add(ModBlocks.WILD_POTATOES.get(), block -> this.wildCropNoSeeds(block, Items.POTATO, registryLookup));
      this.add(ModBlocks.WILD_TOMATOES.get(), block -> this.wildCrop(block, ModItems.TOMATO.get(), ModItems.TOMATO_SEEDS.get(), registryLookup));
      this.add(
         ModBlocks.WILD_RICE.get(),
         block -> LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .add(
                     AlternativesEntry.alternatives(
                        new net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder[]{
                           LootItem.lootTableItem((ItemLike)ModItems.WILD_RICE.get())
                              .when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST)),
                           LootItem.lootTableItem((ItemLike)ModItems.RICE.get()).when(ExplosionCondition.survivesExplosion())
                        }
                     )
                  )
                  .when(
                     LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(
                           net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                              .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                        )
                  )
                  .when(
                     LocationCheck.checkLocation(
                        net.minecraft.advancements.critereon.LocationPredicate.Builder.location()
                           .setBlock(
                              net.minecraft.advancements.critereon.BlockPredicate.Builder.block()
                                 .of(new Block[]{block})
                                 .setProperties(
                                    net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                       .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
                                 )
                           ),
                        new BlockPos(0, 1, 0)
                     )
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .add(
                     AlternativesEntry.alternatives(
                        new net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder[]{
                           LootItem.lootTableItem((ItemLike)ModItems.WILD_RICE.get())
                              .when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST)),
                           LootItem.lootTableItem((ItemLike)ModItems.RICE.get()).when(ExplosionCondition.survivesExplosion())
                        }
                     )
                  )
                  .when(
                     LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(
                           net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                              .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
                        )
                  )
                  .when(
                     LocationCheck.checkLocation(
                        net.minecraft.advancements.critereon.LocationPredicate.Builder.location()
                           .setBlock(
                              net.minecraft.advancements.critereon.BlockPredicate.Builder.block()
                                 .of(new Block[]{block})
                                 .setProperties(
                                    net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                       .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                                 )
                           ),
                        new BlockPos(0, -1, 0)
                     )
                  )
            )
      );
      this.dropSelf(ModBlocks.CARROT_CRATE.get());
      this.dropSelf(ModBlocks.POTATO_CRATE.get());
      this.dropSelf(ModBlocks.BEETROOT_CRATE.get());
      this.dropSelf(ModBlocks.CABBAGE_CRATE.get());
      this.dropSelf(ModBlocks.TOMATO_CRATE.get());
      this.dropSelf(ModBlocks.ONION_CRATE.get());
      this.dropSelf(ModBlocks.RICE_BALE.get());
      this.dropSelf(ModBlocks.RICE_BAG.get());
      this.dropSelf(ModBlocks.STRAW_BALE.get());
      this.dropSelf(ModBlocks.ROPE.get());
      this.dropSelf(ModBlocks.ROPE_FENCE.get());
      this.dropSelf(ModBlocks.ROPE_FENCE_GATE.get());
      this.dropSelf(ModBlocks.SAFETY_NET.get());
      this.dropSelf(ModBlocks.CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.WHITE_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.ORANGE_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.MAGENTA_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.LIGHT_BLUE_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.YELLOW_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.LIME_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.PINK_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.GRAY_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.LIGHT_GRAY_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.CYAN_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.PURPLE_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.BLUE_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.BROWN_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.GREEN_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.RED_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.BLACK_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.WHITE_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.ORANGE_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.MAGENTA_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.LIGHT_BLUE_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.YELLOW_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.LIME_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.PINK_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.GRAY_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.LIGHT_GRAY_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.CYAN_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.PURPLE_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.BLUE_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.BROWN_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.GREEN_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.RED_HANGING_CANVAS_SIGN.get());
      this.dropSelf(ModBlocks.BLACK_HANGING_CANVAS_SIGN.get());
      this.dropNamedContainer(ModBlocks.OAK_CABINET.get());
      this.dropNamedContainer(ModBlocks.SPRUCE_CABINET.get());
      this.dropNamedContainer(ModBlocks.BIRCH_CABINET.get());
      this.dropNamedContainer(ModBlocks.JUNGLE_CABINET.get());
      this.dropNamedContainer(ModBlocks.ACACIA_CABINET.get());
      this.dropNamedContainer(ModBlocks.DARK_OAK_CABINET.get());
      this.dropNamedContainer(ModBlocks.MANGROVE_CABINET.get());
      this.dropNamedContainer(ModBlocks.BAMBOO_CABINET.get());
      this.dropNamedContainer(ModBlocks.CHERRY_CABINET.get());
      this.dropNamedContainer(ModBlocks.CRIMSON_CABINET.get());
      this.dropNamedContainer(ModBlocks.WARPED_CABINET.get());
      this.dropSelf(ModBlocks.CANVAS_RUG.get());
      this.dropSelf(ModBlocks.TATAMI.get());
      this.add(
         ModBlocks.FULL_TATAMI_MAT.get(),
         block -> LootTable.lootTable()
            .withPool(
               (Builder)this.applyExplosionCondition(
                  block,
                  LootPool.lootPool()
                     .setRolls(ConstantValue.exactly(1.0F))
                     .add(
                        LootItem.lootTableItem(block)
                           .when(
                              LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                 .setProperties(
                                    net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                       .hasProperty(TatamiMatBlock.PART, BedPart.HEAD)
                                 )
                           )
                     )
               )
            )
      );
      this.dropSelf(ModBlocks.HALF_TATAMI_MAT.get());
      this.add(ModBlocks.BROWN_MUSHROOM_COLONY.get(), block -> this.mushroomColony(block, Items.BROWN_MUSHROOM));
      this.add(ModBlocks.RED_MUSHROOM_COLONY.get(), block -> this.mushroomColony(block, Items.RED_MUSHROOM));
      this.dropSelf(ModBlocks.ORGANIC_COMPOST.get());
      this.dropSelf(ModBlocks.RICH_SOIL.get());
      this.dropOther(ModBlocks.RICH_SOIL_FARMLAND.get(), (ItemLike)ModBlocks.RICH_SOIL.get());
      this.add(ModBlocks.APPLE_PIE.get(), LootTable.lootTable());
      this.add(ModBlocks.CHOCOLATE_PIE.get(), LootTable.lootTable());
      this.add(ModBlocks.PUMPKIN_PIE.get(), LootTable.lootTable());
      this.add(ModBlocks.SWEET_BERRY_CHEESECAKE.get(), LootTable.lootTable());
      this.add(ModBlocks.HONEY_GLAZED_HAM_BLOCK.get(), block -> this.platedFoodExtraDrop(block, Items.BONE, 4));
      this.add(ModBlocks.RICE_ROLL_MEDLEY_BLOCK.get(), block -> this.platedFood(block, 8));
      this.add(ModBlocks.ROAST_CHICKEN_BLOCK.get(), block -> this.platedFoodExtraDrop(block, Items.BONE_MEAL, 4));
      this.add(ModBlocks.SHEPHERDS_PIE_BLOCK.get(), block -> this.platedFood(block, 4));
      this.add(ModBlocks.GLEAMING_SALAD_BLOCK.get(), block -> this.platedFood(block, 4));
      this.add(
         ModBlocks.STUFFED_PUMPKIN_BLOCK.get(),
         block -> LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(block))
                  .when(
                     LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, 4))
                  )
            )
      );
   }

   protected void dropNamedContainer(Block block) {
      this.add(block, x$0 -> this.createNameableBlockEntityTable(x$0));
   }

   @NotNull
   protected Iterable<Block> getKnownBlocks() {
      return ModBlocks.BLOCKS.getEntries().stream().<Block>map(DeferredHolder::value).collect(Collectors.toList());
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder mushroomColony(Block block, Item mushroom) {
      return (net.minecraft.world.level.storage.loot.LootTable.Builder)this.applyExplosionDecay(
         block,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     AlternativesEntry.alternatives(
                        new net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder[]{
                           LootItem.lootTableItem(mushroom)
                              .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
                              .when(
                                 LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(
                                       net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                          .hasProperty(MushroomColonyBlock.COLONY_AGE, 0)
                                    )
                              ),
                           LootItem.lootTableItem(mushroom)
                              .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F)))
                              .when(
                                 LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(
                                       net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                          .hasProperty(MushroomColonyBlock.COLONY_AGE, 1)
                                    )
                              ),
                           LootItem.lootTableItem(mushroom)
                              .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
                              .when(
                                 LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(
                                       net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                          .hasProperty(MushroomColonyBlock.COLONY_AGE, 2)
                                    )
                              ),
                           ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(mushroom)
                                 .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5.0F)))
                                 .when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST).invert()))
                              .when(
                                 LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(
                                       net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                          .hasProperty(MushroomColonyBlock.COLONY_AGE, 3)
                                    )
                              ),
                           ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(block)
                                 .when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST)))
                              .when(
                                 LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                    .setProperties(
                                       net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                          .hasProperty(MushroomColonyBlock.COLONY_AGE, 3)
                                    )
                              )
                        }
                     )
                  )
            )
      );
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder createCropDrops(
      Block block, Item cropItem, Item seeds, RegistryLookup<Enchantment> registryLookup
   ) {
      net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder maxAgeCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            block
         )
         .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
      return (net.minecraft.world.level.storage.loot.LootTable.Builder)this.applyExplosionDecay(
         block,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .add(
                     ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(cropItem).when(maxAgeCondition))
                        .otherwise(LootItem.lootTableItem(seeds))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .when(maxAgeCondition)
                  .add(
                     LootItem.lootTableItem(seeds)
                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                  )
            )
      );
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder createSeedlessCropDrops(
      Block block, Item cropItem, RegistryLookup<Enchantment> registryLookup
   ) {
      net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder maxAgeCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            block
         )
         .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
      return (net.minecraft.world.level.storage.loot.LootTable.Builder)this.applyExplosionDecay(
         block,
         LootTable.lootTable()
            .withPool(LootPool.lootPool().add(LootItem.lootTableItem(cropItem)))
            .withPool(
               LootPool.lootPool()
                  .when(maxAgeCondition)
                  .add(
                     LootItem.lootTableItem(cropItem)
                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                  )
            )
      );
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder wildCrop(Block block, Item crop, Item seeds, RegistryLookup<Enchantment> registryLookup) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(crop))
               .when(LootItemRandomChanceCondition.randomChance(0.2F))
               .when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST).invert())
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  AlternativesEntry.alternatives(
                     new net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder[]{
                        LootItem.lootTableItem(block).when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST)),
                        LootItem.lootTableItem(seeds)
                           .apply(ApplyExplosionDecay.explosionDecay())
                           .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 2))
                     }
                  )
               )
         );
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder wildCropNoSeeds(Block block, Item crop, RegistryLookup<Enchantment> registryLookup) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  AlternativesEntry.alternatives(
                     new net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder[]{
                        LootItem.lootTableItem(block).when(CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST)),
                        LootItem.lootTableItem(crop)
                           .apply(ApplyExplosionDecay.explosionDecay())
                           .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE), 2))
                     }
                  )
               )
         );
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder platedFood(Block block, int servings) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(block))
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                     .setProperties(
                        net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, servings)
                     )
               )
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(Items.BOWL))
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                     .setProperties(
                        net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, servings)
                     )
                     .invert()
               )
         );
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder platedFoodExtraDrop(Block block, Item extraDrop, int servings) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(block))
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                     .setProperties(
                        net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, servings)
                     )
               )
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(Items.BOWL))
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                     .setProperties(
                        net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, servings)
                     )
                     .invert()
               )
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(extraDrop))
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                     .setProperties(
                        net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(FeastBlock.SERVINGS, servings)
                     )
                     .invert()
               )
         );
   }
}
