package jeresources.entry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import jeresources.collection.TradeList;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractVillagerEntry<T extends AbstractVillager> {
   private final List<TradeList> tradeList = new LinkedList<>();
   protected T entity;

   public AbstractVillagerEntry(Int2ObjectMap<ItemListing[]> itemListings) {
      this.addITradeLists(itemListings);
   }

   public AbstractVillagerEntry() {
   }

   public void addITradeLists(Int2ObjectMap<ItemListing[]> itemListings) {
      for (int i = 1; i < itemListings.size() + 1; i++) {
         ItemListing[] levelList = (ItemListing[])itemListings.get(i);
         TradeList trades = this.tradeList.size() > i ? this.tradeList.get(i) : new TradeList(this);
         trades.addITradeList(levelList);
         this.tradeList.add(trades);
      }
   }

   public TradeList getVillagerTrades(int level) {
      return this.tradeList.size() > level ? this.tradeList.get(level) : new TradeList(this);
   }

   public List<ItemStack> getInputs() {
      List<ItemStack> list = new LinkedList<>();

      for (List<TradeList.Trade> trades : this.tradeList) {
         for (TradeList.Trade trade : trades) {
            list.add(trade.getMinCostA());
            if (!trade.getMinCostB().isEmpty()) {
               list.add(trade.getMinCostB());
            }
         }
      }

      return list;
   }

   public List<ItemStack> getOutputs() {
      List<ItemStack> list = new LinkedList<>();

      for (List<TradeList.Trade> trades : this.tradeList) {
         list.addAll(trades.stream().map(TradeList.Trade::getMinResult).toList());
      }

      return list;
   }

   public int getMaxLevel() {
      return this.tradeList.size();
   }

   public abstract String getName();

   public abstract String getDisplayName();

   public List<Integer> getPossibleLevels(IFocus<ItemStack> focus) {
      List<Integer> levels = new ArrayList<>();

      for (int i = 0; i < this.tradeList.size(); i++) {
         if (this.tradeList.get(i) != null && this.tradeList.get(i).getFocusedList(focus).size() > 0) {
            levels.add(i);
         }
      }

      return levels;
   }

   public abstract T getVillagerEntity();

   public void clearEntity() {
      this.entity = null;
   }

   public abstract List<ItemStack> getPois();

   public abstract boolean hasPois();

   public abstract boolean hasLevels();
}
