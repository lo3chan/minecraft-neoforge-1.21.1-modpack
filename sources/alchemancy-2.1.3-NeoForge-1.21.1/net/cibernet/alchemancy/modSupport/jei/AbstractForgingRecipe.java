package net.cibernet.alchemancy.modSupport.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractForgingRecipe<T extends AbstractForgeRecipe<?>> implements IRecipeCategory<T> {
   private final ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath("alchemancy", "textures/gui/jei/conversion_arrow.png");
   private final ResourceLocation DIAGRAM = ResourceLocation.fromNamespaceAndPath("alchemancy", "textures/gui/jei/forge_diagram.png");
   private final ResourceLocation OUTPUT = ResourceLocation.fromNamespaceAndPath("alchemancy", "textures/gui/jei/property_output_slot.png");
   private final IDrawable icon;
   protected static final int RADIUS = 24;

   public AbstractForgingRecipe(IGuiHelper helper) {
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, AlchemancyItems.INFUSION_PEDESTAL.toStack());
   }

   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }

   public int getWidth() {
      return 112;
   }

   public int getHeight() {
      return 64;
   }

   public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      guiGraphics.blit(this.ARROW, 64, 24, 0.0F, 0.0F, 32, 16, 32, 16);
      guiGraphics.blit(this.DIAGRAM, 8, 8, 0.0F, 0.0F, 48, 48, 48, 48);
      if (this.getOutput(recipe).isEmpty()) {
         guiGraphics.blit(this.OUTPUT, 96, 24, 0.0F, 0.0F, 16, 16, 16, 16);
      }
   }

   public abstract ItemStack getOutput(T var1);

   public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
      int xOff = 24;
      int yOff = 24;
      ArrayList<Ingredient> ingredients = new ArrayList<>(recipe.getInfusables());
      List<ItemStack> propertyCapsules = recipe.getInfusedProperties().stream().map(xva$0 -> InfusedPropertiesHelper.createPropertyIngredient(xva$0)).toList();
      float totalSize = ingredients.size() + propertyCapsules.size();

      for (int i = 0; i < propertyCapsules.size(); i++) {
         builder.addInputSlot(
               xOff - (int)(24.0F * Mth.sin(3.1415927F + 6.2831855F * (i / totalSize))),
               yOff + (int)(24.0F * Mth.cos(3.1415927F + 6.2831855F * (i / totalSize)))
            )
            .addItemStack(propertyCapsules.get(i));
      }

      for (int i = 0; i < ingredients.size(); i++) {
         builder.addInputSlot(
               xOff - (int)(24.0F * Mth.sin(3.1415927F + 6.2831855F * ((i + propertyCapsules.size()) / totalSize))),
               yOff + (int)(24.0F * Mth.cos(3.1415927F + 6.2831855F * ((i + propertyCapsules.size()) / totalSize)))
            )
            .addIngredients(ingredients.get(i));
      }

      if (recipe.getCatalyst().isPresent()) {
         builder.addInputSlot(xOff, yOff).addIngredients(recipe.getCatalyst().get());
      }

      builder.addOutputSlot(xOff + 24 + 48, yOff).addItemStack(this.getOutput(recipe));
   }
}
