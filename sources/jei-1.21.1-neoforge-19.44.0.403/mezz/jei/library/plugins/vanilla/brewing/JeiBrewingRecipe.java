package mezz.jei.library.plugins.vanilla.brewing;

import java.util.List;
import java.util.Objects;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.library.plugins.vanilla.ingredients.subtypes.PotionSubtypeInterpreter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class JeiBrewingRecipe implements IJeiBrewingRecipe {
   private final List<ItemStack> ingredients;
   private final List<ItemStack> potionInputs;
   private final ItemStack potionOutput;
   @Nullable
   private final ResourceLocation uid;
   private final BrewingRecipeUtil brewingRecipeUtil;
   private final int hashCode;

   public JeiBrewingRecipe(
      List<ItemStack> ingredients, List<ItemStack> potionInputs, ItemStack potionOutput, @Nullable ResourceLocation uid, BrewingRecipeUtil brewingRecipeUtil
   ) {
      this.ingredients = List.copyOf(ingredients);
      this.potionInputs = List.copyOf(potionInputs);
      this.potionOutput = potionOutput;
      this.uid = uid;
      this.brewingRecipeUtil = brewingRecipeUtil;
      brewingRecipeUtil.addRecipe(potionInputs, potionOutput);
      if (uid != null) {
         this.hashCode = uid.hashCode();
      } else {
         this.hashCode = Objects.hash(
            ingredients.stream().map(ItemStack::getItem).toList(), potionInputs.stream().map(ItemStack::getItem).toList(), potionOutput.getItem()
         );
      }
   }

   @Override
   public List<ItemStack> getPotionInputs() {
      return this.potionInputs;
   }

   @Override
   public List<ItemStack> getIngredients() {
      return this.ingredients;
   }

   @Override
   public ItemStack getPotionOutput() {
      return this.potionOutput;
   }

   @Nullable
   @Override
   public ResourceLocation getUid() {
      return this.uid;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof JeiBrewingRecipe other) {
         if (this.uid != null) {
            return this.uid.equals(other.uid);
         } else {
            for (int i = 0; i < this.potionInputs.size(); i++) {
               ItemStack potionInput = this.potionInputs.get(i);
               ItemStack otherPotionInput = other.potionInputs.get(i);
               if (!arePotionsEqual(potionInput, otherPotionInput)) {
                  return false;
               }
            }

            if (!arePotionsEqual(other.potionOutput, this.potionOutput)) {
               return false;
            } else if (this.ingredients.size() != other.ingredients.size()) {
               return false;
            } else {
               for (int ix = 0; ix < this.ingredients.size(); ix++) {
                  if (!ItemStack.matches(this.ingredients.get(ix), other.ingredients.get(ix))) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private static boolean arePotionsEqual(ItemStack potion1, ItemStack potion2) {
      Object key1 = PotionSubtypeInterpreter.INSTANCE.getSubtypeData(potion1, UidContext.Recipe);
      Object key2 = PotionSubtypeInterpreter.INSTANCE.getSubtypeData(potion2, UidContext.Recipe);
      return Objects.equals(key1, key2);
   }

   @Override
   public int getBrewingSteps() {
      return this.brewingRecipeUtil.getBrewingSteps(this.potionOutput);
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public String toString() {
      ItemStack input = (ItemStack)this.potionInputs.getFirst();
      String inputName = PotionSubtypeInterpreter.INSTANCE.getStringName(input);
      String outputName = PotionSubtypeInterpreter.INSTANCE.getStringName(this.potionOutput);
      return this.ingredients + " + [" + input.getItem() + " " + inputName + "] = [" + this.potionOutput + " " + outputName + "]";
   }
}
