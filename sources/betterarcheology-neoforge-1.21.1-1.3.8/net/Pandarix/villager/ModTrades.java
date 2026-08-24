package net.Pandarix.villager;

import dev.architectury.registry.level.entity.trade.SimpleTrade;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.Pandarix.BACommon;
import net.Pandarix.block.ModBlocks;
import net.Pandarix.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.npc.VillagerTrades.TreasureMapForEmeralds;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;

public class ModTrades {
   public static void register() {
      BACommon.LOGGER.info("Registering {} for {}", "Villager Trades", "Better Archeology");
   }

   static {
      Int2ObjectMap<ItemListing[]> archeologistTrades = new Int2ObjectOpenHashMap();
      archeologistTrades.put(
         1,
         new ItemListing[]{
            new SimpleTrade(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack((ItemLike)ModBlocks.ROTTEN_PLANKS.get(), 6), 10, 2, 0.02F),
            new SimpleTrade(new ItemCost(Items.EMERALD, 3), Optional.empty(), new ItemStack(Items.BRUSH), 4, 5, 0.02F),
            new SimpleTrade(new ItemCost(Items.BONE, 16), Optional.empty(), new ItemStack(Items.EMERALD), 16, 20, 0.02F)
         }
      );
      archeologistTrades.put(
         2,
         new ItemListing[]{
            new SimpleTrade(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(Blocks.MUD_BRICKS), 14, 5, 0.02F),
            new SimpleTrade(new ItemCost(Items.EMERALD, 3), Optional.empty(), new ItemStack(Items.LANTERN), 12, 10, 0.02F)
         }
      );
      archeologistTrades.put(
         3,
         new ItemListing[]{
            new SimpleTrade(new ItemCost(Items.EMERALD, 4), Optional.empty(), new ItemStack(Blocks.COBWEB, 6), 10, 5, 0.02F),
            new SimpleTrade(new ItemCost(Items.EMERALD, 6), Optional.empty(), new ItemStack((ItemLike)ModItems.IRON_BRUSH.get()), 4, 10, 0.03F)
         }
      );
      archeologistTrades.put(
         4,
         new ItemListing[]{
            new SimpleTrade(new ItemCost(Items.EMERALD, 4), Optional.empty(), new ItemStack((ItemLike)ModBlocks.VASE_CREEPER.get(), 1), 8, 10, 0.025F),
            new SimpleTrade(new ItemCost(Items.EMERALD, 4), Optional.empty(), new ItemStack((ItemLike)ModBlocks.VASE.get(), 1), 8, 10, 0.025F),
            new SimpleTrade(new ItemCost(Items.EMERALD, 4), Optional.empty(), new ItemStack((ItemLike)ModBlocks.VASE_GREEN.get(), 1), 8, 10, 0.025F),
            new SimpleTrade(new ItemCost(Items.EMERALD, 8), Optional.empty(), new ItemStack(Items.SPYGLASS), 8, 10, 0.02F),
            new SimpleTrade(new ItemCost(Items.EMERALD, 8), Optional.empty(), new ItemStack(ModItems.BOMB_ITEM, 3), 6, 10, 0.05F)
         }
      );
      archeologistTrades.put(
         5,
         new ItemListing[]{
            new SimpleTrade(new ItemCost(Items.EMERALD, 13), Optional.empty(), new ItemStack(ModItems.DIAMOND_BRUSH), 4, 10, 0.03F),
            new SimpleTrade(new ItemCost(Items.EMERALD, 24), Optional.empty(), new ItemStack(ModItems.ARTIFACT_SHARDS), 3, 30, 0.1F),
            new TreasureMapForEmeralds(
               24,
               TagKey.create(Registries.STRUCTURE, BACommon.createResource("on_catacombs_explorer_map")),
               "filled_map.catacombs",
               MapDecorationTypes.WOODLAND_MANSION,
               12,
               5
            )
         }
      );
      VillagerTrades.TRADES.put(ModVillagers.ARCHEOLOGIST.get(), archeologistTrades);
   }
}
