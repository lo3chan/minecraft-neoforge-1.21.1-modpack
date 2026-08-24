package tannyjung.tanshugetrees.init;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;
import tannyjung.tanshugetrees.block.BlockPlacerBoughCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerBoughInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerBoughOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerBranchCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerBranchInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerBranchOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerFineRootCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerFineRootInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerFineRootOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerLeaves1Block;
import tannyjung.tanshugetrees.block.BlockPlacerLeaves2Block;
import tannyjung.tanshugetrees.block.BlockPlacerLimbCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerLimbInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerLimbOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerSecondaryRootCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerSecondaryRootInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerSecondaryRootOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerSprigCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerSprigInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerSprigOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTaprootCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTaprootInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTaprootOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTertiaryRootCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTertiaryRootInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTertiaryRootOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTrunkCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTrunkInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTrunkOuterBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTwigCoreBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTwigInnerBlock;
import tannyjung.tanshugetrees.block.BlockPlacerTwigOuterBlock;
import tannyjung.tanshugetrees.block.CustomSaplingBlock;
import tannyjung.tanshugetrees.block.SaplingAgathosBlock;
import tannyjung.tanshugetrees.block.SaplingBaobabBlock;
import tannyjung.tanshugetrees.block.SaplingBeanStalkBlock;
import tannyjung.tanshugetrees.block.SaplingBeeKeeperBlock;
import tannyjung.tanshugetrees.block.SaplingCatalystBlock;
import tannyjung.tanshugetrees.block.SaplingChristmasTreeBlock;
import tannyjung.tanshugetrees.block.SaplingCoconutTreeBlock;
import tannyjung.tanshugetrees.block.SaplingFalconBlock;
import tannyjung.tanshugetrees.block.SaplingGiantPumpkinBlock;
import tannyjung.tanshugetrees.block.SaplingHalcyonBlock;
import tannyjung.tanshugetrees.block.SaplingLegionBlock;
import tannyjung.tanshugetrees.block.SaplingMalusDomesticaBlock;
import tannyjung.tanshugetrees.block.SaplingMangroveBlock;
import tannyjung.tanshugetrees.block.SaplingOldWitchBlock;
import tannyjung.tanshugetrees.block.SaplingPalmBlock;
import tannyjung.tanshugetrees.block.SaplingRedwoodBlock;
import tannyjung.tanshugetrees.block.SaplingRustBlock;
import tannyjung.tanshugetrees.block.SaplingSkyIslandChainBlock;
import tannyjung.tanshugetrees.block.SaplingTheAspirantBlock;
import tannyjung.tanshugetrees.block.SaplingWalkingTreeBlock;
import tannyjung.tanshugetrees.block.SaplingWendyBlock;
import tannyjung.tanshugetrees.block.SaplingWhiteFairyBlock;
import tannyjung.tanshugetrees.block.SaplingYokaiBlock;
import tannyjung.tanshugetrees.block.WaypointFlowerBlock;

public class TanshugetreesModBlocks {
   public static final Blocks REGISTRY = DeferredRegister.createBlocks("tanshugetrees");
   public static final DeferredBlock<Block> WAYPOINT_FLOWER = REGISTRY.register("waypoint_flower", WaypointFlowerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TAPROOT_OUTER = REGISTRY.register("block_placer_taproot_outer", BlockPlacerTaprootOuterBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TAPROOT_INNER = REGISTRY.register("block_placer_taproot_inner", BlockPlacerTaprootInnerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TAPROOT_CORE = REGISTRY.register("block_placer_taproot_core", BlockPlacerTaprootCoreBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_SECONDARY_ROOT_CORE = REGISTRY.register(
      "block_placer_secondary_root_core", BlockPlacerSecondaryRootCoreBlock::new
   );
   public static final DeferredBlock<Block> BLOCK_PLACER_SECONDARY_ROOT_INNER = REGISTRY.register(
      "block_placer_secondary_root_inner", BlockPlacerSecondaryRootInnerBlock::new
   );
   public static final DeferredBlock<Block> BLOCK_PLACER_SECONDARY_ROOT_OUTER = REGISTRY.register(
      "block_placer_secondary_root_outer", BlockPlacerSecondaryRootOuterBlock::new
   );
   public static final DeferredBlock<Block> BLOCK_PLACER_TERTIARY_ROOT_CORE = REGISTRY.register(
      "block_placer_tertiary_root_core", BlockPlacerTertiaryRootCoreBlock::new
   );
   public static final DeferredBlock<Block> BLOCK_PLACER_TERTIARY_ROOT_INNER = REGISTRY.register(
      "block_placer_tertiary_root_inner", BlockPlacerTertiaryRootInnerBlock::new
   );
   public static final DeferredBlock<Block> BLOCK_PLACER_TERTIARY_ROOT_OUTER = REGISTRY.register(
      "block_placer_tertiary_root_outer", BlockPlacerTertiaryRootOuterBlock::new
   );
   public static final DeferredBlock<Block> BLOCK_PLACER_FINE_ROOT_CORE = REGISTRY.register("block_placer_fine_root_core", BlockPlacerFineRootCoreBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_FINE_ROOT_INNER = REGISTRY.register("block_placer_fine_root_inner", BlockPlacerFineRootInnerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_FINE_ROOT_OUTER = REGISTRY.register("block_placer_fine_root_outer", BlockPlacerFineRootOuterBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TRUNK_CORE = REGISTRY.register("block_placer_trunk_core", BlockPlacerTrunkCoreBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TRUNK_INNER = REGISTRY.register("block_placer_trunk_inner", BlockPlacerTrunkInnerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TRUNK_OUTER = REGISTRY.register("block_placer_trunk_outer", BlockPlacerTrunkOuterBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_BRANCH_CORE = REGISTRY.register("block_placer_branch_core", BlockPlacerBranchCoreBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_BRANCH_INNER = REGISTRY.register("block_placer_branch_inner", BlockPlacerBranchInnerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_BRANCH_OUTER = REGISTRY.register("block_placer_branch_outer", BlockPlacerBranchOuterBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TWIG_CORE = REGISTRY.register("block_placer_twig_core", BlockPlacerTwigCoreBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TWIG_INNER = REGISTRY.register("block_placer_twig_inner", BlockPlacerTwigInnerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_TWIG_OUTER = REGISTRY.register("block_placer_twig_outer", BlockPlacerTwigOuterBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_LEAVES_2 = REGISTRY.register("block_placer_leaves_2", BlockPlacerLeaves2Block::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_LEAVES_1 = REGISTRY.register("block_placer_leaves_1", BlockPlacerLeaves1Block::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_SPRIG_CORE = REGISTRY.register("block_placer_sprig_core", BlockPlacerSprigCoreBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_SPRIG_INNER = REGISTRY.register("block_placer_sprig_inner", BlockPlacerSprigInnerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_SPRIG_OUTER = REGISTRY.register("block_placer_sprig_outer", BlockPlacerSprigOuterBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_BOUGH_CORE = REGISTRY.register("block_placer_bough_core", BlockPlacerBoughCoreBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_BOUGH_INNER = REGISTRY.register("block_placer_bough_inner", BlockPlacerBoughInnerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_BOUGH_OUTER = REGISTRY.register("block_placer_bough_outer", BlockPlacerBoughOuterBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_LIMB_CORE = REGISTRY.register("block_placer_limb_core", BlockPlacerLimbCoreBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_LIMB_INNER = REGISTRY.register("block_placer_limb_inner", BlockPlacerLimbInnerBlock::new);
   public static final DeferredBlock<Block> BLOCK_PLACER_LIMB_OUTER = REGISTRY.register("block_placer_limb_outer", BlockPlacerLimbOuterBlock::new);
   public static final DeferredBlock<Block> SAPLING_HALCYON = REGISTRY.register("sapling_halcyon", SaplingHalcyonBlock::new);
   public static final DeferredBlock<Block> SAPLING_YOKAI = REGISTRY.register("sapling_yokai", SaplingYokaiBlock::new);
   public static final DeferredBlock<Block> SAPLING_REDWOOD = REGISTRY.register("sapling_redwood", SaplingRedwoodBlock::new);
   public static final DeferredBlock<Block> SAPLING_MALUS_DOMESTICA = REGISTRY.register("sapling_malus_domestica", SaplingMalusDomesticaBlock::new);
   public static final DeferredBlock<Block> SAPLING_WENDY = REGISTRY.register("sapling_wendy", SaplingWendyBlock::new);
   public static final DeferredBlock<Block> SAPLING_MANGROVE = REGISTRY.register("sapling_mangrove", SaplingMangroveBlock::new);
   public static final DeferredBlock<Block> SAPLING_FALCON = REGISTRY.register("sapling_falcon", SaplingFalconBlock::new);
   public static final DeferredBlock<Block> SAPLING_SKY_ISLAND_CHAIN = REGISTRY.register("sapling_sky_island_chain", SaplingSkyIslandChainBlock::new);
   public static final DeferredBlock<Block> SAPLING_CHRISTMAS_TREE = REGISTRY.register("sapling_christmas_tree", SaplingChristmasTreeBlock::new);
   public static final DeferredBlock<Block> SAPLING_BAOBAB = REGISTRY.register("sapling_baobab", SaplingBaobabBlock::new);
   public static final DeferredBlock<Block> SAPLING_LEGION = REGISTRY.register("sapling_legion", SaplingLegionBlock::new);
   public static final DeferredBlock<Block> SAPLING_OLD_WITCH = REGISTRY.register("sapling_old_witch", SaplingOldWitchBlock::new);
   public static final DeferredBlock<Block> SAPLING_GIANT_PUMPKIN = REGISTRY.register("sapling_giant_pumpkin", SaplingGiantPumpkinBlock::new);
   public static final DeferredBlock<Block> SAPLING_RUST = REGISTRY.register("sapling_rust", SaplingRustBlock::new);
   public static final DeferredBlock<Block> SAPLING_THE_ASPIRANT = REGISTRY.register("sapling_the_aspirant", SaplingTheAspirantBlock::new);
   public static final DeferredBlock<Block> SAPLING_WHITE_FAIRY = REGISTRY.register("sapling_white_fairy", SaplingWhiteFairyBlock::new);
   public static final DeferredBlock<Block> SAPLING_AGATHOS = REGISTRY.register("sapling_agathos", SaplingAgathosBlock::new);
   public static final DeferredBlock<Block> SAPLING_PALM = REGISTRY.register("sapling_palm", SaplingPalmBlock::new);
   public static final DeferredBlock<Block> SAPLING_COCONUT_TREE = REGISTRY.register("sapling_coconut_tree", SaplingCoconutTreeBlock::new);
   public static final DeferredBlock<Block> SAPLING_BEAN_STALK = REGISTRY.register("sapling_bean_stalk", SaplingBeanStalkBlock::new);
   public static final DeferredBlock<Block> SAPLING_BEE_KEEPER = REGISTRY.register("sapling_bee_keeper", SaplingBeeKeeperBlock::new);
   public static final DeferredBlock<Block> SAPLING_WALKING_TREE = REGISTRY.register("sapling_walking_tree", SaplingWalkingTreeBlock::new);
   public static final DeferredBlock<Block> SAPLING_CATALYST = REGISTRY.register("sapling_catalyst", SaplingCatalystBlock::new);
   public static final DeferredBlock<Block> CUSTOM_SAPLING = REGISTRY.register("custom_sapling", CustomSaplingBlock::new);
}
