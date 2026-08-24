package net.mehvahdjukaar.moonlight.api.trades;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.misc.SidedInstance;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ItemListingManager extends SimpleJsonResourceReloadListener {
   private static final SidedInstance<ItemListingManager> INSTANCE = SidedInstance.of(ItemListingManager::new);
   private final Map<EntityType<?>, Set<ModItemListing>> specialTradesAdded = new HashMap<>();
   private final Map<VillagerProfession, Set<ModItemListing>> tradesAdded = new HashMap<>();
   private final Map<EntityType<?>, Int2ObjectArrayMap<Set<ItemListing>>> specialTradesRemoved = new HashMap<>();
   private final Map<VillagerProfession, Int2ObjectArrayMap<Set<ItemListing>>> tradesRemoved = new HashMap<>();
   private final Provider registryAccess;

   @Internal
   public static void init() {
      registerSerializer(Moonlight.res("simple"), SimpleItemListing.CODEC);
      registerSerializer(Moonlight.res("remove_all_non_data"), RemoveNonDataListingListing.CODEC);
      registerSerializer(Moonlight.res("no_op"), NoOpListing.CODEC);
      registerSerializer(Moonlight.res("villager_type_variant"), BiomeVariantItemListing.CODEC);
   }

   public ItemListingManager(Provider provider) {
      super(new Gson(), "moonlight/villager_trade");
      this.registryAccess = provider;
      INSTANCE.set(this.registryAccess, this);
   }

   protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
      this.restoreVanillaState();
      List<Pair<ModItemListing, VillagerProfession>> toAdd = new ArrayList<>();
      List<Pair<ModItemListing, EntityType<?>>> toAddSpecial = new ArrayList<>();
      List<Pair<RemoveNonDataListingListing, VillagerProfession>> toRemove = new ArrayList<>();
      List<Pair<RemoveNonDataListingListing, EntityType<?>>> toRemoveSpecial = new ArrayList<>();
      DynamicOps<JsonElement> ops = ForgeHelper.<JsonElement>conditionalOps(JsonOps.INSTANCE, this.registryAccess, this);

      for (Entry<ResourceLocation, JsonElement> e : jsons.entrySet()) {
         JsonElement json = e.getValue();
         ResourceLocation id = e.getKey();
         if (!id.getPath().contains("/")) {
            Moonlight.LOGGER.error("Invalid villager trade id: {}. Must be in format <profession>/<trade>", id);
         } else {
            ResourceLocation targetId = id.withPath(p -> p.substring(0, p.lastIndexOf(47)));
            Optional<VillagerProfession> profession = BuiltInRegistries.VILLAGER_PROFESSION.getOptional(targetId);
            if (profession.isPresent()) {
               ModItemListing trade = parseOrThrow(json, id, ops).orElse(null);
               if (trade != null && !(trade instanceof NoOpListing)) {
                  if (trade instanceof RemoveNonDataListingListing rl) {
                     toRemove.add(Pair.of(rl, profession.get()));
                  } else {
                     toAdd.add(Pair.of(trade, profession.get()));
                  }
               }
            } else {
               Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(targetId);
               if (entityType.isPresent()) {
                  ModItemListing trade = parseOrThrow(json, id, ops).orElse(null);
                  if (trade != null && !(trade instanceof NoOpListing)) {
                     if (trade instanceof RemoveNonDataListingListing rl) {
                        toRemoveSpecial.add(Pair.of(rl, entityType.get()));
                     } else {
                        toAddSpecial.add(Pair.of(trade, entityType.get()));
                     }
                  }
               } else {
                  Moonlight.LOGGER.warn("Unknown villager type: {}", targetId);
               }
            }
         }
      }

      for (Pair<RemoveNonDataListingListing, VillagerProfession> pair : toRemove) {
         VillagerProfession profession = (VillagerProfession)pair.getSecond();
         RemoveNonDataListingListing listing = (RemoveNonDataListingListing)pair.getFirst();
         Int2ObjectMap<ItemListing[]> tradeMap = getTradeMapForProfession(profession);
         Int2ObjectArrayMap<Set<ItemListing>> removed = this.removeMatchingTrades(listing, tradeMap);
         if (!removed.isEmpty()) {
            this.tradesRemoved.computeIfAbsent(profession, k -> new Int2ObjectArrayMap()).putAll(removed);
         }
      }

      for (Pair<RemoveNonDataListingListing, EntityType<?>> pairx : toRemoveSpecial) {
         EntityType<?> entity = (EntityType<?>)pairx.getSecond();
         if (entity == EntityType.WANDERING_TRADER) {
            RemoveNonDataListingListing listing = (RemoveNonDataListingListing)pairx.getFirst();
            Int2ObjectMap<ItemListing[]> wanderingTraderTrades = VillagerTrades.WANDERING_TRADER_TRADES;
            Int2ObjectArrayMap<Set<ItemListing>> removed = this.removeMatchingTrades(listing, wanderingTraderTrades);
            if (!removed.isEmpty()) {
               this.specialTradesRemoved.computeIfAbsent(entity, k -> new Int2ObjectArrayMap()).putAll(removed);
            }
         }
      }

      for (Pair<ModItemListing, VillagerProfession> pairxx : toAdd) {
         ModItemListing listing = (ModItemListing)pairxx.getFirst();
         VillagerProfession profession = (VillagerProfession)pairxx.getSecond();
         Int2ObjectMap<ItemListing[]> tradeMap = getTradeMapForProfession(profession);
         addTrade(tradeMap, listing, true);
         this.tradesAdded.computeIfAbsent(profession, k -> new HashSet<>()).add(listing);
      }

      for (Pair<ModItemListing, EntityType<?>> pairxx : toAddSpecial) {
         ModItemListing listing = (ModItemListing)pairxx.getFirst();
         EntityType<?> entity = (EntityType<?>)pairxx.getSecond();
         if (entity == EntityType.WANDERING_TRADER) {
            Int2ObjectMap<ItemListing[]> wanderingTraderTrades = VillagerTrades.WANDERING_TRADER_TRADES;
            addTrade(wanderingTraderTrades, listing, true);
         }

         this.specialTradesAdded.computeIfAbsent(entity, k -> new HashSet<>()).add(listing);
      }

      int added = this.specialTradesAdded.values().stream().mapToInt(Set::size).sum() + this.tradesAdded.values().stream().mapToInt(Set::size).sum();
      int removed = this.tradesRemoved.values().stream().mapToInt(map -> map.values().stream().mapToInt(Set::size).sum()).sum()
         + this.specialTradesRemoved.values().stream().mapToInt(map -> map.values().stream().mapToInt(Set::size).sum()).sum();
      if (added > 0) {
         Moonlight.LOGGER.info("Applied {} data villager trades", added);
      }

      if (removed > 0) {
         Moonlight.LOGGER.info("Removed {} data villager trades", removed);
      }
   }

   @NotNull
   private static Int2ObjectMap<ItemListing[]> getTradeMapForProfession(VillagerProfession profession) {
      return VillagerTrades.TRADES.computeIfAbsent(profession, k -> new Int2ObjectArrayMap());
   }

   private static void addTrade(Int2ObjectMap<ItemListing[]> tradeMap, @NotNull ModItemListing listing, boolean add) {
      int level = listing.getLevel();
      ItemListing[] existing = (ItemListing[])tradeMap.computeIfAbsent(level, k -> new ItemListing[0]);
      tradeMap.put(listing.getLevel(), mergeArrays(existing, add, listing));
   }

   private static ItemListing[] mergeArrays(ItemListing[] existing, boolean add, ItemListing... toAdd) {
      ArrayList<ItemListing> list = new ArrayList<>(List.of(existing));
      if (add) {
         list.addAll(List.of(toAdd));
      } else {
         list.removeAll(List.of(toAdd));
      }

      return list.toArray(ItemListing[]::new);
   }

   private Int2ObjectArrayMap<Set<ItemListing>> removeMatchingTrades(RemoveNonDataListingListing removal, Int2ObjectMap<ItemListing[]> originalTrades) {
      Int2ObjectArrayMap<Set<ItemListing>> removedTrades = new Int2ObjectArrayMap();
      Map<Integer, ItemListing[]> updatedTrades = new HashMap<>();
      ObjectIterator var5 = originalTrades.int2ObjectEntrySet().iterator();

      while (var5.hasNext()) {
         it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<ItemListing[]> entry = (it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<ItemListing[]>)var5.next();
         int level = entry.getIntKey();
         ItemListing[] trades = (ItemListing[])entry.getValue();
         List<ItemListing> remaining = new ArrayList<>();
         Set<ItemListing> removedAtLevel = new HashSet<>();

         for (ItemListing trade : trades) {
            if (removal.matches(level, trade)) {
               removedAtLevel.add(trade);
            } else {
               remaining.add(trade);
            }
         }

         if (!removedAtLevel.isEmpty()) {
            removedTrades.put(level, removedAtLevel);
            updatedTrades.put(level, remaining.toArray(ItemListing[]::new));
         }
      }

      originalTrades.putAll(updatedTrades);
      return removedTrades;
   }

   private void restoreVanillaState() {
      for (Entry<VillagerProfession, Set<ModItemListing>> entry : this.tradesAdded.entrySet()) {
         VillagerProfession profession = entry.getKey();
         Set<ModItemListing> listings = entry.getValue();
         Int2ObjectMap<ItemListing[]> tradeMap = getTradeMapForProfession(profession);

         for (ModItemListing listing : listings) {
            int level = listing.getLevel();
            ItemListing[] array = (ItemListing[])tradeMap.get(level);
            if (array != null) {
               addTrade(tradeMap, listing, false);
            }
         }
      }

      for (Entry<EntityType<?>, Set<ModItemListing>> entry : this.specialTradesAdded.entrySet()) {
         EntityType<?> entity = entry.getKey();
         Set<ModItemListing> listings = entry.getValue();
         if (entity == EntityType.WANDERING_TRADER) {
            Int2ObjectMap<ItemListing[]> tradeMap = VillagerTrades.WANDERING_TRADER_TRADES;

            for (ModItemListing listingx : listings) {
               int level = listingx.getLevel();
               ItemListing[] array = (ItemListing[])tradeMap.get(level);
               if (array != null) {
                  addTrade(tradeMap, listingx, false);
               }
            }
         }
      }

      for (Entry<VillagerProfession, Int2ObjectArrayMap<Set<ItemListing>>> entryx : this.tradesRemoved.entrySet()) {
         VillagerProfession profession = entryx.getKey();
         Int2ObjectMap<Set<ItemListing>> removedPerLevel = (Int2ObjectMap<Set<ItemListing>>)entryx.getValue();
         Int2ObjectMap<ItemListing[]> tradeMap = getTradeMapForProfession(profession);
         this.restoreMap(tradeMap, removedPerLevel);
      }

      for (Entry<EntityType<?>, Int2ObjectArrayMap<Set<ItemListing>>> entryx : this.specialTradesRemoved.entrySet()) {
         EntityType<?> entity = entryx.getKey();
         if (entity == EntityType.WANDERING_TRADER) {
            Int2ObjectMap<ItemListing[]> tradeMap = VillagerTrades.WANDERING_TRADER_TRADES;
            Int2ObjectMap<Set<ItemListing>> removedPerLevel = (Int2ObjectMap<Set<ItemListing>>)entryx.getValue();
            this.restoreMap(tradeMap, removedPerLevel);
         }
      }

      this.tradesAdded.clear();
      this.specialTradesAdded.clear();
      this.tradesRemoved.clear();
      this.specialTradesRemoved.clear();
   }

   private void restoreMap(Int2ObjectMap<ItemListing[]> tradeMap, Int2ObjectMap<Set<ItemListing>> removedPerLevel) {
      ObjectIterator var3 = removedPerLevel.int2ObjectEntrySet().iterator();

      while (var3.hasNext()) {
         it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<Set<ItemListing>> levelEntry = (it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<Set<ItemListing>>)var3.next();
         int level = levelEntry.getIntKey();
         Set<ItemListing> removedTrades = (Set<ItemListing>)levelEntry.getValue();
         ItemListing[] currentArray = (ItemListing[])tradeMap.get(level);
         tradeMap.put(level, mergeArrays(currentArray, true, removedTrades.toArray(ItemListing[]::new)));
      }
   }

   private static Optional<? extends ModItemListing> parseOrThrow(JsonElement j, ResourceLocation id, DynamicOps<JsonElement> ops) {
      return (Optional<? extends ModItemListing>)ForgeHelper.conditionalCodec(ModItemListing.CODEC).parse(ops, j).getOrThrow();
   }

   public static List<? extends ItemListing> getVillagerListings(VillagerProfession profession, int level) {
      ItemListing[] array = (ItemListing[])getTradeMapForProfession(profession).get(level);
      return array == null ? List.of() : Arrays.stream(array).toList();
   }

   public static List<? extends ItemListing> getSpecialListings(EntityType<?> entityType, int level, Provider provider) {
      if (entityType == EntityType.WANDERING_TRADER) {
         ItemListing[] array = (ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(level);
         return array == null ? List.of() : Arrays.stream(array).toList();
      } else {
         Set<ModItemListing> special = INSTANCE.get(provider).specialTradesAdded.get(entityType);
         if (special == null) {
            return List.of();
         } else {
            List<ItemListing> listings = new ArrayList<>();

            for (ModItemListing listing : special) {
               if (listing.getLevel() == level) {
                  listings.add(listing);
               }
            }

            return listings;
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static List<? extends ItemListing> getSpecialListings(EntityType<?> entityType, int level) {
      return getSpecialListings(entityType, level, Utils.hackyGetRegistryAccess());
   }

   public static void registerSerializer(ResourceLocation id, MapCodec<? extends ModItemListing> trade) {
      RegHelper.register(id, () -> trade, MoonlightRegistry.VILLAGER_TRADES_REGISTRY.key());
   }

   public static void registerSimple(ResourceLocation id, ItemListing instance, int level) {
      ItemListingManager.SpecialListing specialListing = new ItemListingManager.SpecialListing(instance, level);
      registerSerializer(id, specialListing.getCodec());
   }

   private static class SpecialListing implements ModItemListing {
      private final MapCodec<ModItemListing> codec = MapCodec.unit(this);
      private final ItemListing listing;
      private final int level;

      public SpecialListing(ItemListing listing, int level) {
         this.listing = listing;
         this.level = level;
      }

      @Override
      public MapCodec<? extends ModItemListing> getCodec() {
         return this.codec;
      }

      @Nullable
      public MerchantOffer getOffer(Entity trader, RandomSource random) {
         return this.listing.getOffer(trader, random);
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
