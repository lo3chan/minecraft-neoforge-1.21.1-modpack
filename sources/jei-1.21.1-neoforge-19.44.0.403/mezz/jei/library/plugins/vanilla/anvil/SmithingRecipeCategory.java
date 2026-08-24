package mezz.jei.library.plugins.vanilla.anvil;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.IExtendableSmithingRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class SmithingRecipeCategory extends AbstractRecipeCategory<RecipeHolder<SmithingRecipe>> implements IExtendableSmithingRecipeCategory {
   private final Map<Class<? extends SmithingRecipe>, ISmithingCategoryExtension<?>> extensions = new HashMap<>();

   public SmithingRecipeCategory(IGuiHelper guiHelper) {
      super(RecipeTypes.SMITHING, Blocks.SMITHING_TABLE.getName(), guiHelper.createDrawableItemLike(Blocks.SMITHING_TABLE), 108, 28);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SmithingRecipe> recipeHolder, IFocusGroup focuses) {
      SmithingRecipe recipe = (SmithingRecipe)recipeHolder.value();
      ISmithingCategoryExtension<? super SmithingRecipe> extension = this.getExtension(recipe);
      if (extension != null) {
         IRecipeSlotBuilder templateSlot = builder.addInputSlot(1, 6).setStandardSlotBackground();
         IRecipeSlotBuilder baseSlot = builder.addInputSlot(19, 6).setStandardSlotBackground();
         IRecipeSlotBuilder additionSlot = builder.addInputSlot(37, 6).setStandardSlotBackground();
         IRecipeSlotBuilder outputSlot = builder.addOutputSlot(91, 6).setStandardSlotBackground();
         extension.setTemplate(recipe, templateSlot);
         extension.setBase(recipe, baseSlot);
         extension.setAddition(recipe, additionSlot);
         extension.setOutput(recipe, outputSlot);
      }
   }

   public void onDisplayedIngredientsUpdate(RecipeHolder<SmithingRecipe> recipeHolder, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
      SmithingRecipe recipe = (SmithingRecipe)recipeHolder.value();
      ISmithingCategoryExtension<? super SmithingRecipe> extension = this.getExtension(recipe);
      if (extension != null) {
         IRecipeSlotDrawable templateSlot = (IRecipeSlotDrawable)recipeSlots.getFirst();
         IRecipeSlotDrawable baseSlot = recipeSlots.get(1);
         IRecipeSlotDrawable additionSlot = recipeSlots.get(2);
         IRecipeSlotDrawable outputSlot = recipeSlots.get(3);
         extension.onDisplayedIngredientsUpdate(recipe, templateSlot, baseSlot, additionSlot, outputSlot, focuses);
      }
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<SmithingRecipe> recipe, IFocusGroup focuses) {
      builder.addRecipeArrow().setPosition(61, 6);
   }

   public boolean isHandled(RecipeHolder<SmithingRecipe> recipeHolder) {
      SmithingRecipe recipe = (SmithingRecipe)recipeHolder.value();
      ISmithingCategoryExtension<SmithingRecipe> extension = this.getExtension(recipe);
      return extension != null;
   }

   public ResourceLocation getRegistryName(RecipeHolder<SmithingRecipe> recipe) {
      return recipe.id();
   }

   @Override
   public Codec<RecipeHolder<SmithingRecipe>> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
      return codecHelper.getRecipeHolderCodec();
   }

   @Override
   public <R extends SmithingRecipe> void addExtension(Class<? extends R> recipeClass, ISmithingCategoryExtension<R> extension) {
      ErrorUtil.checkNotNull(recipeClass, "recipeClass");
      ErrorUtil.checkNotNull(extension, "extension");
      if (this.extensions.containsKey(recipeClass)) {
         throw new IllegalArgumentException("An extension has already been registered for: " + recipeClass);
      } else {
         this.extensions.put(recipeClass, extension);
      }
   }

   @Nullable
   private <R extends SmithingRecipe> ISmithingCategoryExtension<? super R> getExtension(SmithingRecipe recipe) {
      ISmithingCategoryExtension<?> extension = this.extensions.get(recipe.getClass());
      if (extension != null) {
         return (ISmithingCategoryExtension<? super R>)extension;
      } else {
         for (Entry<Class<? extends SmithingRecipe>, ISmithingCategoryExtension<?>> e : this.extensions.entrySet()) {
            if (e.getKey().isInstance(recipe)) {
               return (ISmithingCategoryExtension<? super R>)e.getValue();
            }
         }

         return null;
      }
   }
}
