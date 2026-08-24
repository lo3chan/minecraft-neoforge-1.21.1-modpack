package vazkii.patchouli.client.book.template;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup.Provider;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public final class JsonVariableWrapper implements IVariableProvider {
   private final JsonObject source;

   public JsonVariableWrapper(JsonObject source) {
      this.source = source;
   }

   @Override
   public IVariable get(String key, Provider registries) {
      JsonElement prim = this.source.get(key);
      if (prim == null) {
         throw new IllegalArgumentException("Attempted to get variable " + key + " when it's not present");
      } else {
         return IVariable.wrap(prim, registries);
      }
   }

   @Override
   public boolean has(String key) {
      return this.source.has(key);
   }
}
