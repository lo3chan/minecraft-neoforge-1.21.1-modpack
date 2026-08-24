package jeresources.jei.enchantment;

import java.util.List;
import jeresources.jei.BlankJEIRecipeCategory;
import jeresources.jei.JEIConfig;
import jeresources.reference.Resources;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class EnchantmentCategory extends BlankJEIRecipeCategory<EnchantmentWrapper> {
   private static final int ITEM_X = 13;
   private static final int ITEM_Y = 12;

   public EnchantmentCategory() {
      super(JEIConfig.getJeiHelpers().getGuiHelper().createDrawable(Resources.Gui.Jei.TABS, 32, 0, 16, 16), null);
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("jer.enchantments.title");
   }

   @NotNull
   public IDrawable getBackground() {
      return Resources.Gui.Jei.ENCHANTMENT;
   }

   @NotNull
   public RecipeType<EnchantmentWrapper> getRecipeType() {
      return JEIConfig.ENCHANTMENT_TYPE;
   }

   public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull EnchantmentWrapper recipe, @NotNull IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 13, 12).addItemStack(recipe.itemStack);
   }

   public void draw(EnchantmentWrapper recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      recipe.drawInfo(recipe, this.getBackground().getWidth(), this.getBackground().getHeight(), guiGraphics, mouseX, mouseY);
   }

   @NotNull
   public List<Component> getTooltipStrings(EnchantmentWrapper recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      return recipe.getTooltipStrings(recipe, mouseX, mouseY);
   }
}
