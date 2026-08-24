package net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.jei;

import java.util.Collections;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerInfoProvider;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerOcclusionProvider;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.CommonBalmModSupportRecipeViewer;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.IdentifiableRecipeTypeTransferRegistration;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.IngredientInfoRegistration;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.ScreenOcclusionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@JeiPlugin
public class CommonJeiPlugin implements IModPlugin {
   private final JeiRecipeViewerRegistrar registrar = new JeiRecipeViewerRegistrar();
   private boolean registrarsInitialized;

   private void ensureInitialized() {
      if (!this.registrarsInitialized) {
         if (Balm.modSupport().recipeViewers() instanceof CommonBalmModSupportRecipeViewer recipeViewerSupport) {
            for (RecipeViewerInfoProvider provider : recipeViewerSupport.getProviders()) {
               provider.initialize(this.registrar);
            }
         }

         this.registrarsInitialized = true;
      }
   }

   public ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("balm", "jei");
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      this.ensureInitialized();

      for (JeiRecipeTypeRegistration<?> recipeTypeRegistration : this.registrar.getRecipeTypes()) {
         recipeTypeRegistration.registerCatalysts(registration);
      }
   }

   public void registerRecipes(IRecipeRegistration registration) {
      this.ensureInitialized();

      for (JeiRecipeTypeRegistration<?> recipeTypeRegistration : this.registrar.getRecipeTypes()) {
         recipeTypeRegistration.registerRecipes(registration);
      }

      for (IngredientInfoRegistration ingredientInfoRegistration : this.registrar.getIngredientInfoRegistrations()) {
         registration.addIngredientInfo(ingredientInfoRegistration.itemLike(), new Component[]{ingredientInfoRegistration.description()});
      }
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      this.ensureInitialized();

      for (JeiRecipeTypeRegistration<?> recipeTypeRegistration : this.registrar.getRecipeTypes()) {
         recipeTypeRegistration.registerCategories(registration);
      }
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      this.ensureInitialized();

      for (ScreenOcclusionRegistration<?> entry : this.registrar.getScreenOcclusions()) {
         this.registerScreenOcclusion(registration, entry);
      }

      for (RecipeViewerOcclusionProvider<?> entry : this.registrar.getGlobalScreenOcclusions()) {
         this.registerGlobalScreenOcclusion(registration, entry);
      }
   }

   public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
      this.ensureInitialized();

      for (IdentifiableRecipeTypeTransferRegistration<?> entry : this.registrar.getIdentifiableRecipeTypeTransferRegistrations()) {
         this.registerRecipeTransferHandler(registration, entry);
      }
   }

   public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
      if (Balm.modSupport().recipeViewers() instanceof CommonBalmModSupportRecipeViewer recipeViewerSupport) {
         recipeViewerSupport.setHasKeyboardFocus(() -> jeiRuntime.getIngredientListOverlay().hasKeyboardFocus());
      }
   }

   public void onRuntimeUnavailable() {
      if (Balm.modSupport().recipeViewers() instanceof CommonBalmModSupportRecipeViewer recipeViewerSupport) {
         recipeViewerSupport.setHasKeyboardFocus(null);
      }
   }

   private <TMenu extends AbstractContainerMenu> void registerRecipeTransferHandler(
      IRecipeTransferRegistration registration, IdentifiableRecipeTypeTransferRegistration<TMenu> entry
   ) {
      registration.getJeiHelpers()
         .getRecipeType(entry.recipeTypeId())
         .ifPresent(
            recipeType -> registration.addRecipeTransferHandler(
               entry.menuClass(),
               (MenuType)entry.menuType().value(),
               recipeType,
               entry.recipeSlotStart(),
               entry.recipeSlotCount(),
               entry.inventorySlotStart(),
               entry.inventorySlotCount()
            )
         );
   }

   private <T extends AbstractContainerScreen<?>> void registerScreenOcclusion(IGuiHandlerRegistration registration, final ScreenOcclusionRegistration<T> entry) {
      registration.addGuiContainerHandler(entry.containerScreenClass(), new IGuiContainerHandler<T>() {
         public List<Rect2i> getGuiExtraAreas(T containerScreen) {
            return entry.provider().getOcclusions(containerScreen);
         }
      });
   }

   private <T extends AbstractContainerScreen<?>> void registerGlobalScreenOcclusion(
      IGuiHandlerRegistration registration, final RecipeViewerOcclusionProvider<T> entry
   ) {
      registration.addGlobalGuiHandler(
         new IGlobalGuiHandler() {
            public List<Rect2i> getGuiExtraAreas() {
               return Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> containerScreen
                  ? entry.getOcclusions((T)containerScreen)
                  : Collections.emptyList();
            }
         }
      );
   }
}
