package net.joefoxe.hexerei.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.CandleDipper;
import net.joefoxe.hexerei.data.recipes.DipperRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

public class DipperRecipeCategory implements IRecipeCategory<DipperRecipe> {
   public static final ResourceLocation UID = HexereiUtil.getResource("dipper");
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/dipper_jei.png");
   public static final ResourceLocation MIX_TEXTURE = HexereiUtil.getResource("textures/gui/mixing_cauldron_gui_jei.png");
   public static final ResourceLocation TEXTURE_BLANK = HexereiUtil.getResource("textures/block/blank.png");
   private final IDrawable background;
   private final IDrawable cauldron;
   private final IDrawable icon;
   private final IDrawable liquid;

   public DipperRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 100, 92);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ModBlocks.CANDLE_DIPPER.get()));
      this.liquid = helper.createDrawable(MIX_TEXTURE, 182, 2, 12, 10);
      this.cauldron = helper.createDrawable(TEXTURE, 106, 3, 12, 9);
   }

   public int getWidth() {
      return this.background.getWidth();
   }

   public int getHeight() {
      return this.background.getHeight();
   }

   public RecipeType<DipperRecipe> getRecipeType() {
      return new RecipeType(UID, DipperRecipe.class);
   }

   public Component getTitle() {
      return ((CandleDipper)ModBlocks.CANDLE_DIPPER.get()).getName();
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, DipperRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 15, 14).addItemStack(recipe.getInput());
      builder.addSlot(RecipeIngredientRole.OUTPUT, 69, 23).addItemStack(recipe.getOutput());
      FluidStack input = recipe.getLiquid();
      if (recipe.getFluidLevelsConsumed() != 0 && !input.isEmpty()) {
         input.setAmount(Mth.clamp(recipe.getFluidLevelsConsumed(), 0, 2000));
      }

      if (!input.isEmpty()) {
         builder.addSlot(RecipeIngredientRole.INPUT, 17, 35)
            .setFluidRenderer(input.getAmount(), false, 12, 10)
            .setOverlay(this.liquid, 0, 0)
            .addFluidStack(recipe.getFluid().getFluid(), recipe.getFluid().getAmount(), recipe.getFluid().getComponentsPatch());
      }
   }

   public void draw(DipperRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      int numberOfDips = recipe.getNumberOfDips();
      int dippingTime = recipe.getDippingTime();
      int dryingTime = recipe.getDryingTime();
      Minecraft minecraft = Minecraft.getInstance();
      this.background.draw(guiGraphics);
      guiGraphics.pose().pushPose();
      guiGraphics.pose().scale(0.6F, 0.6F, 0.6F);
      String numberOfDipsString = numberOfDips < 2147483647 ? Integer.toString(numberOfDips) : "?";
      MutableComponent times_dipped_1 = Component.translatable("gui.jei.category.dipper.times_dipped_1");
      MutableComponent times_dipped_3 = Component.translatable("gui.jei.category.dipper.result", new Object[]{numberOfDipsString});
      minecraft.font
         .drawInBatch(
            times_dipped_1, 9.996F, 93.296F, -8355712, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      minecraft.font
         .drawInBatch(
            times_dipped_3, 108.29F, 93.296F, -8355712, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      String dippingTimeString = dippingTime < 2147483647 ? dippingTime / 20 + (dippingTime % 20 == 0 ? "" : "." + dippingTime % 20) : "?";
      if (dippingTimeString.charAt(dippingTimeString.length() - 1) == '0' && dippingTime != 0 && dippingTime % 20 != 0) {
         dippingTimeString = dippingTimeString.substring(0, dippingTimeString.length() - 1);
      }

      MutableComponent dip_time_1 = Component.translatable("gui.jei.category.dipper.dip_time_1");
      MutableComponent dip_time_3 = Component.translatable("gui.jei.category.dipper.resultSeconds", new Object[]{dippingTimeString});
      minecraft.font
         .drawInBatch(
            dip_time_1, 9.996F, 113.288F, -8355712, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      minecraft.font
         .drawInBatch(
            dip_time_3, 108.29F, 113.288F, -8355712, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      String dryingTimeString = dryingTime < 2147483647 ? dryingTime / 20 + (dryingTime % 20 == 0 ? "" : "." + dryingTime % 20) : "?";
      if (dryingTimeString.charAt(dryingTimeString.length() - 1) == '0' && dryingTime != 0 && dryingTime % 20 != 0) {
         dryingTimeString = dryingTimeString.substring(0, dryingTimeString.length() - 1);
      }

      MutableComponent dry_time_1 = Component.translatable("gui.jei.category.dipper.dry_time_1");
      MutableComponent dry_time_3 = Component.translatable("gui.jei.category.dipper.resultSeconds", new Object[]{dryingTimeString});
      minecraft.font
         .drawInBatch(
            dry_time_1, 9.996F, 133.28F, -8355712, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      minecraft.font
         .drawInBatch(
            dry_time_3, 108.29F, 133.28F, -8355712, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      String outputName = recipe.getOutput().getHoverName().getString();
      minecraft.font
         .drawInBatch(
            outputName, 8.33F, 6.664F, -12566464, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 15728880
         );
      guiGraphics.pose().popPose();
   }
}
