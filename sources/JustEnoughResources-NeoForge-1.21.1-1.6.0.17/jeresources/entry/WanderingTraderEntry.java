package jeresources.entry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Collections;
import java.util.List;
import jeresources.compatibility.CompatBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;

public class WanderingTraderEntry extends AbstractVillagerEntry<WanderingTrader> {
   public WanderingTraderEntry(Int2ObjectMap<ItemListing[]> itemListings) {
      super(itemListings);
   }

   @Override
   public String getName() {
      return "wandering_trader";
   }

   @Override
   public String getDisplayName() {
      return "entity.minecraft.wandering_trader";
   }

   public WanderingTrader getVillagerEntity() {
      if (this.entity == null) {
         this.entity = (AbstractVillager)EntityType.WANDERING_TRADER.create(CompatBase.getLevel());

         assert this.entity != null;
      }

      return this.entity;
   }

   @Override
   public List<ItemStack> getPois() {
      return Collections.emptyList();
   }

   @Override
   public boolean hasPois() {
      return false;
   }

   @Override
   public boolean hasLevels() {
      return false;
   }
}
