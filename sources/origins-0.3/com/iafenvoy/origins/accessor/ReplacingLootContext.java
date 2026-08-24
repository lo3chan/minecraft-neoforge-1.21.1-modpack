package com.iafenvoy.origins.accessor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ReplacingLootContext extends LootContextTypeHolder {
   void origins$setReplaced(ResourceKey<LootTable> var1);

   boolean origins$isReplaced(ResourceKey<LootTable> var1);
}
