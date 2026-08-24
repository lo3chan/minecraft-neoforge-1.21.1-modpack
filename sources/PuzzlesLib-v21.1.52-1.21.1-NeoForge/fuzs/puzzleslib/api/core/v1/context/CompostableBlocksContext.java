package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Holder;
import net.minecraft.world.level.ItemLike;

@Deprecated
@FunctionalInterface
public interface CompostableBlocksContext {
   void registerCompostable(float var1, Holder<? extends ItemLike>... var2);
}
