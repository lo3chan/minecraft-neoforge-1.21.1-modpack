package net.astralya.hexalia.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public final class HexaliaEmiRecipeCategories {
   public static final EmiRecipeCategory MORTAR_AND_PESTLE = category("mortar_and_pestle", EmiStack.of((ItemLike)ModItems.MORTAR_AND_PESTLE.get()));
   public static final EmiRecipeCategory SMALL_CAULDRON = category("small_cauldron", EmiStack.of((ItemLike)ModItems.SMALL_CAULDRON.get()));
   public static final EmiRecipeCategory NATURES_RITUAL = category("ritual_table", EmiStack.of((ItemLike)ModItems.RITUAL_TABLE.get()));
   public static final EmiRecipeCategory CELESTIAL_INFUSION = category("ritual_brazier", EmiStack.of((ItemLike)ModItems.RITUAL_BRAZIER.get()));
   public static final EmiRecipeCategory MUTATION = category("mutation", EmiStack.of((ItemLike)ModItems.MUTAVIS.get()));

   private HexaliaEmiRecipeCategories() {
   }

   private static EmiRecipeCategory category(String path, EmiStack icon) {
      return new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath("hexalia", path), icon);
   }
}
