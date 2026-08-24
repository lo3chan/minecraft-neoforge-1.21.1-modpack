package at.petrak.hexcasting.forge.interop.jei;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.client.render.RenderLib;
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BrainsweepRecipeCategory implements IRecipeCategory<BrainsweepRecipe> {
   public static final ResourceLocation UID = HexAPI.modLoc("brainsweep");
   private final IDrawableStatic background;
   private final IDrawable icon;
   private final Component localizedName;

   public BrainsweepRecipeCategory(IGuiHelper guiHelper) {
      ResourceLocation location = HexAPI.modLoc("textures/gui/brainsweep_jei.png");
      this.background = guiHelper.drawableBuilder(location, 0, 0, 118, 86).setTextureSize(128, 128).build();
      ResourceLocation brainsweep = HexAPI.modLoc("brainsweep");
      this.localizedName = Component.translatable("hexcasting.action." + brainsweep);
      this.icon = new PatternDrawable(brainsweep, 16, 16);
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

   @NotNull
   public List<Component> getTooltipStrings(@NotNull BrainsweepRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      if (37.0 <= mouseX && mouseX <= 63.0 && 19.0 <= mouseY && mouseY <= 67.0) {
         Minecraft mc = Minecraft.getInstance();
         return recipe.entityIn().getTooltip(mc.options.advancedItemTooltips);
      } else {
         return Collections.emptyList();
      }
   }

   public void draw(@NotNull BrainsweepRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Entity example = recipe.entityIn().exampleEntity(level);
         if (example == null) {
            return;
         }

         RenderSystem.enableBlend();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderLib.renderEntity(graphics, example, level, 50.0F, 62.5F, ClientTickCounter.getTotal(), 20.0F, 0.0F);
      }
   }

   public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull BrainsweepRecipe recipe, @NotNull IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 12, 35).addItemStacks(recipe.blockIn().getDisplayedStacks());
      builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 35).addItemStack(new ItemStack(recipe.result().getBlock()));
   }

   @NotNull
   public RecipeType<BrainsweepRecipe> getRecipeType() {
      return HexJEIPlugin.BRAINSWEEPING;
   }
}
