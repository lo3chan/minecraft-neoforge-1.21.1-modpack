package mezz.jei.library.plugins.vanilla.cooking.fuel;

import java.text.NumberFormat;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiFuelingRecipe;
import mezz.jei.common.gui.textures.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class FurnaceFuelCategory extends AbstractRecipeCategory<IJeiFuelingRecipe> {
   public FurnaceFuelCategory(Textures textures) {
      super(RecipeTypes.FUELING, Component.translatable("gui.jei.category.fuel"), textures.getFlameIcon(), getMaxWidth(), 34);
   }

   private static int getMaxWidth() {
      Minecraft minecraft = Minecraft.getInstance();
      Font fontRenderer = minecraft.font;
      Component maxSmeltCountText = createSmeltCountText(2000000000);
      int maxStringWidth = fontRenderer.width(maxSmeltCountText.getString());
      int textPadding = 20;
      return 18 + textPadding + maxStringWidth;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, IJeiFuelingRecipe recipe, IFocusGroup focuses) {
      builder.addInputSlot(1, 17).setStandardSlotBackground().addItemStacks(recipe.getInputs());
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, IJeiFuelingRecipe recipe, IFocusGroup focuses) {
      int burnTime = recipe.getBurnTime();
      builder.addAnimatedRecipeFlame(burnTime).setPosition(1, 0);
      Component smeltCountText = createSmeltCountText(recipe.getBurnTime());
      builder.addText(smeltCountText, this.getWidth() - 20, this.getHeight())
         .setPosition(20, 0)
         .setTextAlignment(HorizontalAlignment.CENTER)
         .setTextAlignment(VerticalAlignment.CENTER)
         .setColor(-8355712);
   }

   public static Component createSmeltCountText(int burnTime) {
      if (burnTime == 200) {
         return Component.translatable("gui.jei.category.fuel.smeltCount.single");
      } else {
         NumberFormat numberInstance = NumberFormat.getNumberInstance();
         numberInstance.setMaximumFractionDigits(2);
         String smeltCount = numberInstance.format((double)(burnTime / 200.0F));
         return Component.translatable("gui.jei.category.fuel.smeltCount", new Object[]{smeltCount});
      }
   }

   @Nullable
   public ResourceLocation getRegistryName(IJeiFuelingRecipe recipe) {
      return null;
   }
}
