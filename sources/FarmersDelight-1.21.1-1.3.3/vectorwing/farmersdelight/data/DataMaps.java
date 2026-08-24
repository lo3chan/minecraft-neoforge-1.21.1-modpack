package vectorwing.farmersdelight.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ModTags;

public class DataMaps extends DataMapProvider {
   protected DataMaps(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
      super(packOutput, lookupProvider);
   }

   protected void gather(@NotNull Provider provider) {
      this.builder(NeoForgeDataMaps.FURNACE_FUELS)
         .add(item(ModItems.HALF_TATAMI_MAT.get()), new FurnaceFuel(100), false, new ICondition[0])
         .add(item(ModItems.STRAW.get()), new FurnaceFuel(100), false, new ICondition[0])
         .add(item(ModItems.CUTTING_BOARD.get()), new FurnaceFuel(200), false, new ICondition[0])
         .add(item(ModItems.ROPE.get()), new FurnaceFuel(200), false, new ICondition[0])
         .add(item(ModItems.SAFETY_NET.get()), new FurnaceFuel(200), false, new ICondition[0])
         .add(item(ModItems.FULL_TATAMI_MAT.get()), new FurnaceFuel(200), false, new ICondition[0])
         .add(item(ModItems.CANVAS_RUG.get()), new FurnaceFuel(200), false, new ICondition[0])
         .add(item(ModItems.ROPE_FENCE.get()), new FurnaceFuel(200), false, new ICondition[0])
         .add(item(ModItems.ROPE_FENCE_GATE.get()), new FurnaceFuel(200), false, new ICondition[0])
         .add(item(ModItems.TREE_BARK.get()), new FurnaceFuel(200), false, new ICondition[0])
         .add(item(ModItems.WOODEN_BASKET.get()), new FurnaceFuel(300), false, new ICondition[0])
         .add(item(ModItems.BAMBOO_BASKET.get()), new FurnaceFuel(300), false, new ICondition[0])
         .add(ModTags.Items.CABINETS_WOODEN, new FurnaceFuel(300), false, new ICondition[0])
         .add(item(ModItems.TATAMI.get()), new FurnaceFuel(400), false, new ICondition[0])
         .add(item(ModItems.CANVAS.get()), new FurnaceFuel(400), false, new ICondition[0])
         .add(item(ModItems.STRAW_BALE.get()), new FurnaceFuel(1000), false, new ICondition[0])
         .remove(ModItems.CRIMSON_CABINET.get().builtInRegistryHolder())
         .remove(ModItems.WARPED_CABINET.get().builtInRegistryHolder());
      this.builder(NeoForgeDataMaps.COMPOSTABLES)
         .add(item(ModItems.TREE_BARK.get()), new Compostable(0.3F), false, new ICondition[0])
         .add(item(ModItems.STRAW.get()), new Compostable(0.3F), false, new ICondition[0])
         .add(item(ModItems.CABBAGE_SEEDS.get()), new Compostable(0.3F), false, new ICondition[0])
         .add(item(ModItems.TOMATO_SEEDS.get()), new Compostable(0.3F), false, new ICondition[0])
         .add(item(ModItems.RICE.get()), new Compostable(0.3F), false, new ICondition[0])
         .add(item(ModItems.RICE_PANICLE.get()), new Compostable(0.3F), false, new ICondition[0])
         .add(item(ModItems.SANDY_SHRUB.get()), new Compostable(0.3F), false, new ICondition[0])
         .add(item(ModItems.PUMPKIN_SLICE.get()), new Compostable(0.5F), false, new ICondition[0])
         .add(item(ModItems.CABBAGE_LEAF.get()), new Compostable(0.5F), false, new ICondition[0])
         .add(item(ModItems.KELP_ROLL_SLICE.get()), new Compostable(0.5F), false, new ICondition[0])
         .add(item(ModItems.CABBAGE.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.ONION.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.TOMATO.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.WILD_CABBAGES.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.WILD_ONIONS.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.WILD_TOMATOES.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.WILD_CARROTS.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.WILD_POTATOES.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.WILD_BEETROOTS.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.WILD_RICE.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.PIE_CRUST.get()), new Compostable(0.65F), false, new ICondition[0])
         .add(item(ModItems.RICE_BALE.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.SWEET_BERRY_COOKIE.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.HONEY_COOKIE.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.CAKE_SLICE.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.APPLE_PIE_SLICE.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.CHOCOLATE_PIE_SLICE.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.RAW_PASTA.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.ROTTEN_TOMATO.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.KELP_ROLL.get()), new Compostable(0.85F), false, new ICondition[0])
         .add(item(ModItems.APPLE_PIE.get()), new Compostable(1.0F), false, new ICondition[0])
         .add(item(ModItems.SWEET_BERRY_CHEESECAKE.get()), new Compostable(1.0F), false, new ICondition[0])
         .add(item(ModItems.CHOCOLATE_PIE.get()), new Compostable(1.0F), false, new ICondition[0])
         .add(item(ModItems.DUMPLINGS.get()), new Compostable(1.0F), false, new ICondition[0])
         .add(item(ModItems.STUFFED_PUMPKIN_BLOCK.get()), new Compostable(1.0F), false, new ICondition[0])
         .add(item(ModItems.BROWN_MUSHROOM_COLONY.get()), new Compostable(1.0F), false, new ICondition[0])
         .add(item(ModItems.RED_MUSHROOM_COLONY.get()), new Compostable(1.0F), false, new ICondition[0]);
   }

   private static ResourceKey<Item> item(Item item) {
      return (ResourceKey<Item>)BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
   }
}
