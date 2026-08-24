package vazkii.psi.client.jei;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.StringJoiner;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.recipe.ITrickRecipe;
import vazkii.psi.client.jei.crafting.BulletToDriveExtension;
import vazkii.psi.client.jei.crafting.DriveDuplicateExtension;
import vazkii.psi.client.jei.tricks.TrickCraftingCategory;
import vazkii.psi.common.Psi;
import vazkii.psi.common.crafting.recipe.BulletToDriveRecipe;
import vazkii.psi.common.crafting.recipe.DriveDuplicateRecipe;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModItems;

@JeiPlugin
public class JEICompat implements IModPlugin {
   private static final ResourceLocation UID = Psi.location("main");
   public static IJeiHelpers helpers;

   @NotNull
   public ResourceLocation getPluginUid() {
      return UID;
   }

   public void registerCategories(IRecipeCategoryRegistration registry) {
      helpers = registry.getJeiHelpers();
      registry.addRecipeCategories(new IRecipeCategory[]{new TrickCraftingCategory(helpers.getGuiHelper())});
   }

   public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
      registration.getCraftingCategory().addExtension(BulletToDriveRecipe.class, new BulletToDriveExtension());
      registration.getCraftingCategory().addExtension(DriveDuplicateRecipe.class, new DriveDuplicateExtension());
   }

   public void registerRecipes(@NotNull IRecipeRegistration registration) {
      List<ITrickRecipe> trickRecipes = new ArrayList<>();
      if (Minecraft.getInstance().level != null) {
         for (RecipeHolder<?> holder : Minecraft.getInstance().level.getRecipeManager().getRecipes()) {
            if (holder.value() instanceof ITrickRecipe recipe) {
               trickRecipes.add(recipe);
            }
         }

         registration.addRecipes(TrickCraftingCategory.TYPE, trickRecipes);
      }
   }

   public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
      for (ItemStack stack : ItemCAD.getCreativeTabItems()) {
         registration.addRecipeCatalyst(stack, new RecipeType[]{TrickCraftingCategory.TYPE});
      }
   }

   public void registerItemSubtypes(ISubtypeRegistration registration) {
      registration.registerSubtypeInterpreter((Item)ModItems.cad.get(), JEICompat.Cad.INSTANCE);
   }

   private static class Cad implements ISubtypeInterpreter<ItemStack> {
      public static final JEICompat.Cad INSTANCE = new JEICompat.Cad();

      public String getSubtypeData(ItemStack itemStack, @NotNull UidContext context) {
         ItemCAD cad = (ItemCAD)itemStack.getItem();
         List<String> strings = new ArrayList<>();

         for (EnumCADComponent c : EnumSet.allOf(EnumCADComponent.class)) {
            String s = c.getName() + "." + cad.getComponentInSlot(itemStack, c).getItem().getDescriptionId();
            strings.add(s);
         }

         StringJoiner joiner = new StringJoiner(",", "[", "]");
         strings.sort(null);

         for (String s : strings) {
            joiner.add(s);
         }

         return joiner.toString();
      }

      @Deprecated(
         since = "19.9.0"
      )
      @NotNull
      public String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context) {
         return "";
      }
   }
}
