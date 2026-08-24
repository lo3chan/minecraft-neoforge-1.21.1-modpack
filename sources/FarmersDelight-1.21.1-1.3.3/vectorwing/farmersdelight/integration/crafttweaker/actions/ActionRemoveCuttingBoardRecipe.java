package vectorwing.farmersdelight.integration.crafttweaker.actions;

import com.blamejared.crafttweaker.api.action.recipe.ActionRecipeBase;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.item.MCItemStackMutable;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import java.util.Arrays;
import java.util.Iterator;
import javax.script.ScriptException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

public class ActionRemoveCuttingBoardRecipe<T extends Recipe<?>> extends ActionRecipeBase<T> {
   private final IItemStack[] outputs;

   public ActionRemoveCuttingBoardRecipe(IRecipeManager<T> manager, IItemStack[] outputs) {
      super(manager);
      this.outputs = outputs;
   }

   public void apply() {
      Iterator<RecipeHolder<T>> it = this.getManager().getRecipes().iterator();

      label30:
      while (it.hasNext()) {
         CuttingBoardRecipe recipe = (CuttingBoardRecipe)it.next().value();
         if (recipe.getResults().size() == this.outputs.length) {
            int i = 0;

            for (ItemStack result : recipe.getResults()) {
               if (!this.outputs[i++].matches(new MCItemStackMutable(result))) {
                  continue label30;
               }
            }

            it.remove();
         }
      }
   }

   public String describe() {
      return "Removing \""
         + BuiltInRegistries.RECIPE_TYPE.getKey(this.getManager().getRecipeType())
         + "\" recipes with output: "
         + Arrays.toString((Object[])this.outputs)
         + "\"";
   }

   public boolean validate(Logger logger) {
      if (this.outputs == null) {
         logger.throwing(Level.WARN, new ScriptException("Output IItemStacks cannot be null!"));
         return false;
      } else {
         return true;
      }
   }
}
