package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BooleanController implements Controller<Boolean> {
   public static final Function<Boolean, Component> ON_OFF_FORMATTER = state -> state ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
   public static final Function<Boolean, Component> TRUE_FALSE_FORMATTER = state -> state
      ? Component.translatable("yacl.control.boolean.true")
      : Component.translatable("yacl.control.boolean.false");
   public static final Function<Boolean, Component> YES_NO_FORMATTER = state -> state ? CommonComponents.GUI_YES : CommonComponents.GUI_NO;
   private final Option<Boolean> option;
   private final ValueFormatter<Boolean> valueFormatter;
   private final boolean coloured;

   public BooleanController(Option<Boolean> option) {
      this(option, ON_OFF_FORMATTER, false);
   }

   public BooleanController(Option<Boolean> option, boolean coloured) {
      this(option, ON_OFF_FORMATTER, coloured);
   }

   public BooleanController(Option<Boolean> option, Function<Boolean, Component> valueFormatter, boolean coloured) {
      this.option = option;
      this.valueFormatter = valueFormatter::apply;
      this.coloured = coloured;
   }

   @Internal
   public static BooleanController createInternal(Option<Boolean> option, ValueFormatter<Boolean> formatter, boolean coloured) {
      return new BooleanController(option, formatter::format, coloured);
   }

   @Override
   public Option<Boolean> option() {
      return this.option;
   }

   @Override
   public Component formatValue() {
      return this.valueFormatter.format(this.option().pendingValue());
   }

   public boolean coloured() {
      return this.coloured;
   }

   @Override
   public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new BooleanController.BooleanControllerElement(this, screen, widgetDimension);
   }

   public static class BooleanControllerElement extends ControllerWidget<BooleanController> {
      public BooleanControllerElement(BooleanController control, YACLScreen screen, Dimension<Integer> dim) {
         super(control, screen, dim);
      }

      @Override
      protected void drawHoveredControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      }

      @Override
      protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
         super.drawValueText(graphics, mouseX, mouseY, delta);
         if (this.hovered) {
         }
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int button) {
         if (this.isMouseOver(mouseX, mouseY) && this.isAvailable()) {
            this.toggleSetting();
            return true;
         } else {
            return false;
         }
      }

      @Override
      protected int getHoveredControlWidth() {
         return this.getUnhoveredControlWidth();
      }

      public void toggleSetting() {
         this.control.option().requestSet(!this.control.option().pendingValue());
         this.playDownSound();
      }

      @Override
      protected Component getValueText() {
         return (Component)(this.control.coloured()
            ? super.getValueText().copy().withStyle(this.control.option().pendingValue() ? ChatFormatting.GREEN : ChatFormatting.RED)
            : super.getValueText());
      }

      @Override
      public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
         if (!this.isFocused()) {
            return false;
         } else if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
            return false;
         } else {
            this.toggleSetting();
            return true;
         }
      }
   }
}
