package jeresources.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries.Holder;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableFetcher {
   private final Holder reloadableServerRegistries;

   public LootTableFetcher() {
      this.reloadableServerRegistries = null;
   }

   public LootTableFetcher(Holder reloadableServerRegistries) {
      this.reloadableServerRegistries = reloadableServerRegistries;
   }

   public LootTable getLootTable(ResourceKey<LootTable> lootTableKey) {
      return this.reloadableServerRegistries == null ? LootTable.EMPTY : this.reloadableServerRegistries.getLootTable(lootTableKey);
   }
}
