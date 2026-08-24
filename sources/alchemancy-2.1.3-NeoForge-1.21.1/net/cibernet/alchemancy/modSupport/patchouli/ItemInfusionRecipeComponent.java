package net.cibernet.alchemancy.modSupport.patchouli;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

public class ItemInfusionRecipeComponent extends IngredientRingComponentBase {
   transient AbstractForgeRecipe<?> recipe;
   @SerializedName("recipe")
   public String recipeName;

   public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
      this.recipeName = lookup.apply(IVariable.wrap(this.recipeName)).asString();
      Level level = Minecraft.getInstance().level;
      if (level != null
         && ((RecipeHolder)level.getRecipeManager()
               .byKey(ResourceLocation.parse(this.recipeName))
               .orElseThrow(() -> new IllegalArgumentException("recipe " + this.recipeName + " does not exist")))
            .value() instanceof AbstractForgeRecipe r) {
         this.recipe = r;
      } else {
         throw new IllegalArgumentException(this.recipeName + " is not a valid recipe");
      }
   }

   @Override
   public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
      super.render(graphics, context, pticks, mouseX, mouseY);
      context.renderIngredient(graphics, this.x + 24, this.y + 24, mouseX, mouseY, this.recipe.getCatalyst().orElse(Ingredient.EMPTY));
   }

   @Override
   public void build(int componentX, int componentY, int pageNum) {
      super.build(componentX, componentY, pageNum);
      this.ingredients = new ArrayList<>(
         this.recipe
            .getInfusedProperties()
            .stream()
            .map(propertyHolder -> Ingredient.of(new ItemStack[]{InfusedPropertiesHelper.createPropertyIngredient(propertyHolder)}))
            .toList()
      );
      this.ingredients.addAll(this.recipe.getInfusables());
   }
}
