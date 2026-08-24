package dev.architectury.registry.level.entity.trade.forge;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

public class TradeRegistryImpl {
   private static final Map<VillagerProfession, Int2ObjectMap<List<ItemListing>>> TRADES_TO_ADD = new HashMap<>();
   private static final List<ItemListing> WANDERER_TRADER_TRADES_GENERIC = new ArrayList<>();
   private static final List<ItemListing> WANDERER_TRADER_TRADES_RARE = new ArrayList<>();

   public static void registerVillagerTrade0(VillagerProfession profession, int level, ItemListing... trades) {
      Int2ObjectMap<List<ItemListing>> tradesForProfession = TRADES_TO_ADD.computeIfAbsent(profession, $ -> new Int2ObjectOpenHashMap());
      List<ItemListing> tradesForLevel = (List<ItemListing>)tradesForProfession.computeIfAbsent(level, $ -> new ArrayList());
      Collections.addAll(tradesForLevel, trades);
   }

   public static void registerTradeForWanderingTrader(boolean rare, ItemListing... trades) {
      if (rare) {
         Collections.addAll(WANDERER_TRADER_TRADES_RARE, trades);
      } else {
         Collections.addAll(WANDERER_TRADER_TRADES_GENERIC, trades);
      }
   }

   public static void onTradeRegistering(VillagerTradesEvent event) {
      Int2ObjectMap<List<ItemListing>> trades = TRADES_TO_ADD.get(event.getType());
      if (trades != null) {
         ObjectIterator var2 = trades.int2ObjectEntrySet().iterator();

         while (var2.hasNext()) {
            Entry<List<ItemListing>> entry = (Entry<List<ItemListing>>)var2.next();
            ((List)event.getTrades().computeIfAbsent(entry.getIntKey(), $ -> NonNullList.create())).addAll((Collection)entry.getValue());
         }
      }
   }

   public static void onWanderingTradeRegistering(WandererTradesEvent event) {
      if (!WANDERER_TRADER_TRADES_GENERIC.isEmpty()) {
         event.getGenericTrades().addAll(WANDERER_TRADER_TRADES_GENERIC);
      }

      if (!WANDERER_TRADER_TRADES_RARE.isEmpty()) {
         event.getRareTrades().addAll(WANDERER_TRADER_TRADES_RARE);
      }
   }

   static {
      NeoForge.EVENT_BUS.addListener(TradeRegistryImpl::onTradeRegistering);
      NeoForge.EVENT_BUS.addListener(TradeRegistryImpl::onWanderingTradeRegistering);
   }
}
