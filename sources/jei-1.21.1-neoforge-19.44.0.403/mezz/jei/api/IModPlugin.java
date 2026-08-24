package mezz.jei.api;

import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IAdvancedSearchRegistration;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiFeatures;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.config.IJeiConfigManager;
import net.minecraft.resources.ResourceLocation;

public interface IModPlugin {
   ResourceLocation getPluginUid();

   default void configureJei(IJeiFeatures jeiFeatures) {
   }

   default void registerItemSubtypes(ISubtypeRegistration registration) {
   }

   default <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
   }

   default void registerIngredients(IModIngredientRegistration registration) {
   }

   default void registerExtraIngredients(IExtraIngredientRegistration registration) {
   }

   default void registerIngredientAliases(IIngredientAliasRegistration registration) {
   }

   default void registerAdvancedSearch(IAdvancedSearchRegistration registration) {
   }

   default void registerModInfo(IModInfoRegistration modAliasRegistration) {
   }

   default void registerCategories(IRecipeCategoryRegistration registration) {
   }

   default void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
   }

   default void registerRecipes(IRecipeRegistration registration) {
   }

   default void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
   }

   default void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
   }

   default void registerGuiHandlers(IGuiHandlerRegistration registration) {
   }

   default void registerAdvanced(IAdvancedRegistration registration) {
   }

   default void registerRuntime(IRuntimeRegistration registration) {
   }

   default void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
   }

   default void onRuntimeUnavailable() {
   }

   default void onConfigManagerAvailable(IJeiConfigManager configManager) {
   }
}
