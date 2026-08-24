package vazkii.patchouli.api;

import com.google.gson.JsonElement;
import net.minecraft.core.HolderLookup.Provider;

public interface IVariableSerializer<T> {
   T fromJson(JsonElement var1, Provider var2);

   JsonElement toJson(T var1, Provider var2);
}
