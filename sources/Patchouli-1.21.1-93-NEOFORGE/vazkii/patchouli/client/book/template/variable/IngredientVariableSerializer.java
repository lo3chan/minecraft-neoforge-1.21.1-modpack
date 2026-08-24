package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

public class IngredientVariableSerializer implements IVariableSerializer<Ingredient> {
   public Ingredient fromJson(JsonElement json, Provider registries) {
      return json.isJsonPrimitive()
         ? ItemStackUtil.loadIngredientFromString(json.getAsString(), registries)
         : (Ingredient)Ingredient.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), json).result().orElseThrow();
   }

   public JsonElement toJson(Ingredient stack, Provider registries) {
      return (JsonElement)Ingredient.CODEC.encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), stack).result().orElseThrow();
   }
}
