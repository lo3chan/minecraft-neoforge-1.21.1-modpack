package at.petrak.hexcasting.forge.interop.jei;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.casting.actions.spells.OpEdifySapling;
import at.petrak.hexcasting.common.lib.HexBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EdifyRecipeCategory implements IRecipeCategory<OpEdifySapling> {
   public static final ResourceLocation UID = HexAPI.modLoc("edify_tree");
   private final IDrawableStatic background;
   private final IDrawable icon;
   private final Component localizedName;

   public EdifyRecipeCategory(IGuiHelper guiHelper) {
      ResourceLocation location = HexAPI.modLoc("textures/gui/edify_jei.png");
      this.background = guiHelper.drawableBuilder(location, 0, 0, 79, 61).setTextureSize(128, 128).build();
      ResourceLocation edify = HexAPI.modLoc("edify");
      this.localizedName = Component.translatable("hexcasting.action." + edify);
      this.icon = new PatternDrawable(edify, 16, 16).strokeOrder(false);
   }

   @OnlyIn(Dist.CLIENT)
   @NotNull
   public Component getTitle() {
      return this.localizedName;
   }

   @NotNull
   public IDrawable getBackground() {
      return this.background;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull OpEdifySapling recipe, @NotNull IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 12, 22).addIngredients(Ingredient.of(ItemTags.SAPLINGS));
      ((IRecipeSlotBuilder)((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 51, 10)
               .addItemStack(new ItemStack(HexBlocks.AMETHYST_EDIFIED_LEAVES)))
            .addItemStack(new ItemStack(HexBlocks.AVENTURINE_EDIFIED_LEAVES)))
         .addItemStack(new ItemStack(HexBlocks.CITRINE_EDIFIED_LEAVES));
      ((IRecipeSlotBuilder)((IRecipeSlotBuilder)((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 51, 35)
                  .addItemStack(new ItemStack(HexBlocks.EDIFIED_LOG)))
               .addItemStack(new ItemStack(HexBlocks.EDIFIED_LOG_AMETHYST)))
            .addItemStack(new ItemStack(HexBlocks.EDIFIED_LOG_AVENTURINE)))
         .addItemStack(new ItemStack(HexBlocks.EDIFIED_LOG_CITRINE));
   }

   @NotNull
   public RecipeType<OpEdifySapling> getRecipeType() {
      return HexJEIPlugin.EDIFY;
   }
}
