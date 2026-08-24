package net.Pandarix.compat.rei;

import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.Pandarix.BACommon;
import net.Pandarix.recipe.IdentifyingRecipe;
import net.Pandarix.util.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public record IdentifyingDisplay(RecipeHolder<IdentifyingRecipe> recipe) implements Display {
   public static final ResourceLocation TEXTURE = BACommon.createResource("textures/gui/rei_archeology_table_overlay.png");
   public static final CategoryIdentifier<IdentifyingDisplay> CATEGORY = CategoryIdentifier.of("betterarcheology", "identifying");

   public List<EntryIngredient> getInputEntries() {
      return List.of(
         EntryIngredients.ofIngredient((Ingredient)((IdentifyingRecipe)this.recipe.value()).getIngredients().getFirst()),
         EntryIngredients.ofItemTag(ModTags.Items.BRUSHES)
      );
   }

   public List<EntryIngredient> getOutputEntries() {
      return List.of(EntryIngredients.of(((IdentifyingRecipe)this.recipe.value()).getResult()));
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return CATEGORY;
   }

   public Optional<ResourceLocation> getDisplayLocation() {
      return Optional.of(TEXTURE);
   }
}
