package jeresources.api.util;

import jeresources.api.conditionals.Conditional;
import jeresources.api.drop.LootDrop;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;

public class LootConditionHelper {
   public static void applyCondition(LootItemCondition condition, LootDrop lootDrop) {
      if (condition instanceof LootItemKilledByPlayerCondition) {
         lootDrop.addConditional(Conditional.playerKill);
      } else if (condition instanceof LootItemRandomChanceCondition) {
         lootDrop.chance = ((LootItemRandomChanceCondition)condition).chance().getFloat(null);
      } else if (condition instanceof LootItemRandomChanceWithEnchantedBonusCondition) {
         lootDrop.chance = ((LootItemRandomChanceWithEnchantedBonusCondition)condition).enchantedChance().calculate(1);
         lootDrop.addConditional(Conditional.affectedByLooting);
      } else if (condition instanceof LootItemBlockStatePropertyCondition) {
      }
   }
}
