package me.shedaniel.clothconfig2.gui;

import java.util.Optional;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.math.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ClothConfigTabButton extends AbstractButton {
   private final int index;
   private final ClothConfigScreen screen;
   @Nullable
   private final Supplier<Optional<FormattedText[]>> descriptionSupplier;

   public ClothConfigTabButton(
      ClothConfigScreen screen,
      int index,
      int int_1,
      int int_2,
      int int_3,
      int int_4,
      Component string_1,
      Supplier<Optional<FormattedText[]>> descriptionSupplier
   ) {
      super(int_1, int_2, int_3, int_4, string_1);
      this.index = index;
      this.screen = screen;
      this.descriptionSupplier = descriptionSupplier;
   }

   public ClothConfigTabButton(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Component string_1) {
      this(screen, index, int_1, int_2, int_3, int_4, string_1, null);
   }

   public void onPress() {
      if (this.index != -1) {
         this.screen.selectedCategoryIndex = this.index;
      }

      this.screen.init(Minecraft.getInstance(), this.screen.width, this.screen.height);
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      this.active = this.index != this.screen.selectedCategoryIndex;
      super.renderWidget(graphics, mouseX, mouseY, delta);
      if (this.isMouseOver(mouseX, mouseY)) {
         Optional<FormattedText[]> tooltip = this.getDescription();
         if (tooltip.isPresent() && tooltip.get().length > 0) {
            this.screen.addTooltip(Tooltip.of(new Point(mouseX, mouseY), tooltip.get()));
         }
      }
   }

   protected boolean clicked(double double_1, double double_2) {
      return this.visible && this.active && this.isMouseOver(double_1, double_2);
   }

   public boolean isMouseOver(double double_1, double double_2) {
      return this.visible
         && double_1 >= this.getX()
         && double_2 >= this.getY()
         && double_1 < this.getX() + this.width
         && double_2 < this.getY() + this.height
         && double_1 >= 20.0
         && double_1 < this.screen.width - 20;
   }

   public Optional<FormattedText[]> getDescription() {
      return this.descriptionSupplier != null ? this.descriptionSupplier.get() : Optional.empty();
   }

   public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }
}
