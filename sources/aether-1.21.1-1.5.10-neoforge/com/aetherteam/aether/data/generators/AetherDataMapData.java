package com.aetherteam.aether.data.generators;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.aether.item.AetherItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.data.DataMapProvider.Builder;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public class AetherDataMapData extends DataMapProvider {
   public AetherDataMapData(PackOutput output, CompletableFuture<Provider> provider) {
      super(output, provider);
   }

   protected void gather() {
      Builder<Compostable, Item> compostables = this.builder(NeoForgeDataMaps.COMPOSTABLES);
      this.addCompost(compostables, AetherBlocks.SKYROOT_LEAVES, 0.3F);
      this.addCompost(compostables, AetherBlocks.SKYROOT_SAPLING, 0.3F);
      this.addCompost(compostables, AetherBlocks.GOLDEN_OAK_LEAVES, 0.3F);
      this.addCompost(compostables, AetherBlocks.GOLDEN_OAK_SAPLING, 0.3F);
      this.addCompost(compostables, AetherBlocks.CRYSTAL_LEAVES, 0.3F);
      this.addCompost(compostables, AetherBlocks.CRYSTAL_FRUIT_LEAVES, 0.3F);
      this.addCompost(compostables, AetherBlocks.HOLIDAY_LEAVES, 0.3F);
      this.addCompost(compostables, AetherBlocks.DECORATED_HOLIDAY_LEAVES, 0.3F);
      this.addCompost(compostables, AetherItems.BLUE_BERRY, 0.3F);
      this.addCompost(compostables, AetherItems.ENCHANTED_BERRY, 0.5F);
      this.addCompost(compostables, AetherBlocks.BERRY_BUSH, 0.5F);
      this.addCompost(compostables, AetherBlocks.BERRY_BUSH_STEM, 0.5F);
      this.addCompost(compostables, AetherBlocks.WHITE_FLOWER, 0.65F);
      this.addCompost(compostables, AetherBlocks.PURPLE_FLOWER, 0.65F);
      this.addCompost(compostables, AetherItems.WHITE_APPLE, 0.65F);
      Builder<FurnaceFuel, Item> fuels = this.builder(NeoForgeDataMaps.FURNACE_FUELS);
      fuels.add(AetherBlocks.AMBROSIUM_BLOCK.toStack().getItemHolder(), new FurnaceFuel(16000), false, new ICondition[0]);
      fuels.add(AetherItems.AMBROSIUM_SHARD, new FurnaceFuel(1600), false, new ICondition[0]);
      fuels.add(AetherBlocks.SKYROOT_PLANKS.toStack().getItemHolder(), new FurnaceFuel(300), false, new ICondition[0]);
      fuels.add(AetherBlocks.SKYROOT_BOOKSHELF.toStack().getItemHolder(), new FurnaceFuel(300), false, new ICondition[0]);
      fuels.add(AetherItems.SKYROOT_SWORD, new FurnaceFuel(200), false, new ICondition[0]);
      fuels.add(AetherItems.SKYROOT_PICKAXE, new FurnaceFuel(200), false, new ICondition[0]);
      fuels.add(AetherItems.SKYROOT_AXE, new FurnaceFuel(200), false, new ICondition[0]);
      fuels.add(AetherItems.SKYROOT_SHOVEL, new FurnaceFuel(200), false, new ICondition[0]);
      fuels.add(AetherItems.SKYROOT_HOE, new FurnaceFuel(200), false, new ICondition[0]);
      fuels.add(AetherItems.SKYROOT_BUCKET, new FurnaceFuel(200), false, new ICondition[0]);
      fuels.add(AetherItems.SKYROOT_STICK, new FurnaceFuel(100), false, new ICondition[0]);
      Builder<FurnaceFuel, Item> altar = this.builder(AetherDataMaps.ALTAR_FUEL);
      altar.add(AetherItems.AMBROSIUM_SHARD, new FurnaceFuel(250), false, new ICondition[0]);
      altar.add(AetherBlocks.AMBROSIUM_BLOCK.toStack().getItemHolder(), new FurnaceFuel(2500), false, new ICondition[0]);
      Builder<FurnaceFuel, Item> freezer = this.builder(AetherDataMaps.FREEZER_FUEL);
      freezer.add(AetherBlocks.ICESTONE.toStack().getItemHolder(), new FurnaceFuel(400), false, new ICondition[0]);
      freezer.add(AetherBlocks.ICESTONE_SLAB.toStack().getItemHolder(), new FurnaceFuel(200), false, new ICondition[0]);
      freezer.add(AetherBlocks.ICESTONE_STAIRS.toStack().getItemHolder(), new FurnaceFuel(400), false, new ICondition[0]);
      freezer.add(AetherBlocks.ICESTONE_WALL.toStack().getItemHolder(), new FurnaceFuel(400), false, new ICondition[0]);
      Builder<FurnaceFuel, Item> incubator = this.builder(AetherDataMaps.INCUBATOR_FUEL);
      incubator.add(AetherBlocks.AMBROSIUM_TORCH.toStack().getItemHolder(), new FurnaceFuel(500), false, new ICondition[0]);
   }

   private void addCompost(Builder<Compostable, Item> map, ItemLike item, float chance) {
      map.add(new ItemStack(item).getItemHolder(), new Compostable(chance), false, new ICondition[0]);
   }
}
