package jeresources.jei.worldgen;

import jeresources.entry.WorldGenEntry;
import jeresources.jei.BlankJEIRecipeCategory;
import jeresources.jei.JEIConfig;
import jeresources.reference.Resources;
import jeresources.util.RenderHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class WorldGenCategory extends BlankJEIRecipeCategory<WorldGenEntry> {
   protected static final int X_ITEM = 6;
   protected static final int Y_ITEM = 22;
   protected static final int X_DROP_ITEM = 6;
   protected static final int Y_DROP_ITEM = 67;
   private static final int DROP_ITEM_COUNT = 8;

   public WorldGenCategory() {
      super(JEIConfig.getJeiHelpers().getGuiHelper().createDrawable(Resources.Gui.Jei.TABS, 32, 16, 16, 16), new WorldGenWrapper());
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("jer.worldgen.title");
   }

   @NotNull
   public IDrawable getBackground() {
      return Resources.Gui.Jei.WORLD_GEN;
   }

   @NotNull
   public RecipeType<WorldGenEntry> getRecipeType() {
      return JEIConfig.WORLD_GEN_TYPE;
   }

   public void draw(@NotNull WorldGenEntry recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      RenderHelper.drawLine(guiGraphics, 29, 52, 157, 52, -7829368);
      RenderHelper.drawLine(guiGraphics, 29, 52, 29, 12, -7829368);
      super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
   }

   public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull WorldGenEntry recipe, @NotNull IFocusGroup focuses) {
      WorldGenTooltip worldGenTooltip = new WorldGenTooltip(recipe);
      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 6, 22).addItemStacks(recipe.getBlocks()))
         .setSlotName("oreSlot")
         .addTooltipCallback(worldGenTooltip);

      for (int i = 0; i < Math.min(8, recipe.getDrops().size()); i++) {
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 6 + i * 18, 67).addItemStack(recipe.getDrops().get(i)))
            .addTooltipCallback(worldGenTooltip);
      }
   }
}
