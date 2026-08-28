/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.brewing;

import java.util.List;
import java.util.Objects;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.library.plugins.vanilla.brewing.BrewingRecipeUtil;
import mezz.jei.library.plugins.vanilla.ingredients.subtypes.PotionSubtypeInterpreter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class JeiBrewingRecipe
implements IJeiBrewingRecipe {
    private final List<ItemStack> ingredients;
    private final List<ItemStack> potionInputs;
    private final ItemStack potionOutput;
    @Nullable
    private final ResourceLocation uid;
    private final BrewingRecipeUtil brewingRecipeUtil;
    private final int hashCode;

    public JeiBrewingRecipe(List<ItemStack> ingredients, List<ItemStack> potionInputs, ItemStack potionOutput, @Nullable ResourceLocation uid, BrewingRecipeUtil brewingRecipeUtil) {
        this.ingredients = List.copyOf(ingredients);
        this.potionInputs = List.copyOf(potionInputs);
        this.potionOutput = potionOutput;
        this.uid = uid;
        this.brewingRecipeUtil = brewingRecipeUtil;
        brewingRecipeUtil.addRecipe(potionInputs, potionOutput);
        this.hashCode = uid != null ? uid.hashCode() : Objects.hash(ingredients.stream().map(ItemStack::getItem).toList(), potionInputs.stream().map(ItemStack::getItem).toList(), potionOutput.getItem());
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

    @Override
    @Nullable
    public ResourceLocation getUid() {
        return this.uid;
    }

    public boolean equals(Object obj) {
        int i;
        if (!(obj instanceof JeiBrewingRecipe)) {
            return false;
        }
        JeiBrewingRecipe other = (JeiBrewingRecipe)obj;
        if (this.uid != null) {
            return this.uid.equals((Object)other.uid);
        }
        for (i = 0; i < this.potionInputs.size(); ++i) {
            ItemStack otherPotionInput;
            ItemStack potionInput = this.potionInputs.get(i);
            if (JeiBrewingRecipe.arePotionsEqual(potionInput, otherPotionInput = other.potionInputs.get(i))) continue;
            return false;
        }
        if (!JeiBrewingRecipe.arePotionsEqual(other.potionOutput, this.potionOutput)) {
            return false;
        }
        if (this.ingredients.size() != other.ingredients.size()) {
            return false;
        }
        for (i = 0; i < this.ingredients.size(); ++i) {
            if (ItemStack.matches((ItemStack)this.ingredients.get(i), (ItemStack)other.ingredients.get(i))) continue;
            return false;
        }
        return true;
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

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        ItemStack input = (ItemStack)this.potionInputs.getFirst();
        String inputName = PotionSubtypeInterpreter.INSTANCE.getStringName(input);
        String outputName = PotionSubtypeInterpreter.INSTANCE.getStringName(this.potionOutput);
        return String.valueOf(this.ingredients) + " + [" + String.valueOf(input.getItem()) + " " + inputName + "] = [" + String.valueOf(this.potionOutput) + " " + outputName + "]";
    }
}

