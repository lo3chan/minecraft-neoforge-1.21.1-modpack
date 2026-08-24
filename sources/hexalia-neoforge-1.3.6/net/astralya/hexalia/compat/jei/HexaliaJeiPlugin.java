package net.astralya.hexalia.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.astralya.hexalia.compat.jei.category.CelestialInfusionJeiCategory;
import net.astralya.hexalia.compat.jei.category.MortarAndPestleJeiCategory;
import net.astralya.hexalia.compat.jei.category.MutationJeiCategory;
import net.astralya.hexalia.compat.jei.category.NaturesRitualJeiCategory;
import net.astralya.hexalia.compat.jei.category.SmallCauldronJeiCategory;
import net.astralya.hexalia.compat.jei.util.JeiRecipeLookup;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public final class HexaliaJeiPlugin implements IModPlugin {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("hexalia", "jei_plugin");

   public ResourceLocation getPluginUid() {
      return UID;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new MortarAndPestleJeiCategory(guiHelper),
            new SmallCauldronJeiCategory(guiHelper),
            new NaturesRitualJeiCategory(guiHelper),
            new CelestialInfusionJeiCategory(guiHelper),
            new MutationJeiCategory(guiHelper)
         }
      );
   }

   public void registerRecipes(IRecipeRegistration registration) {
      registration.addRecipes(HexaliaJeiRecipeTypes.MORTAR_AND_PESTLE, JeiRecipeLookup.getRecipes((RecipeType)ModRecipeTypes.MORTAR_AND_PESTLE.get()));
      registration.addRecipes(HexaliaJeiRecipeTypes.SMALL_CAULDRON, JeiRecipeLookup.getRecipes((RecipeType)ModRecipeTypes.SMALL_CAULDRON.get()));
      registration.addRecipes(HexaliaJeiRecipeTypes.NATURES_RITUAL, JeiRecipeLookup.getRecipes((RecipeType)ModRecipeTypes.NATURES_RITUAL.get()));
      registration.addRecipes(HexaliaJeiRecipeTypes.CELESTIAL_INFUSION, JeiRecipeLookup.getRecipes((RecipeType)ModRecipeTypes.CELESTIAL_INFUSION.get()));
      registration.addRecipes(HexaliaJeiRecipeTypes.MUTATION, JeiRecipeLookup.getRecipes((RecipeType)ModRecipeTypes.MUTATION.get()));
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst((ItemLike)ModItems.MORTAR_AND_PESTLE.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.MORTAR_AND_PESTLE});
      registration.addRecipeCatalyst((ItemLike)ModItems.SMALL_CAULDRON.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.SMALL_CAULDRON});
      registration.addRecipeCatalyst((ItemLike)ModItems.RITUAL_TABLE.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.NATURES_RITUAL});
      registration.addRecipeCatalyst((ItemLike)ModItems.RITUAL_BRAZIER.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.NATURES_RITUAL});
      registration.addRecipeCatalyst((ItemLike)ModItems.HEX_FOCUS.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.NATURES_RITUAL});
      registration.addRecipeCatalyst((ItemLike)ModItems.RITUAL_BRAZIER.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.CELESTIAL_INFUSION});
      registration.addRecipeCatalyst((ItemLike)ModItems.CELESTIAL_CRYSTAL.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.CELESTIAL_INFUSION});
      registration.addRecipeCatalyst((ItemLike)ModItems.HEX_FOCUS.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.CELESTIAL_INFUSION});
      registration.addRecipeCatalyst((ItemLike)ModItems.MUTAVIS.get(), new mezz.jei.api.recipe.RecipeType[]{HexaliaJeiRecipeTypes.MUTATION});
   }
}
