package vectorwing.farmersdelight.common.crafting.condition;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.Configuration;

public class VanillaCrateEnabledCondition implements ICondition {
   public static final MapCodec<VanillaCrateEnabledCondition> CODEC = MapCodec.unit(new VanillaCrateEnabledCondition());
   public static final VanillaCrateEnabledCondition INSTANCE = new VanillaCrateEnabledCondition();

   public boolean test(@NotNull IContext context) {
      return Configuration.ENABLE_VANILLA_CROP_CRATES.get();
   }

   @NotNull
   public MapCodec<? extends ICondition> codec() {
      return CODEC;
   }
}
