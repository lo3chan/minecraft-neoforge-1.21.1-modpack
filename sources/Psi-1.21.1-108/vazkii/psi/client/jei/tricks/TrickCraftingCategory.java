package vazkii.psi.client.jei.tricks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.recipe.ITrickRecipe;
import vazkii.psi.common.Psi;
import vazkii.psi.common.item.base.ModItems;

public class TrickCraftingCategory implements IRecipeCategory<ITrickRecipe> {
   public static final RecipeType<ITrickRecipe> TYPE = RecipeType.create("psi", "trick", ITrickRecipe.class);
   private static final int trickX = 43;
   private static final int trickY = 24;
   private final Map<ResourceLocation, IDrawable> trickIcons = new HashMap<>();
   private final IDrawable background;
   private final IDrawable icon;
   private final IDrawable programmerHover;

   public TrickCraftingCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(Psi.location("textures/gui/jei/trick.png"), 0, 0, 96, 41);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ModItems.psidust.get()));
      this.programmerHover = helper.createDrawable(Psi.location("textures/gui/programmer.png"), 16, 184, 16, 16);
   }

   private static boolean onTrick(double mouseX, double mouseY) {
      return mouseX >= 43.0 && mouseX <= 59.0 && mouseY >= 24.0 && mouseY <= 40.0;
   }

   @NotNull
   public RecipeType<ITrickRecipe> getRecipeType() {
      return TYPE;
   }

   @NotNull
   public Component getTitle() {
      return Component.literal(I18n.get("jei.psi.category.trick", new Object[0]));
   }

   public int getWidth() {
      return 96;
   }

   public int getHeight() {
      return 41;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void draw(ITrickRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
      if (recipe.getPiece() != null) {
         IDrawable trickIcon = this.trickIcons.computeIfAbsent(recipe.getPiece().registryKey, key -> new DrawablePiece(recipe.getPiece()));
         trickIcon.draw(guiGraphics, 43, 24);
         if (onTrick(mouseX, mouseY)) {
            this.programmerHover.draw(guiGraphics, 43, 24);
         }
      }
   }

   public void getTooltip(@NotNull ITooltipBuilder tooltip, ITrickRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      if (recipe.getPiece() != null && onTrick(mouseX, mouseY)) {
         List<Component> tooltips = new ArrayList<>();
         recipe.getPiece().getTooltip(tooltips);
         tooltip.addAll(tooltips);
      }
   }

   public void setRecipe(IRecipeLayoutBuilder builder, ITrickRecipe recipe, @NotNull IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 1, 6).addIngredients(recipe.getInput());
      builder.addSlot(RecipeIngredientRole.CATALYST, 22, 24).addItemStack(recipe.getAssembly());
      builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 6).addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
   }
}
