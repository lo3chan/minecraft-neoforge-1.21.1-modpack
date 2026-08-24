package jeresources.api.util;

import java.util.stream.IntStream;
import jeresources.api.conditionals.Conditional;
import jeresources.api.conditionals.ICustomLootFunction;
import jeresources.api.drop.LootDrop;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class LootFunctionHelper {
   public static final LootContext randContext = new LootFunctionHelper.RandomLootContext();
   private static final int STATISTICAL_TEST = 100;

   public static void applyFunction(LootItemFunction lootFunction, LootDrop lootDrop) {
      if (lootFunction instanceof SetItemCountFunction) {
         lootDrop.minDrop = getMin(((SetItemCountFunction)lootFunction).value);
         if (lootDrop.minDrop < 0) {
            lootDrop.minDrop = 0;
         }

         lootDrop.item.setCount(Math.max(lootDrop.minDrop, 1));
         lootDrop.maxDrop = getMax(((SetItemCountFunction)lootFunction).value);
      } else if (lootFunction instanceof SetItemDamageFunction) {
         ((SetItemDamageFunction)lootFunction).run(lootDrop.item, randContext);
      } else if (lootFunction instanceof EnchantRandomlyFunction || lootFunction instanceof EnchantWithLevelsFunction) {
         lootDrop.enchanted = true;
      } else if (!(lootFunction instanceof SmeltItemFunction)) {
         if (lootFunction instanceof EnchantedCountIncreaseFunction) {
            lootDrop.addConditional(Conditional.affectedByLooting);
         } else if (lootFunction instanceof ICustomLootFunction) {
            ((ICustomLootFunction)lootFunction).apply(lootDrop);
         } else {
            try {
               lootDrop.item = (ItemStack)lootFunction.apply(lootDrop.item, null);
            } catch (NullPointerException var3) {
            }
         }
      }
   }

   public static int getMin(NumberProvider randomRange) {
      if (randomRange instanceof ConstantValue) {
         return randomRange.getInt(randContext);
      } else if (randomRange instanceof UniformGenerator) {
         return Mth.floor(((UniformGenerator)randomRange).min.getInt(randContext));
      } else {
         return randomRange instanceof BinomialDistributionGenerator
            ? 0
            : IntStream.iterate(0, i -> randomRange.getInt(randContext)).limit(100L).min().orElse(0);
      }
   }

   public static int getMax(NumberProvider randomRange) {
      if (randomRange instanceof ConstantValue) {
         return randomRange.getInt(randContext);
      } else if (randomRange instanceof UniformGenerator) {
         return Mth.floor(((UniformGenerator)randomRange).max.getInt(randContext));
      } else {
         return randomRange instanceof BinomialDistributionGenerator
            ? ((BinomialDistributionGenerator)randomRange).n.getInt(randContext)
            : IntStream.iterate(0, i -> randomRange.getInt(randContext)).limit(100L).max().orElse(0);
      }
   }

   public static class RandomLootContext extends LootContext {
      public RandomLootContext() {
         super(new Builder(null).create(LootContextParamSets.EMPTY), RandomSource.create(), null);
      }
   }
}
