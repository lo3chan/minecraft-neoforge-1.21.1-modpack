package jeresources.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import jeresources.entry.VillagerEntry;
import jeresources.entry.WanderingTraderEntry;
import jeresources.registry.VillagerRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.level.block.state.BlockState;

public class VillagersHelper {
   public static void initRegistry(VillagerRegistry reg) {
      for (VillagerProfession profession : BuiltInRegistries.VILLAGER_PROFESSION) {
         try {
            reg.addVillagerEntry(new VillagerEntry(profession, getTrades(profession)));
         } catch (Exception var5) {
            LogHelper.warn("Failed loading villager {} registered at {}", profession.toString(), profession.name());
            LogHelper.warn("Exception caught when registering villager", var5);
         }
      }

      try {
         reg.addVillagerEntry(new WanderingTraderEntry(getWanderingTrades()));
      } catch (Exception var4) {
         LogHelper.warn("Failed loading wandering trader");
         LogHelper.warn("Exception caught when registering wandering trader", var4);
      }
   }

   private static Int2ObjectMap<ItemListing[]> getTrades(VillagerProfession profession) {
      return VillagerTrades.TRADES.getOrDefault(profession, Int2ObjectMaps.emptyMap());
   }

   private static Int2ObjectMap<ItemListing[]> getWanderingTrades() {
      ItemListing[] allWanderingTrades = VillagerTrades.WANDERING_TRADER_TRADES
         .values()
         .stream()
         .flatMap(x -> Arrays.stream((ItemListing[])x))
         .toArray(ItemListing[]::new);
      return new Int2ObjectOpenHashMap(new int[]{1}, new ItemListing[][]{allWanderingTrades});
   }

   public static Set<BlockState> getPoiBlocks(PoiType poiType) {
      return poiType.matchingStates();
   }

   public static Set<BlockState> getPoiBlocks(Predicate<Holder<PoiType>> heldJobSite) {
      return getPoiBlocks((PoiType)((Reference)BuiltInRegistries.POINT_OF_INTEREST_TYPE.holders().filter(heldJobSite).findFirst().get()).value());
   }
}
