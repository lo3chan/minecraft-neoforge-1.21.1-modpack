package net.astralya.hexalia.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class HexaliaEmiRecipe implements EmiRecipe {
   private final EmiRecipeCategory category;
   private final HexaliaRecipeGuiLayout layout;
   private final ResourceLocation id;
   private final List<EmiIngredient> inputs;
   private final List<EmiStack> outputs;
   private final List<Component> recipeTooltips;
   private final List<Component> outputTooltips;
   private final boolean drawRitualBrazierFocus;
   private final boolean showRitualTableFocusTooltip;
   private final boolean showMutationTooltip;

   public HexaliaEmiRecipe(EmiRecipeCategory category, HexaliaRecipeGuiLayout layout, ResourceLocation id, List<EmiIngredient> inputs, EmiStack output) {
      this(category, layout, id, inputs, output, List.of(), List.of());
   }

   public HexaliaEmiRecipe(
      EmiRecipeCategory category,
      HexaliaRecipeGuiLayout layout,
      ResourceLocation id,
      List<EmiIngredient> inputs,
      EmiStack output,
      List<Component> recipeTooltips,
      List<Component> outputTooltips
   ) {
      this(category, layout, id, inputs, output, recipeTooltips, outputTooltips, false, false, false);
   }

   public HexaliaEmiRecipe(
      EmiRecipeCategory category,
      HexaliaRecipeGuiLayout layout,
      ResourceLocation id,
      List<EmiIngredient> inputs,
      EmiStack output,
      List<Component> recipeTooltips,
      List<Component> outputTooltips,
      boolean drawRitualBrazierFocus,
      boolean showRitualTableFocusTooltip,
      boolean showMutationTooltip
   ) {
      this.category = category;
      this.layout = layout;
      this.id = id;
      this.inputs = List.copyOf(inputs);
      this.outputs = List.of(output);
      this.recipeTooltips = List.copyOf(recipeTooltips);
      this.outputTooltips = List.copyOf(outputTooltips);
      this.drawRitualBrazierFocus = drawRitualBrazierFocus;
      this.showRitualTableFocusTooltip = showRitualTableFocusTooltip;
      this.showMutationTooltip = showMutationTooltip;
   }

   public EmiRecipeCategory getCategory() {
      return this.category;
   }

   @Nullable
   public ResourceLocation getId() {
      return this.id;
   }

   public List<EmiIngredient> getInputs() {
      return this.inputs;
   }

   public List<EmiStack> getOutputs() {
      return this.outputs;
   }

   public int getDisplayWidth() {
      return this.layout.width();
   }

   public int getDisplayHeight() {
      return this.layout.height();
   }

   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(
         this.layout.texture(),
         0,
         0,
         this.layout.width(),
         this.layout.height(),
         this.layout.textureU(),
         this.layout.textureV(),
         this.layout.textureWidth(),
         this.layout.textureHeight(),
         256,
         256
      );
      if (!this.recipeTooltips.isEmpty()) {
         widgets.addTooltipText(this.recipeTooltips, 0, 0, this.layout.width(), this.layout.height());
      }

      if (this.drawRitualBrazierFocus) {
         widgets.addTexture(this.layout.texture(), 50, 30, 16, 16, 118, 0);
         widgets.addTooltipText(List.of(Component.translatable("tooltip.hexalia.hex_focus_gui")), 3, 53, 16, 16);
      }

      if (this.showRitualTableFocusTooltip) {
         widgets.addTooltipText(List.of(Component.translatable("tooltip.hexalia.hex_focus_gui")), 3, 62, 16, 16);
      }

      if (this.showMutationTooltip) {
         widgets.addTooltipText(List.of(Component.translatable("tooltip.hexalia.mutation")), 4, 55, 16, 16);
      }

      for (int index = 0; index < this.inputs.size(); index++) {
         widgets.addSlot(this.inputs.get(index), this.layout.inputX(index), this.layout.inputY(index)).drawBack(false);
      }

      if (!this.outputs.isEmpty()) {
         SlotWidget outputSlot = widgets.addSlot((EmiIngredient)this.outputs.get(0), this.layout.outputX(), this.layout.outputY())
            .drawBack(false)
            .recipeContext(this);

         for (Component tooltip : this.outputTooltips) {
            outputSlot.appendTooltip(tooltip);
         }
      }
   }
}
