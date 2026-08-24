package me.flashyreese.mods.reeses_sodium_options.client.gui.widget;

import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuideProvider;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LabelWidget extends BaseWidget implements ControlGuideProvider {
   private static final int TEXT_PADDING = 5;
   private static final int CHEVRON_GAP = 4;
   private static final String CHEVRON_COLLAPSED = "▶";
   private static final String CHEVRON_EXPANDED = "▼";
   private final Component text;
   private final int color;
   @Nullable
   private final GuiTheme theme;
   @Nullable
   private final ResourceLocation collapseKey;
   @Nullable
   private final Runnable onToggle;
   private boolean collapsed;

   public LabelWidget(LayoutBounds dim, Component text, int color) {
      this(dim, text, color, null, null, null, false);
   }

   public LabelWidget(LayoutBounds dim, Component text, int color, @Nullable ResourceLocation collapseKey, @Nullable Runnable onToggle, boolean collapsed) {
      this(dim, text, color, null, collapseKey, onToggle, collapsed);
   }

   public LabelWidget(
      LayoutBounds dim,
      Component text,
      int color,
      @Nullable GuiTheme theme,
      @Nullable ResourceLocation collapseKey,
      @Nullable Runnable onToggle,
      boolean collapsed
   ) {
      super(dim);
      this.text = text;
      this.color = color;
      this.theme = theme;
      this.collapseKey = collapseKey;
      this.onToggle = onToggle;
      this.collapsed = collapsed;
   }

   @Nullable
   public ResourceLocation collapseKey() {
      return this.collapseKey;
   }

   public void setCollapsed(boolean collapsed) {
      this.collapsed = collapsed;
   }

   @Override
   public List<ControlGuide> controlGuides() {
      return this.onToggle != null && this.isFocused()
         ? List.of(ControlGuide.press(Component.translatable(this.collapsed ? "rso.controller.guide.expand" : "rso.controller.guide.collapse")))
         : List.of();
   }

   @Override
   public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      if (this.onToggle == null) {
         int textWidth = this.getStringWidth(this.text);
         int x = this.getCenterX() - textWidth / 2;
         this.drawString(guiGraphics, this.text, x, this.getY(), this.color);
      } else {
         this.hovered = this.isMouseOver(mouseX, mouseY);
         if (this.theme != null) {
            this.drawRect(
               guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), this.hovered ? this.theme.bgHighlight : this.theme.bgDefault
            );
         }

         int textY = (int)(this.getY() + Math.ceil((this.getHeight() - 9) / 2.0));
         String chevron = this.collapsed ? "▶" : "▼";
         int chevronX = this.getX() + 5;
         this.drawString(guiGraphics, chevron, chevronX, textY, this.color);
         this.drawString(guiGraphics, this.text, chevronX + this.font.width(chevron) + 4, textY, this.color);
         if (this.shouldRenderFocusBorder()) {
            this.drawBorder(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -1);
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.onToggle != null && button == 0 && this.isMouseOver(mouseX, mouseY)) {
         this.onToggle.run();
         this.playClickSound();
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.onToggle != null && this.isFocused() && isSelectionKey(keyCode)) {
         this.onToggle.run();
         this.playClickSound();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   @Override
   public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
      return this.onToggle != null && !this.isFocused() ? ComponentPath.leaf(this) : null;
   }

   public boolean isActive() {
      return this.onToggle != null;
   }

   @Override
   public void updateNarration(NarrationElementOutput builder) {
      if (this.onToggle != null) {
         Component state = Component.translatable(this.collapsed ? "rso.narration.collapsed" : "rso.narration.expanded");
         this.addButtonNarration(builder, CommonComponents.optionNameValue(this.text, state));
      }
   }

   @Override
   public boolean isFocused() {
      return this.onToggle != null && this.focused;
   }

   @Override
   public void setFocused(boolean focused) {
      if (this.onToggle != null) {
         this.focused = focused;
      }
   }
}
