package io.github.razordevs.deep_aether.recipe.jei;

import com.aetherteam.aether.item.AetherItems;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.recipe.DARecipeTypes;
import java.util.Objects;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class DAJEIPlugin implements IModPlugin {
   @NotNull
   public ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("deep_aether", "jei");
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new PoisonRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new CombinerRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      registration.addRecipes(
         PoisonRecipeCategory.RECIPE_TYPE, rm.getAllRecipesFor((RecipeType)DARecipeTypes.POISON_RECIPE.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         CombinerRecipeCategory.RECIPE_TYPE, rm.getAllRecipesFor((RecipeType)DARecipeTypes.COMBINING.get()).stream().map(RecipeHolder::value).toList()
      );
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalysts(PoisonRecipeCategory.RECIPE_TYPE, new ItemLike[]{AetherItems.SKYROOT_POISON_BUCKET, DAItems.PLACEABLE_POISON_BUCKET});
      registration.addRecipeCatalyst(new ItemStack((ItemLike)DABlocks.COMBINER.get()), new mezz.jei.api.recipe.RecipeType[]{CombinerRecipeCategory.RECIPE_TYPE});
   }

   public void registerItemSubtypes(ISubtypeRegistration registration) {
      registration.registerSubtypeInterpreter((Item)DAItems.MOA_FODDER.get(), MoaFodderSubtypeInterpreter.INSTANCE);
   }
}
