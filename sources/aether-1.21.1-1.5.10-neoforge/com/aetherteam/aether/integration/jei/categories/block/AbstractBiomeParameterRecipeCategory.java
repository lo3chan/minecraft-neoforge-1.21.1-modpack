package com.aetherteam.aether.integration.jei.categories.block;

import com.aetherteam.aether.integration.jei.categories.BiomeTooltip;
import com.aetherteam.aether.recipe.recipes.block.AbstractBiomeParameterRecipe;
import java.util.List;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public abstract class AbstractBiomeParameterRecipeCategory<T extends AbstractBiomeParameterRecipe>
   extends AbstractAetherBlockStateRecipeCategory<T>
   implements BiomeTooltip {
   public AbstractBiomeParameterRecipeCategory(
      String id, ResourceLocation uid, IDrawable background, IDrawable icon, RecipeType<T> recipeType, IPlatformFluidHelper<?> fluidHelper
   ) {
      super(id, uid, background, icon, recipeType, fluidHelper);
   }

   protected void populateAdditionalInformation(T recipe, List<Component> tooltip) {
      if (Minecraft.getInstance().level != null && recipe.getBiome().isPresent()) {
         this.populateBiomeInformation(
            (ResourceKey<Biome>)recipe.getBiome().get().left().orElse(null), (TagKey<Biome>)recipe.getBiome().get().right().orElse(null), tooltip
         );
      }
   }
}
