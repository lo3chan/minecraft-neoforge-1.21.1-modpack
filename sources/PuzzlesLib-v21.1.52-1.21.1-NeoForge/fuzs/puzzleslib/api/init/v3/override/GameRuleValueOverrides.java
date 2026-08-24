package fuzs.puzzleslib.api.init.v3.override;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Type;
import net.minecraft.world.level.GameRules.Value;

@Deprecated
public final class GameRuleValueOverrides {
   private GameRuleValueOverrides() {
   }

   public static void setValue(Key<BooleanValue> key, boolean value) {
      setValue(key, booleanValue -> booleanValue.set(value, null));
   }

   public static void setValue(Key<IntegerValue> key, int value) {
      setValue(key, integerValue -> integerValue.set(value, null));
   }

   public static <T extends Value<T>> void setValue(Key<T> key, Consumer<T> valueSetter) {
      Type<T> type = (Type<T>)GameRules.GAME_RULE_TYPES.get(key);
      Function<Type<T>, T> originalConstructor = type.constructor;
      type.constructor = factoryType -> {
         T ruleValue = originalConstructor.apply(factoryType);
         valueSetter.accept(ruleValue);
         return ruleValue;
      };
   }
}
