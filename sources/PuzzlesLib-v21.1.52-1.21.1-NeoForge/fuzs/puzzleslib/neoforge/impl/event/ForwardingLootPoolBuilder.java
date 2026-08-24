package fuzs.puzzleslib.neoforge.impl.event;

import java.util.ArrayList;
import net.minecraft.Util;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public final class ForwardingLootPoolBuilder extends net.minecraft.world.level.storage.loot.LootPool.Builder {
   private final LootPool lootPool;

   public ForwardingLootPoolBuilder(LootPool lootPool) {
      this.lootPool = lootPool;
   }

   public net.minecraft.world.level.storage.loot.LootPool.Builder setRolls(NumberProvider rolls) {
      this.lootPool.rolls = rolls;
      return this;
   }

   public net.minecraft.world.level.storage.loot.LootPool.Builder setBonusRolls(NumberProvider bonusRolls) {
      this.lootPool.bonusRolls = bonusRolls;
      return this;
   }

   public net.minecraft.world.level.storage.loot.LootPool.Builder add(Builder<?> entriesBuilder) {
      if (!(this.lootPool.entries instanceof ArrayList)) {
         this.lootPool.entries = new ArrayList(this.lootPool.entries);
      }

      this.lootPool.entries.add(entriesBuilder.build());
      return this;
   }

   public net.minecraft.world.level.storage.loot.LootPool.Builder when(
      net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder conditionBuilder
   ) {
      if (!(this.lootPool.conditions instanceof ArrayList)) {
         this.lootPool.conditions = new ArrayList(this.lootPool.conditions);
      }

      this.lootPool.conditions.add(conditionBuilder.build());
      this.lootPool.compositeCondition = Util.allOf(this.lootPool.conditions);
      return this;
   }

   public net.minecraft.world.level.storage.loot.LootPool.Builder apply(
      net.minecraft.world.level.storage.loot.functions.LootItemFunction.Builder functionBuilder
   ) {
      if (!(this.lootPool.functions instanceof ArrayList)) {
         this.lootPool.functions = new ArrayList(this.lootPool.functions);
      }

      this.lootPool.functions.add(functionBuilder.build());
      this.lootPool.compositeFunction = LootItemFunctions.compose(this.lootPool.functions);
      return this;
   }

   public LootPool build() {
      return this.lootPool;
   }
}
