package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.level.ItemLike;

@Deprecated
@FunctionalInterface
public interface FuelBurnTimesContext {
   void registerFuel(int var1, ItemLike... var2);
}
