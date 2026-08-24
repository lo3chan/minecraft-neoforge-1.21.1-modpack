package at.petrak.paucal.api.datagen;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public abstract class PaucalLootTableSubProvider implements LootTableSubProvider {
   protected final String modid;

   protected PaucalLootTableSubProvider(String modid) {
      this.modid = modid;
   }

   public void generate(BiConsumer<ResourceKey<LootTable>, Builder> register) {
      HashMap<Block, Builder> blockTables = new HashMap<>();
      HashMap<ResourceKey<LootTable>, Builder> lootTables = new HashMap<>();
      this.makeLootTables(blockTables, lootTables);

      for (Entry<Block, Builder> entry : blockTables.entrySet()) {
         register.accept(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK));
      }

      lootTables.forEach(register);
   }

   protected abstract void makeLootTables(Map<Block, Builder> var1, Map<ResourceKey<LootTable>, Builder> var2);

   protected net.minecraft.world.level.storage.loot.LootPool.Builder dropThisPool(ItemLike item, int count) {
      return this.dropThisPool(item, ConstantValue.exactly(count));
   }

   protected net.minecraft.world.level.storage.loot.LootPool.Builder dropThisPool(ItemLike item, NumberProvider count) {
      return LootPool.lootPool().setRolls(count).add(LootItem.lootTableItem(item));
   }

   @SafeVarargs
   protected final void dropSelf(Map<Block, Builder> lootTables, Supplier<? extends Block>... blocks) {
      for (Supplier<? extends Block> blockSupp : blocks) {
         Block block = blockSupp.get();
         this.dropSelf(block, lootTables);
      }
   }

   protected void dropSelf(Map<Block, Builder> lootTables, Block... blocks) {
      for (Block block : blocks) {
         this.dropSelf(block, lootTables);
      }
   }

   protected void dropSelf(Block block, Map<Block, Builder> lootTables) {
      Builder table = LootTable.lootTable().withPool(this.dropThisPool(block, 1));
      lootTables.put(block, table);
   }

   protected void dropThis(Block block, ItemLike drop, Map<Block, Builder> lootTables) {
      Builder table = LootTable.lootTable().withPool(this.dropThisPool(drop, 1));
      lootTables.put(block, table);
   }

   protected void dropThis(Block block, ItemLike drop, NumberProvider count, Map<Block, Builder> lootTables) {
      Builder table = LootTable.lootTable().withPool(this.dropThisPool(drop, count));
      lootTables.put(block, table);
   }
}
