package me.flashyreese.mods.reeses_sodium_options.client.gui.widget;

import java.util.function.UnaryOperator;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiThemes;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlatButtonWidget extends BaseWidget {
   public static final GuiTheme DEFAULT_THEME = GuiThemes.DEFAULT_BUTTON;
   private final Component label;
   private final Runnable action;
   private final boolean drawBackground;
   private final boolean drawFrame;
   private final boolean leftAlign;
   private final GuiTheme theme;
   private UnaryOperator<Component> labelDecorator = UnaryOperator.identity();
   private boolean selected;
   private boolean enabled = true;
   private boolean visible = true;

   public FlatButtonWidget(LayoutBounds dim, Component label, Runnable action, boolean drawBackground, boolean leftAlign) {
      this(dim, label, action, drawBackground, !drawBackground, leftAlign, DEFAULT_THEME);
   }

   public FlatButtonWidget(LayoutBounds dim, Component label, Runnable action, boolean drawBackground, boolean drawFrame, boolean leftAlign, GuiTheme theme) {
      super(dim);
      this.label = label;
      this.action = action;
      this.drawBackground = drawBackground;
      this.drawFrame = drawFrame;
      this.leftAlign = leftAlign;
      this.theme = theme;
   }

   @Override
   public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      if (this.visible) {
         this.hovered = this.isMouseOver(mouseX, mouseY);
         if (this.drawBackground) {
            this.drawRect(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), this.backgroundColor());
         }

         Component label = this.labelDecorator.apply(this.label);
         int textWidth = this.font.width(label);
         int textX = this.leftAlign ? this.getX() + 8 : this.getCenterX() - textWidth / 2;
         int textY = this.getCenterY() - 9 / 2;
         this.drawString(guiGraphics, label, textX, textY, this.textColor());
         if (this.enabled && this.selected) {
            this.drawRect(guiGraphics, this.getX(), this.getLimitY() - 1, this.getLimitX(), this.getLimitY(), -7019309);
         }

         if (this.drawFrame || this.enabled && this.shouldRenderFocusBorder()) {
            this.drawBorder(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -2147418130);
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.enabled && this.visible && button == 0 && this.isMouseOver(mouseX, mouseY)) {
         this.doAction();
         return true;
      } else {
         return false;
      }
   }

   public boolean tryPress() {
      if (this.enabled && this.visible) {
         this.doAction();
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.enabled && this.visible && this.isFocused() && isSelectionKey(keyCode)) {
         this.doAction();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   @Override
   public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
      return this.enabled && this.visible ? super.nextFocusPath(navigation) : null;
   }

   public boolean isActive() {
      return this.enabled && this.visible;
   }

   @Override
   public void updateNarration(NarrationElementOutput builder) {
      this.addButtonNarration(builder, this.label);
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setLabelDecorator(@Nullable UnaryOperator<Component> labelDecorator) {
      this.labelDecorator = labelDecorator == null ? UnaryOperator.identity() : labelDecorator;
   }

   private int backgroundColor() {
      if (!this.enabled) {
         return this.theme.bgInactive;
      } else {
         return this.hovered ? this.theme.bgHighlight : this.theme.bgDefault;
      }
   }

   private int textColor() {
      return this.enabled ? this.theme.themeLighter : this.theme.themeDarker;
   }

   private void doAction() {
      this.action.run();
      this.playClickSound();
   }
}
