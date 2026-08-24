package mezz.jei.api.recipe.vanilla;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface IJeiGrindstoneRecipe {
   @Unmodifiable
   List<ItemStack> getTopInputs();

   @Unmodifiable
   List<ItemStack> getBottomInputs();

   @Unmodifiable
   List<ItemStack> getOutputs();

   int getMinXpReward();

   int getMaxXpReward();

   @Nullable
   ResourceLocation getUid();

   @Unmodifiable
   boolean isOutputRenderOnly();
}
