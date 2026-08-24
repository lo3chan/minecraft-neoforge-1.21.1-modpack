package net.astralya.hexalia.compat.rei;

import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class HexaliaReiDisplay extends BasicDisplay {
   private final CategoryIdentifier<HexaliaReiDisplay> category;
   private final HexaliaReiDisplay.Layout layout;
   private final List<Component> recipeTooltips;

   public HexaliaReiDisplay(
      CategoryIdentifier<HexaliaReiDisplay> category, HexaliaReiDisplay.Layout layout, List<EntryIngredient> inputs, List<EntryIngredient> outputs
   ) {
      this(category, layout, inputs, outputs, List.of(), Optional.empty());
   }

   public HexaliaReiDisplay(
      CategoryIdentifier<HexaliaReiDisplay> category,
      HexaliaReiDisplay.Layout layout,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs,
      Optional<ResourceLocation> location
   ) {
      this(category, layout, inputs, outputs, List.of(), location);
   }

   public HexaliaReiDisplay(
      CategoryIdentifier<HexaliaReiDisplay> category,
      HexaliaReiDisplay.Layout layout,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs,
      List<Component> recipeTooltips
   ) {
      this(category, layout, inputs, outputs, recipeTooltips, Optional.empty());
   }

   private HexaliaReiDisplay(
      CategoryIdentifier<HexaliaReiDisplay> category,
      HexaliaReiDisplay.Layout layout,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs,
      List<Component> recipeTooltips,
      Optional<ResourceLocation> location
   ) {
      super(inputs, outputs, location);
      this.category = category;
      this.layout = layout;
      this.recipeTooltips = List.copyOf(recipeTooltips);
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return this.category;
   }

   public HexaliaReiDisplay.Layout getLayout() {
      return this.layout;
   }

   public List<Component> getRecipeTooltips() {
      return this.recipeTooltips;
   }

   public static enum Layout {
      MORTAR_AND_PESTLE,
      SMALL_CAULDRON,
      NATURES_RITUAL,
      CELESTIAL_INFUSION,
      MUTATION;
   }
}
