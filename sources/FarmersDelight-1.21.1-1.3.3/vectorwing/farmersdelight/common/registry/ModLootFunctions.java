package vectorwing.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.loot.function.CopySkilletFunction;
import vectorwing.farmersdelight.common.loot.function.SmokerCookFunction;

public class ModLootFunctions {
   public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, "farmersdelight");
   public static final Supplier<LootItemFunctionType<CopySkilletFunction>> COPY_SKILLET = LOOT_FUNCTIONS.register(
      "copy_skillet", () -> new LootItemFunctionType(CopySkilletFunction.CODEC)
   );
   public static final Supplier<LootItemFunctionType<SmokerCookFunction>> SMOKER_COOK = LOOT_FUNCTIONS.register(
      "smoker_cook", () -> new LootItemFunctionType(SmokerCookFunction.CODEC)
   );
}
