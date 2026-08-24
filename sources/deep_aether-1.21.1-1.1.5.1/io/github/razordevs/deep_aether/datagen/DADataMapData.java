package io.github.razordevs.deep_aether.datagen;

import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.data.DataMapProvider.Builder;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public class DADataMapData extends DataMapProvider {
   public DADataMapData(PackOutput output, CompletableFuture<Provider> provider) {
      super(output, provider);
   }

   protected void gather() {
      Builder<Compostable, Item> compostables = this.builder(NeoForgeDataMaps.COMPOSTABLES);
      this.addCompost(compostables, ((Block)DABlocks.ROSEROOT_LEAVES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.BLUE_ROSEROOT_LEAVES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.FLOWERING_BLUE_ROSEROOT_LEAVES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.YAGROOT_LEAVES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.CRUDEROOT_LEAVES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.AETHER_MOSS_BLOCK.get()).asItem(), 0.65F);
      this.addCompost(compostables, ((Block)DABlocks.AETHER_MOSS_CARPET.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.CLOUDBLOOM_CARPET.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.ROSEROOT_SAPLING.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.BLUE_ROSEROOT_SAPLING.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.YAGROOT_SAPLING.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.CRUDEROOT_SAPLING.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.CONBERRY_LEAVES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.CONBERRY_SAPLING.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.SUNROOT_LEAVES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.SUNROOT_SAPLING.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.YAGROOT_ROOTS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.YAGROOT_VINE.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.GLOWING_SPORES.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.SUNROOT_HANGER.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.AERGLOW_BLOSSOM_BLOCK.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.MINI_GOLDEN_GRASS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.SHORT_GOLDEN_GRASS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.MEDIUM_GOLDEN_GRASS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.TALL_GOLDEN_GRASS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.RADIANT_ORCHID.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.AERLAVENDER.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.TALL_AERLAVENDER.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.AETHER_CATTAILS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.TALL_AETHER_CATTAILS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.GOLDEN_FLOWER.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.SKY_TULIPS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.IASPOVE.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.ENCHANTED_BLOSSOM.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.ECHAISY.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.LIGHTCAP_MUSHROOMS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.GOLDEN_ASPESS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.FEATHER_GRASS.get()).asItem(), 0.3F);
      this.addCompost(compostables, ((Block)DABlocks.TALL_FEATHER_GRASS.get()).asItem(), 0.3F);
      this.addCompost(compostables, (ItemLike)DAItems.AERGLOW_BLOSSOM.get(), 0.1F);
      this.addCompost(compostables, (ItemLike)DAItems.GOLDEN_BERRIES.get(), 0.2F);
      this.addCompost(compostables, (ItemLike)DAItems.GOLDEN_GRASS_SEEDS.get(), 0.1F);
      this.addCompost(compostables, (ItemLike)DAItems.SQUASH_SEEDS.get(), 0.1F);
      this.addCompost(compostables, (ItemLike)DAItems.CLOUDBLOOM_BOUQUET.get(), 0.1F);
   }

   private void addCompost(Builder<Compostable, Item> map, ItemLike item, float chance) {
      map.add(item.asItem().builtInRegistryHolder(), new Compostable(chance), false, new ICondition[0]);
   }
}
