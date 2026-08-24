package dev.architectury.event.forge;

import dev.architectury.event.events.common.LootEvent;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootPool.Builder;

final class LootTableModificationContextImpl implements LootEvent.LootTableModificationContext {
   private final LootTable table;
   private final List<LootPool> pools;

   LootTableModificationContextImpl(LootTable table) {
      this.table = table;
      List<LootPool> pools = null;

      try {
         Field field = LootTable.class.getDeclaredField("pools");
         field.setAccessible(true);

         try {
            pools = (List<LootPool>)field.get(table);
         } catch (IllegalAccessException var10) {
            throw new RuntimeException(var10);
         }
      } catch (NoSuchFieldException var11) {
         for (Field field : LootTable.class.getDeclaredFields()) {
            if (field.getType().equals(List.class)) {
               field.setAccessible(true);

               try {
                  pools = (List<LootPool>)field.get(table);
               } catch (IllegalAccessException var9) {
                  throw new RuntimeException(var9);
               }
            }
         }

         if (pools == null) {
            throw new RuntimeException("Unable to find pools field in LootTable!");
         }
      }

      this.pools = pools;
   }

   @Override
   public void addPool(Builder pool) {
      this.pools.add(pool.build());
   }
}
