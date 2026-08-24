package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.common.util.ItemStackUtil;

public class ItemStackArrayVariableSerializer extends GenericArrayVariableSerializer<ItemStack> {
   public ItemStackArrayVariableSerializer() {
      super(new ItemStackVariableSerializer(), ItemStack.class);
   }

   public ItemStack[] fromJson(JsonElement json, Provider registries) {
      if (!json.isJsonArray()) {
         return this.fromNonArray(json, registries);
      } else {
         JsonArray array = json.getAsJsonArray();
         List<ItemStack> stacks = new ArrayList<>();

         for (JsonElement e : array) {
            stacks.addAll(Arrays.asList(this.fromNonArray(e, registries)));
         }

         return stacks.toArray(this.empty);
      }
   }

   public ItemStack[] fromNonArray(JsonElement json, Provider registries) {
      if (json.isJsonNull()) {
         return this.empty;
      } else if (json.isJsonPrimitive()) {
         return ItemStackUtil.loadStackListFromString(json.getAsString(), registries).toArray(this.empty);
      } else if (json.isJsonObject()) {
         return new ItemStack[]{ItemStackUtil.loadStackFromJson(json.getAsJsonObject(), registries)};
      } else {
         throw new IllegalArgumentException("Can't make an ItemStack from an array!");
      }
   }
}
