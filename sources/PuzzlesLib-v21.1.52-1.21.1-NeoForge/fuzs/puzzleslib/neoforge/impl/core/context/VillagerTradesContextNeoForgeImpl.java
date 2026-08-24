package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.core.v1.context.VillagerTradesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

public final class VillagerTradesContextNeoForgeImpl implements VillagerTradesContext {
   private final List<VillagerTradesContextNeoForgeImpl.VillagerTrade> villagerTrades = new ArrayList<>();
   private final List<VillagerTradesContextNeoForgeImpl.WanderingTrade> wanderingTrades = new ArrayList<>();

   @Override
   public void registerVillagerTrades(VillagerProfession profession, VillagerTradesContext.VillagerLevel level, Consumer<List<ItemListing>> factories) {
      Objects.requireNonNull(profession, "profession is null");
      Objects.requireNonNull(level, "level is null");
      Objects.requireNonNull(factories, "factories is null");
      if (this.villagerTrades.isEmpty()) {
         NeoForge.EVENT_BUS.addListener(event -> this.villagerTrades.forEach(villagerTrade -> villagerTrade.registerTrades(event)));
      }

      this.villagerTrades.add(new VillagerTradesContextNeoForgeImpl.VillagerTrade(profession, level, factories));
   }

   @Override
   public void registerWanderingTrades(VillagerTradesContext.WanderingTradesPool pool, ItemListing... itemListings) {
      Objects.requireNonNull(pool, "pool is null");
      Objects.requireNonNull(itemListings, "item listings is null");
      Preconditions.checkArgument(itemListings.length > 0, "item listings is empty");
      if (this.wanderingTrades.isEmpty()) {
         NeoForge.EVENT_BUS.addListener(event -> this.wanderingTrades.forEach(wanderingTrade -> wanderingTrade.registerTrades(event)));
      }

      this.wanderingTrades.add(new VillagerTradesContextNeoForgeImpl.WanderingTrade(pool, ImmutableList.copyOf(itemListings)));
   }

   record VillagerTrade(VillagerProfession profession, VillagerTradesContext.VillagerLevel level, Consumer<List<ItemListing>> factories) {
      public void registerTrades(VillagerTradesEvent event) {
         if (event.getType().equals(this.profession)) {
            List<ItemListing> itemListings = (List<ItemListing>)event.getTrades().get(this.level.getLevel());
            if (itemListings != null) {
               this.factories.accept(itemListings);
            }
         }
      }
   }

   record WanderingTrade(VillagerTradesContext.WanderingTradesPool pool, List<ItemListing> itemListings) {
      public void registerTrades(WandererTradesEvent event) {
         this.getTradesPool(event, this.pool).addAll(this.itemListings);
      }

      List<ItemListing> getTradesPool(WandererTradesEvent event, VillagerTradesContext.WanderingTradesPool pool) {
         return switch (pool) {
            case PURCHASES, COMMON_SALES -> event.getGenericTrades();
            case SPECIAL_SALES -> event.getRareTrades();
         };
      }
   }
}
