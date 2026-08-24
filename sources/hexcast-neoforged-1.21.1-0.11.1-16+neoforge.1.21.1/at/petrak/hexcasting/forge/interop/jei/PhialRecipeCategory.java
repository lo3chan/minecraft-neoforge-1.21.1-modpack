package at.petrak.hexcasting.forge.interop.jei;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.common.casting.actions.spells.OpMakeBattery;
import at.petrak.hexcasting.interop.utils.PhialRecipeStackBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class PhialRecipeCategory implements IRecipeCategory<OpMakeBattery> {
   public static final ResourceLocation UID = HexAPI.modLoc("craft_phial");
   private final IDrawableStatic background;
   private final IDrawable icon;
   private final Component localizedName;

   public PhialRecipeCategory(IGuiHelper guiHelper) {
      ResourceLocation location = HexAPI.modLoc("textures/gui/phial_jei.png");
      this.background = guiHelper.drawableBuilder(location, 0, 0, 113, 40).setTextureSize(128, 128).build();
      ResourceLocation craftPhial = HexAPI.modLoc("craft/battery");
      this.localizedName = Component.translatable("hexcasting.action." + craftPhial);
      this.icon = new PatternDrawable(craftPhial, 12, 12);
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

   public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull OpMakeBattery recipe, @NotNull IFocusGroup focuses) {
      Pair<List<ItemStack>, List<ItemStack>> stacks = PhialRecipeStackBuilder.createStacks();
      IRecipeSlotBuilder inputSlot = (IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 12, 12).addItemStacks((List)stacks.getFirst());
      builder.addSlot(RecipeIngredientRole.INPUT, 47, 12).addIngredients(Ingredient.of(HexTags.Items.PHIAL_BASE));
      IRecipeSlotBuilder outputSlot = (IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 12).addItemStacks((List)stacks.getSecond());
      builder.createFocusLink(new IIngredientAcceptor[]{inputSlot, outputSlot});
   }

   @NotNull
   public RecipeType<OpMakeBattery> getRecipeType() {
      return HexJEIPlugin.PHIAL;
   }
}
