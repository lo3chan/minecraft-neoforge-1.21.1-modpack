package net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.registration.IRecipeRegistration;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerVanillaRecipeTypeRegistration;
import net.blay09.mods.balm.world.item.crafting.DeferredRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public class JeiVanillaRecipeTypeRegistration<TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>>
   extends JeiRecipeTypeRegistration<TRecipe>
   implements RecipeViewerVanillaRecipeTypeRegistration<TRecipeInput, TRecipe> {
   private final List<Holder<RecipeType<TRecipe>>> recipeTypeHolders = new ArrayList<>();
   private final List<DeferredRecipeType<TRecipeInput, TRecipe>> deferredRecipeTypes = new ArrayList<>();

   public JeiVanillaRecipeTypeRegistration(ResourceLocation identifier, Class<TRecipe> recipeClass) {
      super(identifier, recipeClass);
   }

   @Override
   public RecipeViewerVanillaRecipeTypeRegistration<TRecipeInput, TRecipe> withSyncedRecipes(Holder<RecipeType<TRecipe>> recipeType) {
      this.recipeTypeHolders.add(recipeType);
      return this;
   }

   @Override
   public RecipeViewerVanillaRecipeTypeRegistration<TRecipeInput, TRecipe> withSyncedRecipes(DeferredRecipeType<TRecipeInput, TRecipe> recipeType) {
      this.deferredRecipeTypes.add(recipeType);
      return this;
   }

   @Override
   public void registerRecipes(IRecipeRegistration registration) {
      super.registerRecipes(registration);
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      if (connection != null) {
         RecipeManager recipeManager = connection.getRecipeManager();

         for (Holder<RecipeType<TRecipe>> recipeTypeHolder : this.recipeTypeHolders) {
            List<TRecipe> recipes = recipeManager.getAllRecipesFor((RecipeType)recipeTypeHolder.value()).stream().<TRecipe>map(RecipeHolder::value).toList();
            registration.addRecipes(this.jeiRecipeType, recipes);
         }

         for (DeferredRecipeType<TRecipeInput, TRecipe> deferredRecipeType : this.deferredRecipeTypes) {
            List<TRecipe> recipes = recipeManager.getAllRecipesFor(deferredRecipeType.type()).stream().<TRecipe>map(RecipeHolder::value).toList();
            registration.addRecipes(this.jeiRecipeType, recipes);
         }
      }
   }

   public boolean containsRecipeType(RecipeType<?> recipeType) {
      return this.recipeTypeHolders.stream().anyMatch(it -> it.value() == recipeType)
         || this.deferredRecipeTypes.stream().anyMatch(it -> it.type() == recipeType);
   }
}
