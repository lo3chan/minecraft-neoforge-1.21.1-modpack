package com.aetherteam.aether.integration.jei.categories.block;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.recipes.block.AccessoryFreezableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class AccessoryFreezableRecipeCategory extends AbstractAetherBlockStateRecipeCategory<AccessoryFreezableRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("aether", "accessory_freezable");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/gui/menu/jei_render.png");
   public static final RecipeType<AccessoryFreezableRecipe> RECIPE_TYPE = RecipeType.create("aether", "accessory_freezable", AccessoryFreezableRecipe.class);

   public AccessoryFreezableRecipeCategory(IGuiHelper guiHelper, IPlatformFluidHelper<?> fluidHelper) {
      super(
         "accessory_freezable",
         UID,
         guiHelper.createDrawable(TEXTURE, 0, 0, 84, 28),
         guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)AetherItems.ICE_RING.get())),
         RECIPE_TYPE,
         fluidHelper
      );
   }
}
