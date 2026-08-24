package net.astralya.hexalia.event;

import dev.architectury.event.events.common.LootEvent;
import dev.architectury.event.events.common.LootEvent.ModifyLootTable;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

public final class AncientSeedLootEvents {
   private AncientSeedLootEvents() {
   }

   public static void register() {
      LootEvent.MODIFY_LOOT_TABLE
         .register(
            (ModifyLootTable)(key, context, builtin) -> {
               if (builtin && BuiltInLootTables.JUNGLE_TEMPLE.equals(key)) {
                  context.addPool(
                     LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.35F))
                        .add(LootItem.lootTableItem((ItemLike)ModItems.ANCIENT_SEED.get()))
                  );
               }
            }
         );
   }
}
