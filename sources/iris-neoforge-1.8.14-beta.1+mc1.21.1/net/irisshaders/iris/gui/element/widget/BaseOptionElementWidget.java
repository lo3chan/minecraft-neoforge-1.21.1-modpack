package net.irisshaders.iris.gui.element.widget;

import java.util.Optional;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;

public abstract class BaseOptionElementWidget<T extends OptionMenuElement> extends CommentedElementWidget<T> {
   protected static final Component SET_TO_DEFAULT = Component.translatable("options.iris.setToDefault").withStyle(ChatFormatting.GREEN);
   protected static final Component DIVIDER = Component.literal(": ");
   protected MutableComponent unmodifiedLabel;
   protected ShaderPackScreen screen;
   protected NavigationController navigation;
   protected Component trimmedLabel;
   protected Component valueLabel;
   protected boolean usedKeyboard;
   private MutableComponent label;
   private boolean isLabelTrimmed;
   private int maxLabelWidth;
   private int valueSectionWidth;

   public BaseOptionElementWidget(T element) {
      super(element);
   }

   @Override
   public void init(ShaderPackScreen screen, NavigationController navigation) {
      this.screen = screen;
      this.navigation = navigation;
      this.valueLabel = null;
      this.trimmedLabel = null;
   }

   protected final void setLabel(MutableComponent label) {
      this.label = label.copy().append(DIVIDER);
      this.unmodifiedLabel = label;
   }

   protected final void updateRenderParams(int minValueSectionWidth) {
      this.usedKeyboard = this.isFocused();
      if (this.valueLabel == null) {
         this.valueLabel = this.createValueLabel();
      }

      Font font = Minecraft.getInstance().font;
      this.valueSectionWidth = Math.max(minValueSectionWidth, font.width(this.valueLabel) + 8);
      this.maxLabelWidth = this.bounds.width() - 8 - this.valueSectionWidth;
      if (this.trimmedLabel == null || font.width(this.label) > this.maxLabelWidth != this.isLabelTrimmed) {
         this.updateLabels();
      }

      this.isLabelTrimmed = font.width(this.label) > this.maxLabelWidth;
   }

   protected final void renderOptionWithValue(GuiGraphics guiGraphics, boolean hovered, float sliderPosition, int sliderWidth) {
      GuiUtil.bindIrisWidgetsTexture();
      GuiUtil.drawButton(guiGraphics, this.bounds.position().x(), this.bounds.position().y(), this.bounds.width(), this.bounds.height(), hovered, false);
      GuiUtil.drawButton(
         guiGraphics,
         this.bounds.getBoundInDirection(ScreenDirection.RIGHT) - (this.valueSectionWidth + 2),
         this.bounds.position().y() + 2,
         this.valueSectionWidth,
         this.bounds.height() - 4,
         false,
         true
      );
      if (sliderPosition >= 0.0F) {
         int sliderSpace = this.valueSectionWidth - 4 - sliderWidth;
         int sliderPos = this.bounds.getBoundInDirection(ScreenDirection.RIGHT) - this.valueSectionWidth + (int)(sliderPosition * sliderSpace);
         GuiUtil.drawButton(guiGraphics, sliderPos, this.bounds.position().y() + 4, sliderWidth, this.bounds.height() - 8, false, false);
      }

      Font font = Minecraft.getInstance().font;
      guiGraphics.drawString(font, this.trimmedLabel, this.bounds.position().x() + 6, this.bounds.position().y() + 7, 16777215);
      guiGraphics.drawString(
         font,
         this.valueLabel,
         this.bounds.getBoundInDirection(ScreenDirection.RIGHT) - 2 - (int)(this.valueSectionWidth * 0.5) - (int)(font.width(this.valueLabel) * 0.5),
         this.bounds.position().y() + 7,
         16777215
      );
   }

   protected final void renderOptionWithValue(GuiGraphics guiGraphics, boolean hovered) {
      this.renderOptionWithValue(guiGraphics, hovered, -1.0F, 0);
   }

   protected final void tryRenderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered) {
      if (Screen.hasShiftDown()) {
         this.renderTooltip(guiGraphics, SET_TO_DEFAULT, mouseX, mouseY, hovered);
      } else if (this.isLabelTrimmed && !this.screen.isDisplayingComment()) {
         this.renderTooltip(guiGraphics, this.unmodifiedLabel, mouseX, mouseY, hovered);
      }
   }

   protected final void renderTooltip(GuiGraphics guiGraphics, Component text, int mouseX, int mouseY, boolean hovered) {
      if (hovered) {
         ShaderPackScreen.TOP_LAYER_RENDER_QUEUE.add(() -> GuiUtil.drawTextPanel(Minecraft.getInstance().font, guiGraphics, text, mouseX + 2, mouseY - 16));
      }
   }

   protected final void updateLabels() {
      this.trimmedLabel = this.createTrimmedLabel();
      this.valueLabel = this.createValueLabel();
   }

   protected final Component createTrimmedLabel() {
      MutableComponent label = GuiUtil.shortenText(Minecraft.getInstance().font, this.label.copy(), this.maxLabelWidth);
      if (this.isValueModified()) {
         label = label.withStyle(style -> style.withColor(TextColor.fromRgb(16763210)));
      }

      return label;
   }

   protected abstract Component createValueLabel();

   public abstract boolean applyNextValue();

   public abstract boolean applyPreviousValue();

   public abstract boolean applyOriginalValue();

   public abstract boolean isValueModified();

   @Nullable
   public abstract String getCommentKey();

   @Override
   public Optional<Component> getCommentTitle() {
      return Optional.of(this.unmodifiedLabel);
   }

   @Override
   public Optional<Component> getCommentBody() {
      return Optional.ofNullable(this.getCommentKey()).map(key -> I18n.exists(key) ? Component.translatable(key) : null);
   }

   @Override
   public boolean mouseClicked(double mx, double my, int button) {
      if (button != 0 && button != 1) {
         return super.mouseClicked(mx, my, button);
      } else {
         boolean refresh = false;
         if (Screen.hasShiftDown()) {
            refresh = this.applyOriginalValue();
         }

         if (!refresh) {
            if (button == 0) {
               refresh = this.applyNextValue();
            } else {
               refresh = this.applyPreviousValue();
            }
         }

         if (refresh) {
            this.navigation.refresh();
         }

         GuiUtil.playButtonClickSound();
         return true;
      }
   }

   @Override
   public boolean keyPressed(int keycode, int scancode, int modifiers) {
      if (keycode == 257) {
         boolean refresh = Screen.hasControlDown() ? this.applyOriginalValue() : (Screen.hasShiftDown() ? this.applyPreviousValue() : this.applyNextValue());
         if (refresh) {
            this.navigation.refresh();
         }

         GuiUtil.playButtonClickSound();
         return true;
      } else {
         return false;
      }
   }
}
