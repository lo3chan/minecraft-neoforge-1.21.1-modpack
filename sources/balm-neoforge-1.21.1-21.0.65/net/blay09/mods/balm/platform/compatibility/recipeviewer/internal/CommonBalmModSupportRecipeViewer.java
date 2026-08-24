package net.blay09.mods.balm.platform.compatibility.recipeviewer.internal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.BalmModSupportRecipeViewer;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerInfoProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CommonBalmModSupportRecipeViewer implements BalmModSupportRecipeViewer {
   private final Map<ResourceLocation, RecipeViewerInfoProvider> providers = new ConcurrentHashMap<>();
   @Nullable
   private Supplier<Boolean> hasKeyboardFocus;

   @Override
   public void register(ResourceLocation identifier, RecipeViewerInfoProvider provider) {
      this.providers.put(identifier, provider);
   }

   @Override
   public boolean hasKeyboardFocus() {
      return this.hasKeyboardFocus != null && this.hasKeyboardFocus.get();
   }

   public Collection<RecipeViewerInfoProvider> getProviders() {
      return this.providers.values();
   }

   public void setHasKeyboardFocus(@Nullable Supplier<Boolean> hasKeyboardFocus) {
      this.hasKeyboardFocus = hasKeyboardFocus;
   }
}
