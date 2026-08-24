package jeresources.collection;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import jeresources.entry.AbstractVillagerEntry;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class TradeList extends LinkedList<TradeList.Trade> {
   private static final Random r = new Random();
   private final AbstractVillagerEntry<?> entry;

   public TradeList(AbstractVillagerEntry<?> entry) {
      this.entry = entry;
   }

   public List<ItemStack> getCostAs() {
      return this.stream().map(TradeList.Trade::getMinCostA).collect(Collectors.toList());
   }

   public List<ItemStack> getCostBs() {
      return this.stream().map(TradeList.Trade::getMinCostB).filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
   }

   public List<ItemStack> getResults() {
      return this.stream().map(TradeList.Trade::getMinResult).collect(Collectors.toList());
   }

   public TradeList getSubListSell(ItemStack itemStack) {
      return this.stream().filter(trade -> trade.sellsItem(itemStack)).collect(Collectors.toCollection(() -> new TradeList(this.entry)));
   }

   public TradeList getSubListBuy(ItemStack itemStack) {
      return this.stream().filter(trade -> trade.buysItem(itemStack)).collect(Collectors.toCollection(() -> new TradeList(this.entry)));
   }

   public TradeList getFocusedList(IFocus<ItemStack> focus) {
      if (focus == null) {
         return this;
      } else {
         return switch (focus.getRole()) {
            case INPUT -> this.getSubListBuy((ItemStack)focus.getTypedValue().getIngredient());
            case OUTPUT -> this.getSubListSell((ItemStack)focus.getTypedValue().getIngredient());
            default -> this;
         };
      }
   }

   private void addMerchantRecipe(MerchantOffers merchantOffers, ItemListing itemListing, RandomSource rand) {
      MerchantOffer offer = itemListing.getOffer(this.entry.getVillagerEntity(), rand);
      if (offer != null) {
         merchantOffers.add(offer);
      }
   }

   public void addITradeList(ItemListing[] itemListings) {
      for (ItemListing itemListing : itemListings) {
         MerchantOffers tempList = new MerchantOffers();
         RandomSource rand = RandomSource.create();

         for (int itr = 0; itr < 100; itr++) {
            this.addMerchantRecipe(tempList, itemListing, rand);
         }

         if (tempList.size() == 0) {
            return;
         }

         ItemStack costA = ((MerchantOffer)tempList.get(0)).getCostA();
         ItemStack costB = ((MerchantOffer)tempList.get(0)).getCostB();
         ItemStack result = ((MerchantOffer)tempList.get(0)).getResult();
         int maxCostA;
         int minCostA = maxCostA = costA.getCount();
         int minCostB;
         int maxCostB;
         if (!costB.isEmpty()) {
            minCostB = maxCostB = costB.getCount();
         } else {
            maxCostB = 1;
            minCostB = 1;
         }

         int maxResult;
         int minResult = maxResult = result.getCount();

         for (MerchantOffer merchantRecipe : tempList) {
            if (minCostA > merchantRecipe.getBaseCostA().getCount()) {
               minCostA = merchantRecipe.getCostA().getCount();
            }

            if (!costB.isEmpty() && minCostB > merchantRecipe.getCostB().getCount()) {
               minCostB = merchantRecipe.getCostB().getCount();
            }

            if (minResult > merchantRecipe.getResult().getCount()) {
               minResult = merchantRecipe.getResult().getCount();
            }

            if (maxCostA < merchantRecipe.getCostA().getCount()) {
               maxCostA = merchantRecipe.getCostA().getCount();
            }

            if (!costB.isEmpty() && maxCostB < merchantRecipe.getCostB().getCount()) {
               maxCostB = merchantRecipe.getCostA().getCount();
            }

            if (maxResult < merchantRecipe.getResult().getCount()) {
               maxResult = merchantRecipe.getResult().getCount();
            }
         }

         this.add(new TradeList.Trade(costA, minCostA, maxCostA, costB, minCostB, maxCostB, result, minResult, maxResult));
      }
   }

   public static class Trade {
      private final ItemStack costA;
      private final ItemStack costB;
      private final ItemStack result;
      private final int minCostA;
      private final int minCostB;
      private final int minResult;
      private final int maxCostA;
      private final int maxCostB;
      private final int maxResult;

      Trade(ItemStack costA, int minCostA, int maxCostA, ItemStack costB, int minCostB, int maxCostB, ItemStack result, int minResult, int maxResult) {
         this.costA = costA;
         this.minCostA = minCostA;
         this.maxCostA = maxCostA;
         this.costB = costB;
         this.minCostB = minCostB;
         this.maxCostB = maxCostB;
         this.result = result;
         this.minResult = minResult;
         this.maxResult = maxResult;
      }

      public boolean sellsItem(ItemStack itemStack) {
         return this.result.is(itemStack.getItem());
      }

      public boolean buysItem(ItemStack itemStack) {
         return this.costA.is(itemStack.getItem()) || !this.costB.isEmpty() && this.costB.is(itemStack.getItem());
      }

      public ItemStack getMinCostA() {
         ItemStack minBuyStack = this.costA.copy();
         minBuyStack.setCount(this.minCostA);
         return minBuyStack;
      }

      public ItemStack getMinCostB() {
         if (this.costB == null) {
            return ItemStack.EMPTY;
         } else {
            ItemStack minBuyStack = this.costB.copy();
            minBuyStack.setCount(this.minCostB);
            return minBuyStack;
         }
      }

      public ItemStack getMinResult() {
         ItemStack minSellStack = this.result.copy();
         minSellStack.setCount(this.minResult);
         return minSellStack;
      }

      public ItemStack getMaxCostA() {
         ItemStack maxBuyStack = this.costA.copy();
         maxBuyStack.setCount(this.maxCostA);
         return maxBuyStack;
      }

      public ItemStack getMaxCostB() {
         if (this.costB == null) {
            return ItemStack.EMPTY;
         } else {
            ItemStack maxBuyStack = this.costB.copy();
            maxBuyStack.setCount(this.maxCostB);
            return maxBuyStack;
         }
      }

      public ItemStack getMaxResult() {
         ItemStack maxSellStack = this.result.copy();
         maxSellStack.setCount(this.maxResult);
         return maxSellStack;
      }

      @Override
      public String toString() {
         return "Buy1: " + this.costA + ", Buy2: " + this.costB + ", Sell: " + this.result;
      }
   }
}
