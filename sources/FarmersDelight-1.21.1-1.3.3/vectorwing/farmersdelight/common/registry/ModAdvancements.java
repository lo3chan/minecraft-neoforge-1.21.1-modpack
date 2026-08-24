package vectorwing.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.advancement.CuttingBoardTrigger;

public class ModAdvancements {
   public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, "farmersdelight");
   public static final Supplier<CuttingBoardTrigger> USE_CUTTING_BOARD = TRIGGERS.register("use_cutting_board", CuttingBoardTrigger::new);
}
