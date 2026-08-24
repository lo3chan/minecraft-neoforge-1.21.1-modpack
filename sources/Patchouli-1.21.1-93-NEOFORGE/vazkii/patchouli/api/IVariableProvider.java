package vazkii.patchouli.api;

import net.minecraft.core.HolderLookup.Provider;

public interface IVariableProvider {
   IVariable get(String var1, Provider var2);

   boolean has(String var1);
}
