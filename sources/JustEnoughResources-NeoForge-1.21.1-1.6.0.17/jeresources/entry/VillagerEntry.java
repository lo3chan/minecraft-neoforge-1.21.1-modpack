package jeresources.entry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.stream.Collectors;
import jeresources.compatibility.CompatBase;
import jeresources.util.VillagersHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;

public class VillagerEntry extends AbstractVillagerEntry<Villager> {
   private final VillagerProfession profession;

   public VillagerEntry(VillagerProfession profession, Int2ObjectMap<ItemListing[]> itemListings) {
      this.profession = profession;
      this.addITradeLists(itemListings);
   }

   @Override
   public String getName() {
      return this.profession.toString();
   }

   @Override
   public String getDisplayName() {
      return "entity.minecraft.villager." + this.profession.toString();
   }

   public VillagerProfession getProfession() {
      return this.profession;
   }

   public Villager getVillagerEntity() {
      if (this.entity == null) {
         this.entity = (AbstractVillager)EntityType.VILLAGER.create(CompatBase.getLevel());

         assert this.entity != null;

         this.entity.setVillagerData(this.entity.getVillagerData().setProfession(this.profession));
         this.entity.tick();
      }

      return this.entity;
   }

   @Override
   public List<ItemStack> getPois() {
      return VillagersHelper.getPoiBlocks(this.profession.heldJobSite())
         .stream()
         .map(blockstate -> new ItemStack(blockstate.getBlock()))
         .collect(Collectors.toList());
   }

   @Override
   public boolean hasPois() {
      return !VillagersHelper.getPoiBlocks(this.profession.heldJobSite()).isEmpty();
   }

   @Override
   public boolean hasLevels() {
      return true;
   }
}
