package jeresources.jei.villager;

import java.util.List;
import jeresources.collection.TradeList;
import jeresources.entry.AbstractVillagerEntry;
import jeresources.jei.BlankJEIRecipeCategory;
import jeresources.jei.JEIConfig;
import jeresources.reference.Resources;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VillagerCategory extends BlankJEIRecipeCategory<AbstractVillagerEntry> {
   protected static final int X_FIRST_ITEM = 95;
   protected static final int X_ITEM_DISTANCE = 18;
   protected static final int X_ITEM_RESULT = 150;
   protected static final int Y_ITEM_DISTANCE = 22;

   public VillagerCategory() {
      super(JEIConfig.getJeiHelpers().getGuiHelper().createDrawable(Resources.Gui.Jei.TABS, 0, 0, 16, 16), new VillagerWrapper());
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("jer.villager.title");
   }

   @NotNull
   public IDrawable getBackground() {
      return Resources.Gui.Jei.VILLAGER;
   }

   @NotNull
   public RecipeType<AbstractVillagerEntry> getRecipeType() {
      return JEIConfig.VILLAGER_TYPE;
   }

   public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull AbstractVillagerEntry recipe, @NotNull IFocusGroup focuses) {
      if (recipe.hasPois()) {
         builder.addSlot(RecipeIngredientRole.INPUT, 50, 19).addItemStacks(recipe.getPois());
      }

      IFocus<ItemStack> focus = (IFocus<ItemStack>)focuses.getFocuses(VanillaTypes.ITEM_STACK).findFirst().orElse(null);
      ((VillagerWrapper)this.recipeCategoryExtension).setFocus(focus);
      List<Integer> possibleLevels = recipe.getPossibleLevels(focus);
      int y = 1 + 22 * (6 - possibleLevels.size()) / 2;

      for (int i = 0; i < possibleLevels.size(); i++) {
         TradeList tradeList = recipe.getVillagerTrades(possibleLevels.get(i)).getFocusedList(focus);
         builder.addSlot(RecipeIngredientRole.INPUT, 96, y + i * 22).addItemStacks(tradeList.getCostAs());
         builder.addSlot(RecipeIngredientRole.INPUT, 114, y + i * 22).addItemStacks(tradeList.getCostBs());
         builder.addSlot(RecipeIngredientRole.OUTPUT, 151, y + i * 22).addItemStacks(tradeList.getResults());
      }
   }
}
