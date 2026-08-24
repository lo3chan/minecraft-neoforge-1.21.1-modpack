package snownee.jade.addon.harvest;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import snownee.jade.Jade;
import snownee.jade.util.CommonProxy;

public class LootTableMineableCollector {
   private final Registry<LootTable> lootRegistry;
   private final ItemStack toolItem;

   public LootTableMineableCollector(Registry<LootTable> lootRegistry, ItemStack toolItem) {
      this.lootRegistry = lootRegistry;
      this.toolItem = toolItem;
   }

   public static List<Block> execute(Registry<LootTable> lootRegistry, ItemStack toolItem) {
      Stopwatch stopwatch = null;
      if (CommonProxy.isDevEnv()) {
         stopwatch = Stopwatch.createStarted();
      }

      LootTableMineableCollector collector = new LootTableMineableCollector(lootRegistry, toolItem);
      List<Block> list = Lists.newArrayList();

      for (Block block : BuiltInRegistries.BLOCK) {
         if (ShearsToolHandler.getInstance().test(block.defaultBlockState()).isEmpty()) {
            LootTable lootTable = (LootTable)lootRegistry.get(block.getLootTable());
            if (collector.doLootTable(lootTable)) {
               list.add(block);
            }
         }
      }

      if (stopwatch != null) {
         Jade.LOGGER.info("LootTableMineableCollector took {}", stopwatch.stop());
      }

      return list;
   }

   private boolean doLootTable(LootTable lootTable) {
      if (lootTable != null && lootTable != LootTable.EMPTY) {
         for (LootPool pool : lootTable.pools) {
            if (this.doLootPool(pool)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean doLootPool(LootPool lootPool) {
      for (LootPoolEntryContainer entry : lootPool.entries) {
         if (this.doLootPoolEntry(entry)) {
            return true;
         }
      }

      return false;
   }

   private boolean doLootPoolEntry(LootPoolEntryContainer entry) {
      if (entry instanceof AlternativesEntry alternativesEntry) {
         for (LootPoolEntryContainer child : alternativesEntry.children) {
            if (this.doLootPoolEntry(child)) {
               return true;
            }
         }

         return false;
      } else if (entry instanceof NestedLootTable nestedLootTable) {
         LootTable lootTable = (LootTable)nestedLootTable.contents.map(this.lootRegistry::get, Function.identity());
         return this.doLootTable(lootTable);
      } else {
         return CommonProxy.isCorrectConditions(entry.conditions, this.toolItem);
      }
   }
}
