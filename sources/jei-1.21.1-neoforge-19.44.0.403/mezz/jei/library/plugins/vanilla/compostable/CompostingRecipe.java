package mezz.jei.library.plugins.vanilla.compostable;

import com.google.common.base.Preconditions;
import java.util.List;
import mezz.jei.api.recipe.vanilla.IJeiCompostingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CompostingRecipe implements IJeiCompostingRecipe {
   private final List<ItemStack> inputs;
   private final float chance;
   private final ResourceLocation uid;

   public CompostingRecipe(ItemStack input, float chance, ResourceLocation uid) {
      Preconditions.checkArgument(chance > 0.0F, "composting chance must be greater than 0");
      this.inputs = List.of(input);
      this.chance = chance;
      this.uid = uid;
   }

   @Override
   public List<ItemStack> getInputs() {
      return this.inputs;
   }

   @Override
   public float getChance() {
      return this.chance;
   }

   @Override
   public ResourceLocation getUid() {
      return this.uid;
   }
}
