package vazkii.patchouli.api;

import java.util.function.UnaryOperator;
import net.minecraft.core.HolderLookup.Provider;

public interface IVariablesAvailableCallback {
   void onVariablesAvailable(UnaryOperator<IVariable> var1, Provider var2);
}
