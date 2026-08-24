package mezz.jei.library.plugins.vanilla.anvil;

import java.util.List;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record AnvilRecipe(List<ItemStack> leftInputs, List<ItemStack> rightInputs, List<ItemStack> outputs, @Nullable ResourceLocation uid)
   implements IJeiAnvilRecipe {
   @Override
   public List<ItemStack> getLeftInputs() {
      return this.leftInputs;
   }

   @Override
   public List<ItemStack> getRightInputs() {
      return this.rightInputs;
   }

   @Override
   public List<ItemStack> getOutputs() {
      return this.outputs;
   }

   @Nullable
   @Override
   public ResourceLocation getUid() {
      return this.uid;
   }
}
