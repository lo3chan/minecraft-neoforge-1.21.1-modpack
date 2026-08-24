package jeresources.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jeresources.api.drop.LootDrop;
import jeresources.entry.WorldGenEntry;
import jeresources.util.MapKeys;
import net.minecraft.world.item.ItemStack;

public class WorldGenRegistry {
   private Map<String, WorldGenEntry> worldGenMap = new HashMap<>();
   private static WorldGenRegistry instance;

   public static WorldGenRegistry getInstance() {
      if (instance == null) {
         instance = new WorldGenRegistry();
      }

      return instance;
   }

   private WorldGenRegistry() {
   }

   public void registerEntry(WorldGenEntry entry) {
      if (this.worldGenMap.containsKey(MapKeys.getKey(entry))) {
         WorldGenEntry existing = this.worldGenMap.get(MapKeys.getKey(entry));
         existing.merge(entry);
      } else {
         this.worldGenMap.put(MapKeys.getKey(entry), entry);
      }
   }

   public void addDrops(ItemStack block, LootDrop... drops) {
      this.worldGenMap.entrySet().stream().filter(entry -> {
         String key = MapKeys.getKey(block);
         return key != null && entry.getKey().startsWith(key);
      }).forEach(entry -> entry.getValue().addDrops(drops));
   }

   public List<WorldGenEntry> getWorldGen() {
      return new ArrayList<>(this.worldGenMap.values());
   }

   public void clear() {
      this.worldGenMap.clear();
   }
}
