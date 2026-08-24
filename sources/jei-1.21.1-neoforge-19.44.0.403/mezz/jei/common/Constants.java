package mezz.jei.common;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;

public final class Constants {
   public static final RecipeType<?> UNIVERSAL_RECIPE_TRANSFER_TYPE = RecipeType.create("jei", "universal_recipe_transfer_handler", Object.class);
   public static final ResourceLocation LOCATION_JEI_GUI_TEXTURE_ATLAS = ResourceLocation.fromNamespaceAndPath("jei", "textures/atlas/gui.png");
   public static final ResourceLocation JEI_GUI_TEXTURE_ATLAS_ID = ResourceLocation.fromNamespaceAndPath("jei", "gui");

   private Constants() {
   }
}
