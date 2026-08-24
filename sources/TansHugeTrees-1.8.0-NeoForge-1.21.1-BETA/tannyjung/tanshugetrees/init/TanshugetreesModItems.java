package tannyjung.tanshugetrees.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import tannyjung.tanshugetrees.item.TreeSummonerStaffItem;

public class TanshugetreesModItems {
   public static final Items REGISTRY = DeferredRegister.createItems("tanshugetrees");
   public static final DeferredItem<Item> WAYPOINT_FLOWER = block(TanshugetreesModBlocks.WAYPOINT_FLOWER);
   public static final DeferredItem<Item> BLOCK_PLACER_TAPROOT_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_TAPROOT_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_TAPROOT_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_TAPROOT_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_TAPROOT_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_TAPROOT_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_SECONDARY_ROOT_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_SECONDARY_ROOT_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_SECONDARY_ROOT_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_SECONDARY_ROOT_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_SECONDARY_ROOT_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_SECONDARY_ROOT_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_TERTIARY_ROOT_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_TERTIARY_ROOT_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_TERTIARY_ROOT_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_TERTIARY_ROOT_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_TERTIARY_ROOT_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_TERTIARY_ROOT_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_FINE_ROOT_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_FINE_ROOT_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_FINE_ROOT_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_FINE_ROOT_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_FINE_ROOT_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_FINE_ROOT_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_TRUNK_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_TRUNK_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_TRUNK_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_TRUNK_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_TRUNK_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_TRUNK_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_BRANCH_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_BRANCH_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_BRANCH_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_BRANCH_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_BRANCH_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_BRANCH_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_TWIG_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_TWIG_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_TWIG_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_TWIG_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_TWIG_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_TWIG_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_LEAVES_2 = block(TanshugetreesModBlocks.BLOCK_PLACER_LEAVES_2);
   public static final DeferredItem<Item> BLOCK_PLACER_LEAVES_1 = block(TanshugetreesModBlocks.BLOCK_PLACER_LEAVES_1);
   public static final DeferredItem<Item> BLOCK_PLACER_SPRIG_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_SPRIG_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_SPRIG_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_SPRIG_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_SPRIG_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_SPRIG_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_BOUGH_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_BOUGH_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_BOUGH_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_BOUGH_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_BOUGH_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_BOUGH_OUTER);
   public static final DeferredItem<Item> BLOCK_PLACER_LIMB_CORE = block(TanshugetreesModBlocks.BLOCK_PLACER_LIMB_CORE);
   public static final DeferredItem<Item> BLOCK_PLACER_LIMB_INNER = block(TanshugetreesModBlocks.BLOCK_PLACER_LIMB_INNER);
   public static final DeferredItem<Item> BLOCK_PLACER_LIMB_OUTER = block(TanshugetreesModBlocks.BLOCK_PLACER_LIMB_OUTER);
   public static final DeferredItem<Item> SAPLING_HALCYON = block(TanshugetreesModBlocks.SAPLING_HALCYON);
   public static final DeferredItem<Item> SAPLING_YOKAI = block(TanshugetreesModBlocks.SAPLING_YOKAI);
   public static final DeferredItem<Item> SAPLING_REDWOOD = block(TanshugetreesModBlocks.SAPLING_REDWOOD);
   public static final DeferredItem<Item> SAPLING_MALUS_DOMESTICA = block(TanshugetreesModBlocks.SAPLING_MALUS_DOMESTICA);
   public static final DeferredItem<Item> SAPLING_WENDY = block(TanshugetreesModBlocks.SAPLING_WENDY);
   public static final DeferredItem<Item> SAPLING_MANGROVE = block(TanshugetreesModBlocks.SAPLING_MANGROVE);
   public static final DeferredItem<Item> SAPLING_FALCON = block(TanshugetreesModBlocks.SAPLING_FALCON);
   public static final DeferredItem<Item> SAPLING_SKY_ISLAND_CHAIN = block(TanshugetreesModBlocks.SAPLING_SKY_ISLAND_CHAIN);
   public static final DeferredItem<Item> TREE_SUMMONER_STAFF = REGISTRY.register("tree_summoner_staff", TreeSummonerStaffItem::new);
   public static final DeferredItem<Item> SAPLING_CHRISTMAS_TREE = block(TanshugetreesModBlocks.SAPLING_CHRISTMAS_TREE);
   public static final DeferredItem<Item> SAPLING_BAOBAB = block(TanshugetreesModBlocks.SAPLING_BAOBAB);
   public static final DeferredItem<Item> SAPLING_LEGION = block(TanshugetreesModBlocks.SAPLING_LEGION);
   public static final DeferredItem<Item> SAPLING_OLD_WITCH = block(TanshugetreesModBlocks.SAPLING_OLD_WITCH);
   public static final DeferredItem<Item> SAPLING_GIANT_PUMPKIN = block(TanshugetreesModBlocks.SAPLING_GIANT_PUMPKIN);
   public static final DeferredItem<Item> SAPLING_RUST = block(TanshugetreesModBlocks.SAPLING_RUST);
   public static final DeferredItem<Item> SAPLING_THE_ASPIRANT = block(TanshugetreesModBlocks.SAPLING_THE_ASPIRANT);
   public static final DeferredItem<Item> SAPLING_WHITE_FAIRY = block(TanshugetreesModBlocks.SAPLING_WHITE_FAIRY);
   public static final DeferredItem<Item> SAPLING_AGATHOS = block(TanshugetreesModBlocks.SAPLING_AGATHOS);
   public static final DeferredItem<Item> SAPLING_PALM = block(TanshugetreesModBlocks.SAPLING_PALM);
   public static final DeferredItem<Item> SAPLING_COCONUT_TREE = block(TanshugetreesModBlocks.SAPLING_COCONUT_TREE);
   public static final DeferredItem<Item> SAPLING_BEAN_STALK = block(TanshugetreesModBlocks.SAPLING_BEAN_STALK);
   public static final DeferredItem<Item> SAPLING_BEE_KEEPER = block(TanshugetreesModBlocks.SAPLING_BEE_KEEPER);
   public static final DeferredItem<Item> SAPLING_WALKING_TREE = block(TanshugetreesModBlocks.SAPLING_WALKING_TREE);
   public static final DeferredItem<Item> SAPLING_CATALYST = block(TanshugetreesModBlocks.SAPLING_CATALYST);
   public static final DeferredItem<Item> CUSTOM_SAPLING = block(TanshugetreesModBlocks.CUSTOM_SAPLING);

   private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
      return block(block, new Properties());
   }

   private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Properties properties) {
      return REGISTRY.register(block.getId().getPath(), () -> new BlockItem((Block)block.get(), properties));
   }
}
