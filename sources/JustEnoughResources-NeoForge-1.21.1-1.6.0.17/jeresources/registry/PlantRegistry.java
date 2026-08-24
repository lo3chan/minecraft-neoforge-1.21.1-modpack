package jeresources.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jeresources.api.drop.PlantDrop;
import jeresources.entry.PlantEntry;
import jeresources.util.MapKeys;
import net.minecraft.world.item.ItemStack;

public class PlantRegistry {
   private Map<String, PlantEntry> registry = new LinkedHashMap<>();
   private static PlantRegistry instance;

   public static PlantRegistry getInstance() {
      return instance == null ? (instance = new PlantRegistry()) : instance;
   }

   public PlantRegistry() {
      this.registerPlant(PlantEntry.registerGrass());
   }

   public boolean registerPlant(PlantEntry entry) {
      String key = MapKeys.getKey(entry.getPlantItemStack());
      if (key != null && !this.contains(key)) {
         this.registry.put(key, entry);
         return true;
      } else {
         return false;
      }
   }

   private boolean contains(String key) {
      return this.registry.containsKey(key);
   }

   public List<PlantEntry> getAllPlants() {
      return new ArrayList<>(this.registry.values());
   }

   public void addDrops(ItemStack itemStack, PlantDrop[] drops) {
      String key = MapKeys.getKey(itemStack);
      if (key == null || this.contains(key)) {
         for (PlantDrop drop : drops) {
            this.registry.get(key).add(drop);
         }
      }
   }

   public void clear() {
      this.registry.clear();
   }
}
