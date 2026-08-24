package jeresources.registry;

import java.util.LinkedList;
import java.util.List;
import jeresources.entry.AbstractVillagerEntry;

public class VillagerRegistry {
   private static VillagerRegistry instance;
   private List<AbstractVillagerEntry> villagers = new LinkedList<>();

   public static VillagerRegistry getInstance() {
      if (instance == null) {
         instance = new VillagerRegistry();
      }

      return instance;
   }

   private VillagerRegistry() {
   }

   public void addVillagerEntry(AbstractVillagerEntry<?> entry) {
      if (entry.getVillagerTrades(0).size() > 0) {
         this.villagers.add(entry);
      }
   }

   public List<AbstractVillagerEntry> getVillagers() {
      return this.villagers;
   }

   public void clearEntities() {
      this.villagers.forEach(AbstractVillagerEntry::clearEntity);
   }

   public void clear() {
      this.villagers.clear();
   }
}
