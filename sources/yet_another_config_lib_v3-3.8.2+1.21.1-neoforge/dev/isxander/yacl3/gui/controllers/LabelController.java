package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import java.util.List;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.HoverEvent.EntityTooltipInfo;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LabelController implements Controller<Component> {
   private final Option<Component> option;

   public LabelController(Option<Component> option) {
      this.option = option;
   }

   @Override
   public Option<Component> option() {
      return this.option;
   }

   @Override
   public Component formatValue() {
      return this.option().pendingValue();
   }

   @Override
   public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new LabelController.LabelControllerElement(screen, widgetDimension);
   }

   public class LabelControllerElement extends AbstractWidget {
      private List<FormattedCharSequence> wrappedText;
      protected MultiLineLabel wrappedTooltip;
      protected boolean focused;
      protected final YACLScreen screen;

      public LabelControllerElement(YACLScreen screen, Dimension<Integer> dim) {
         super(dim);
         this.screen = screen;
         LabelController.this.option().addListener((opt, pending) -> this.updateTooltip());
         this.updateTooltip();
         this.updateText();
      }

      public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
         this.updateText();
         int y = this.getDimension().y();

         for (FormattedCharSequence text : this.wrappedText) {
            graphics.drawString(
               this.textRenderer,
               text,
               this.getDimension().x() + this.getXPadding(),
               y + this.getYPadding(),
               LabelController.this.option().available() ? -1 : -6250336,
               true
            );
            y += 9;
         }

         if (this.isFocused()) {
            graphics.fill(this.getDimension().x() - 1, this.getDimension().y() - 1, this.getDimension().xLimit() + 1, this.getDimension().y(), -1);
            graphics.fill(this.getDimension().x() - 1, this.getDimension().y() - 1, this.getDimension().x(), this.getDimension().yLimit() + 1, -1);
            graphics.fill(this.getDimension().x() - 1, this.getDimension().yLimit(), this.getDimension().xLimit() + 1, this.getDimension().yLimit() + 1, -1);
            graphics.fill(this.getDimension().xLimit(), this.getDimension().y() - 1, this.getDimension().xLimit() + 1, this.getDimension().yLimit() + 1, -1);
         }

         GuiUtils.pushPose(graphics);
         GuiUtils.translateZ(graphics, 100.0F);
         if (this.isMouseOver(mouseX, mouseY)) {
            Style style = this.getStyle(mouseX, mouseY);
            if (style != null && style.getHoverEvent() != null) {
               HoverEvent hoverEvent = style.getHoverEvent();
               ItemStackInfo itemStackContent = (ItemStackInfo)hoverEvent.getValue(Action.SHOW_ITEM);
               EntityTooltipInfo entityContent = (EntityTooltipInfo)hoverEvent.getValue(Action.SHOW_ENTITY);
               Component text = (Component)hoverEvent.getValue(Action.SHOW_TEXT);
               if (itemStackContent != null) {
                  ItemStack stack = itemStackContent.getItemStack();
                  this.renderItemStackTooltip(graphics, mouseX, mouseY, stack);
               } else if (entityContent != null) {
                  this.renderEntityTooltip(graphics, mouseX, mouseY, entityContent);
               } else if (text != null) {
                  this.renderTextTooltip(graphics, mouseX, mouseY, text);
               }
            }
         }

         GuiUtils.popPose(graphics);
      }

      private void renderItemStackTooltip(GuiGraphics graphics, int mouseX, int mouseY, ItemStack itemStack) {
         graphics.renderTooltip(this.textRenderer, Screen.getTooltipFromItem(this.client, itemStack), itemStack.getTooltipImage(), mouseX, mouseY);
      }

      private void renderEntityTooltip(GuiGraphics graphics, int mouseX, int mouseY, EntityTooltipInfo entity) {
         if (this.client.options.advancedItemTooltips) {
            graphics.renderComponentTooltip(this.textRenderer, entity.getTooltipLines(), mouseX, mouseY);
         }
      }

      private void renderTextTooltip(GuiGraphics graphics, int mouseX, int mouseY, Component text) {
         MultiLineLabel multilineText = MultiLineLabel.create(this.textRenderer, text, this.getDimension().width());
         YACLScreen.renderMultilineTooltip(
            graphics,
            this.textRenderer,
            multilineText,
            this.getDimension().centerX(),
            this.getDimension().y(),
            this.getDimension().yLimit(),
            this.screen.width,
            this.screen.height
         );
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int button) {
         if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
         } else {
            Style style = this.getStyle((int)mouseX, (int)mouseY);
            return style == null ? false : this.screen.handleComponentClicked(style);
         }
      }

      @Nullable
      protected Style getStyle(int mouseX, int mouseY) {
         if (!this.getDimension().isPointInside(mouseX, mouseY)) {
            return null;
         } else {
            int x = mouseX - this.getDimension().x() - this.getXPadding();
            int y = mouseY - this.getDimension().y() - this.getYPadding();
            int line = y / 9;
            if (x < 0 || x > this.getDimension().xLimit()) {
               return null;
            } else if (y < 0 || y > this.getDimension().yLimit()) {
               return null;
            } else {
               return line >= 0 && line < this.wrappedText.size() ? this.textRenderer.getSplitter().componentStyleAtWidth(this.wrappedText.get(line), x) : null;
            }
         }
      }

      private int getXPadding() {
         return 4;
      }

      private int getYPadding() {
         return 3;
      }

      private void updateText() {
         this.wrappedText = this.textRenderer.split(LabelController.this.formatValue(), this.getDimension().width() - this.getXPadding() * 2);
         this.setDimension(this.getDimension().withHeight(this.wrappedText.size() * 9 + this.getYPadding() * 2));
      }

      private void updateTooltip() {
         this.wrappedTooltip = MultiLineLabel.create(this.textRenderer, LabelController.this.option().tooltip(), this.screen.width / 3 * 2 - 10);
      }

      @Override
      public boolean matchesSearch(String query) {
         return LabelController.this.formatValue().getString().toLowerCase().contains(query.toLowerCase());
      }

      @Nullable
      public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
         if (!LabelController.this.option().available()) {
            return null;
         } else {
            return !this.isFocused() ? ComponentPath.leaf(this) : null;
         }
      }

      public boolean isFocused() {
         return this.focused;
      }

      public void setFocused(boolean focused) {
         this.focused = focused;
      }

      @Override
      public void updateNarration(NarrationElementOutput builder) {
         builder.add(NarratedElementType.TITLE, LabelController.this.formatValue());
      }

      @Override
      public NarrationPriority narrationPriority() {
         return NarrationPriority.FOCUSED;
      }
   }
}
