package tannyjung.tanshugetrees.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TanshugetreesModTabs {
   public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "tanshugetrees");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = REGISTRY.register(
      "tab",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.tanshugetrees.tab"))
         .icon(() -> new ItemStack((ItemLike)TanshugetreesModBlocks.SAPLING_YOKAI.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept(((Block)TanshugetreesModBlocks.CUSTOM_SAPLING.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.WAYPOINT_FLOWER.get()).asItem());
            tabData.accept((ItemLike)TanshugetreesModItems.TREE_SUMMONER_STAFF.get());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_AGATHOS.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_BAOBAB.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_BEAN_STALK.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_BEE_KEEPER.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_CATALYST.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_CHRISTMAS_TREE.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_COCONUT_TREE.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_FALCON.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_GIANT_PUMPKIN.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_HALCYON.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_LEGION.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_MALUS_DOMESTICA.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_MANGROVE.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_OLD_WITCH.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_PALM.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_REDWOOD.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_RUST.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_SKY_ISLAND_CHAIN.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_THE_ASPIRANT.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_WALKING_TREE.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_WENDY.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_WHITE_FAIRY.get()).asItem());
            tabData.accept(((Block)TanshugetreesModBlocks.SAPLING_YOKAI.get()).asItem());
         })
         .build()
   );
}
