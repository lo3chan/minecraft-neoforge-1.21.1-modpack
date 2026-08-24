package net.joefoxe.hexerei.world.gen;

import java.util.List;
import java.util.Random;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.FloweringLilyPadBlock;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.block.custom.PickablePlant;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class ModConfiguredFeatures {
   public static final ResourceKey<ConfiguredFeature<?, ?>> SELENITE_GEODE_KEY = registerKey("selenite_geode");
   public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_FLOWERS_KEY = registerKey("swamp_flowers");
   public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWERING_LILYPAD_KEY = registerKey("flowering_lilypad");
   public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_KEY = registerKey("willow");
   public static final ResourceKey<ConfiguredFeature<?, ?>> WITCH_HAZEL_KEY = registerKey("witch_hazel");
   public static final ResourceKey<ConfiguredFeature<?, ?>> MAHOGANY_KEY = registerKey("mahogany");
   public static Random random = new Random();

   public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
      return ResourceKey.create(Registries.CONFIGURED_FEATURE, HexereiUtil.getResource(name));
   }

   private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
      BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration
   ) {
      context.register(key, new ConfiguredFeature(feature, configuration));
   }

   public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
      register(
         context,
         SELENITE_GEODE_KEY,
         Feature.GEODE,
         new GeodeConfiguration(
            new GeodeBlockSettings(
               BlockStateProvider.simple(Blocks.AIR),
               BlockStateProvider.simple((Block)ModBlocks.SELENITE_BLOCK.get()),
               BlockStateProvider.simple((Block)ModBlocks.BUDDING_SELENITE.get()),
               BlockStateProvider.simple(Blocks.CALCITE),
               BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
               List.of(
                  ((AmethystBlock)ModBlocks.SMALL_SELENITE_BUD.get()).defaultBlockState(),
                  ((AmethystBlock)ModBlocks.MEDIUM_SELENITE_BUD.get()).defaultBlockState(),
                  ((AmethystBlock)ModBlocks.LARGE_SELENITE_BUD.get()).defaultBlockState(),
                  ((AmethystBlock)ModBlocks.SELENITE_CLUSTER.get()).defaultBlockState()
               ),
               BlockTags.FEATURES_CANNOT_REPLACE,
               BlockTags.GEODE_INVALID_BLOCKS
            ),
            new GeodeLayerSettings(1.7, 2.2, 3.2, 4.2),
            new GeodeCrackSettings(0.95, 2.0, 2),
            0.35,
            0.083,
            true,
            UniformInt.of(4, 6),
            UniformInt.of(3, 4),
            UniformInt.of(1, 2),
            -16,
            16,
            0.05,
            1
         )
      );
      register(
         context,
         SWAMP_FLOWERS_KEY,
         Feature.SIMPLE_RANDOM_SELECTOR,
         new SimpleRandomFeatureConfiguration(
            HolderSet.direct(
               new Holder[]{
                  PlacementUtils.inlinePlaced(
                     Feature.SIMPLE_BLOCK,
                     new SimpleBlockConfiguration(
                        BlockStateProvider.simple(
                           (BlockState)((PickableDoublePlant)ModBlocks.YELLOW_DOCK_BUSH.get())
                              .defaultBlockState()
                              .setValue(PickableDoublePlant.AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))
                        )
                     ),
                     new PlacementModifier[0]
                  ),
                  PlacementUtils.inlinePlaced(
                     Feature.SIMPLE_BLOCK,
                     new SimpleBlockConfiguration(
                        BlockStateProvider.simple(
                           (BlockState)((PickableDoublePlant)ModBlocks.MUGWORT_BUSH.get())
                              .defaultBlockState()
                              .setValue(PickableDoublePlant.AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))
                        )
                     ),
                     new PlacementModifier[0]
                  ),
                  PlacementUtils.inlinePlaced(
                     Feature.SIMPLE_BLOCK,
                     new SimpleBlockConfiguration(
                        BlockStateProvider.simple(
                           (BlockState)((PickablePlant)ModBlocks.BELLADONNA_PLANT.get())
                              .defaultBlockState()
                              .setValue(PickableDoublePlant.AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))
                        )
                     ),
                     new PlacementModifier[0]
                  ),
                  PlacementUtils.inlinePlaced(
                     Feature.SIMPLE_BLOCK,
                     new SimpleBlockConfiguration(
                        BlockStateProvider.simple(
                           (BlockState)((PickablePlant)ModBlocks.MANDRAKE_PLANT.get())
                              .defaultBlockState()
                              .setValue(PickableDoublePlant.AGE, Math.max(0, Math.min(3, random.nextInt() % 3)))
                        )
                     ),
                     new PlacementModifier[0]
                  )
               }
            )
         )
      );
      register(
         context,
         FLOWERING_LILYPAD_KEY,
         Feature.RANDOM_PATCH,
         new RandomPatchConfiguration(
            10,
            7,
            3,
            PlacementUtils.onlyWhenEmpty(
               Feature.SIMPLE_BLOCK,
               new SimpleBlockConfiguration(BlockStateProvider.simple(((FloweringLilyPadBlock)ModBlocks.LILY_PAD_BLOCK.get()).defaultBlockState()))
            )
         )
      );
      register(context, WILLOW_KEY, (Feature)ModFeatures.WILLOW_TREE.get(), NoneFeatureConfiguration.INSTANCE);
      register(context, WITCH_HAZEL_KEY, (Feature)ModFeatures.WITCH_HAZEL_TREE.get(), NoneFeatureConfiguration.INSTANCE);
      register(context, MAHOGANY_KEY, (Feature)ModFeatures.MAHOGANY_TREE.get(), NoneFeatureConfiguration.INSTANCE);
   }
}
