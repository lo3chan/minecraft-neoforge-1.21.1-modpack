package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class ControllerWidget<T extends Controller<?>> extends AbstractWidget {
   protected final T control;
   protected MultiLineLabel wrappedTooltip;
   protected final YACLScreen screen;
   protected boolean focused = false;
   protected boolean hovered = false;
   protected final Component modifiedOptionName;
   protected final String optionNameString;

   public ControllerWidget(T control, YACLScreen screen, Dimension<Integer> dim) {
      super(dim);
      this.control = control;
      this.screen = screen;
      control.option().addListener((opt, pending) -> this.updateTooltip());
      this.updateTooltip();
      this.modifiedOptionName = control.option().name().copy().withStyle(ChatFormatting.ITALIC);
      this.optionNameString = control.option().name().getString().toLowerCase();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      this.hovered = this.isMouseOver(mouseX, mouseY);
      Component name = this.control.option().changed() ? this.modifiedOptionName : this.control.option().name();
      Component shortenedName = Component.literal(
            GuiUtils.shortenString(name.getString(), this.textRenderer, this.getDimension().width() - this.getControlWidth() - this.getXPadding() - 7, "...")
         )
         .setStyle(name.getStyle());
      this.drawButtonRect(
         graphics,
         this.getDimension().x(),
         this.getDimension().y(),
         this.getDimension().xLimit(),
         this.getDimension().yLimit(),
         this.hovered && this.isAvailable() || this.focused,
         this.isAvailable()
      );
      graphics.drawString(this.textRenderer, shortenedName, this.getDimension().x() + this.getXPadding(), this.getTextY(), this.getValueColor(), true);
      this.drawValueText(graphics, mouseX, mouseY, delta);
      if (this.isHovered()) {
         this.drawHoveredControl(graphics, mouseX, mouseY, delta);
      }
   }

   protected void drawHoveredControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
   }

   protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      Component valueText = this.getValueText();
      graphics.drawString(
         this.textRenderer,
         valueText,
         this.getDimension().xLimit() - this.textRenderer.width(valueText) - this.getXPadding(),
         this.getTextY(),
         this.getValueColor(),
         true
      );
   }

   private void updateTooltip() {
      this.wrappedTooltip = MultiLineLabel.create(this.textRenderer, this.control.option().tooltip(), this.screen.width / 3 * 2 - 10);
   }

   protected int getControlWidth() {
      return this.isHovered() ? this.getHoveredControlWidth() : this.getUnhoveredControlWidth();
   }

   public boolean isHovered() {
      return this.isAvailable() && (this.hovered || this.focused);
   }

   protected abstract int getHoveredControlWidth();

   protected int getUnhoveredControlWidth() {
      return this.textRenderer.width(this.getValueText());
   }

   protected int getXPadding() {
      return 5;
   }

   protected int getYPadding() {
      return 2;
   }

   protected Component getValueText() {
      return this.control.formatValue();
   }

   protected boolean isAvailable() {
      return this.control.option().available();
   }

   protected int getValueColor() {
      return this.isAvailable() ? -1 : -6250336;
   }

   @Override
   public boolean canReset() {
      return true;
   }

   protected int getTextY() {
      return (int)(this.getDimension().y().intValue() + this.getDimension().height().intValue() / 2.0F - 9.0F / 2.0F);
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
      return !this.isFocused() ? ComponentPath.leaf(this) : null;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void setFocused(boolean focused) {
      this.focused = focused;
   }

   @Override
   public void unfocus() {
      this.focused = false;
   }

   @Override
   public boolean matchesSearch(String query) {
      return this.optionNameString.contains(query.toLowerCase());
   }

   @Override
   public NarrationPriority narrationPriority() {
      return this.focused ? NarrationPriority.FOCUSED : (this.isHovered() ? NarrationPriority.HOVERED : NarrationPriority.NONE);
   }

   @Override
   public void updateNarration(NarrationElementOutput builder) {
      builder.add(NarratedElementType.TITLE, this.control.option().name());
      builder.add(NarratedElementType.HINT, this.control.option().tooltip());
   }
}
