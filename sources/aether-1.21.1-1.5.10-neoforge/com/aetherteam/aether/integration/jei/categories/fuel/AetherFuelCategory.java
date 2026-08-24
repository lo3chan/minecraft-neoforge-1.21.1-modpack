package com.aetherteam.aether.integration.jei.categories.fuel;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.nitrogen.integration.jei.categories.fuel.AbstractFuelCategory;
import com.aetherteam.nitrogen.integration.jei.categories.fuel.FuelRecipe;
import java.util.List;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class AetherFuelCategory extends AbstractFuelCategory {
   public static final ResourceLocation ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/gui/sprites/menu/lit_progress_transparent.png");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/gui/menu/altar.png");
   public static final RecipeType<FuelRecipe> RECIPE_TYPE = RecipeType.create("aether", "fuel", FuelRecipe.class);

   public AetherFuelCategory(IGuiHelper helper) {
      super(
         helper,
         List.of(
            ((Block)AetherBlocks.ALTAR.get()).getName().getString(),
            ((Block)AetherBlocks.FREEZER.get()).getName().getString(),
            ((Block)AetherBlocks.INCUBATOR.get()).getName().getString()
         )
      );
   }

   public Component getTitle() {
      return Component.translatable("gui.aether.jei.fuel");
   }

   public RecipeType<FuelRecipe> getRecipeType() {
      return RECIPE_TYPE;
   }

   public ResourceLocation getBackgroundTexture() {
      return TEXTURE;
   }

   public ResourceLocation getIconTexture() {
      return ICON_TEXTURE;
   }
}
