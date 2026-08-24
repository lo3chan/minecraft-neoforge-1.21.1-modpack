package dev.architectury.event.events.common;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootPool.Builder;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

public interface LootEvent {
   Event<LootEvent.ModifyLootTable> MODIFY_LOOT_TABLE = EventFactory.createLoop();

   @NonExtendable
   public interface LootTableModificationContext {
      void addPool(Builder var1);
   }

   @FunctionalInterface
   public interface ModifyLootTable {
      void modifyLootTable(ResourceKey<LootTable> var1, LootEvent.LootTableModificationContext var2, boolean var3);
   }
}
