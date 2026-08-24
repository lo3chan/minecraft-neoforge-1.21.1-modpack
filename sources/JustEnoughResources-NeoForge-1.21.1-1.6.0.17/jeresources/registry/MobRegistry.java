package jeresources.registry;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import jeresources.entry.MobEntry;

public class MobRegistry {
   private Set<MobEntry> registry = new LinkedHashSet<>();
   private static MobRegistry instance;

   public static MobRegistry getInstance() {
      return instance == null ? (instance = new MobRegistry()) : instance;
   }

   private MobRegistry() {
   }

   public boolean registerMob(MobEntry entry) {
      return entry != null && this.registry.add(entry);
   }

   public List<MobEntry> getMobs() {
      return new ArrayList<>(this.registry);
   }

   public void clearEntities() {
      this.registry.forEach(MobEntry::clearEntity);
   }

   public void clear() {
      this.registry.clear();
   }
}
