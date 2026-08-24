package com.mrcrayfish.configured.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.configured.client.screen.widget.IconButton;
import com.mrcrayfish.configured.client.util.ScreenUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public abstract class ListMenuScreen extends TooltipScreen {
   public static final ResourceLocation CONFIGURED_LOGO = ResourceLocation.fromNamespaceAndPath("configured", "textures/gui/logo.png");
   protected final Screen parent;
   protected final int itemHeight;
   protected ListMenuScreen.EntryList list;
   protected List<ListMenuScreen.Item> entries;
   protected ListMenuScreen.FocusedEditBox activeTextField;
   protected ListMenuScreen.FocusedEditBox searchTextField;

   protected ListMenuScreen(Screen parent, Component title, int itemHeight) {
      super(title);
      this.parent = parent;
      this.itemHeight = itemHeight;
   }

   protected abstract void constructEntries(List<ListMenuScreen.Item> var1);

   protected void init() {
      List<ListMenuScreen.Item> entries = new ArrayList<>();
      this.constructEntries(entries);
      this.entries = ImmutableList.copyOf(entries);
      this.list = new ListMenuScreen.EntryList(this.entries);
      this.addWidget(this.list);
      this.searchTextField = new ListMenuScreen.FocusedEditBox(this.font, this.width / 2 - 110, 22, 220, 20, Component.translatable("configured.gui.search"));
      this.searchTextField.setClearable(true);
      this.searchTextField.setResponder(s -> this.updateSearchResults());
      this.addWidget(this.searchTextField);
      ScreenUtil.updateSearchTextFieldSuggestion(this.searchTextField, "", this.entries);
   }

   protected void updateSearchResults() {
      String query = this.searchTextField.getValue();
      ScreenUtil.updateSearchTextFieldSuggestion(this.searchTextField, query, this.entries);
      this.list.replaceEntries((Collection<ListMenuScreen.Item>)(query.isEmpty() ? this.entries : this.getSearchResults(query)));
      if (!query.isEmpty()) {
         this.list.setScrollAmount(0.0);
      }
   }

   protected Collection<ListMenuScreen.Item> getSearchResults(String s) {
      return this.entries
         .stream()
         .filter(item -> !(item instanceof ListMenuScreen.IIgnoreSearch) && item.getLabel().toLowerCase(Locale.ENGLISH).contains(s.toLowerCase(Locale.ENGLISH)))
         .collect(Collectors.toList());
   }

   protected void updateTooltip(int mouseX, int mouseY) {
      if (ScreenUtil.isMouseWithin(10, 13, 23, 23, mouseX, mouseY)) {
         this.setActiveTooltip(Component.translatable("configured.gui.info"));
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      this.resetTooltip();
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.list.render(graphics, mouseX, mouseY, partialTicks);
      this.searchTextField.render(graphics, mouseX, mouseY, partialTicks);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 7, 16777215);
      this.renderForeground(graphics, mouseX, mouseY, partialTicks);
      graphics.blit(CONFIGURED_LOGO, 10, 13, 0, 0.0F, 0.0F, 23, 23, 32, 32);
      graphics.blit(IconButton.ICONS, this.width / 2 - 128, 26, 14, 14, 22.0F, 11.0F, 10, 10, 64, 64);
      this.updateTooltip(mouseX, mouseY);
      if (this.tooltipText != null) {
         this.drawTooltip(graphics, mouseX, mouseY);
      } else {
         for (GuiEventListener widget : this.children()) {
            if (widget instanceof Button && ((Button)widget).isHoveredOrFocused()) {
               break;
            }
         }
      }
   }

   protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (ScreenUtil.isMouseWithin(10, 13, 23, 23, (int)mouseX, (int)mouseY)) {
         Style style = Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured"));
         this.handleComponentClicked(style);
         return true;
      } else {
         if (this.activeTextField != null && !this.activeTextField.isMouseOver(mouseX, mouseY)) {
            this.activeTextField.setFocused(false);
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   protected class EntryList extends ContainerObjectSelectionList<ListMenuScreen.Item> {
      public EntryList(List<ListMenuScreen.Item> entries) {
         super(ListMenuScreen.this.minecraft, ListMenuScreen.this.width, ListMenuScreen.this.height - 36 - 50, 50, ListMenuScreen.this.itemHeight);
         entries.forEach(x$0 -> this.addEntry(x$0));
      }

      protected int getScrollbarPosition() {
         return this.width / 2 + 144;
      }

      public int getRowWidth() {
         return 260;
      }

      public void replaceEntries(Collection<ListMenuScreen.Item> entries) {
         super.replaceEntries(entries);
      }

      public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         super.renderWidget(graphics, mouseX, mouseY, partialTicks);
         this.renderToolTips(graphics, mouseX, mouseY);
      }

      private void renderToolTips(GuiGraphics graphics, int mouseX, int mouseY) {
         this.children().forEach(item -> item.children().forEach(o -> {
            if (o instanceof Button) {
            }
         }));
      }
   }

   protected class FocusedEditBox extends EditBox {
      private boolean clearable = false;

      public FocusedEditBox(Font font, int x, int y, int width, int height, Component label) {
         super(font, x, y, width, height, label);
      }

      public ListMenuScreen.FocusedEditBox setClearable(boolean clearable) {
         this.clearable = clearable;
         return this;
      }

      public void setFocused(boolean focused) {
         super.setFocused(focused);
         if (focused) {
            if (ListMenuScreen.this.activeTextField != null && ListMenuScreen.this.activeTextField != this) {
               ListMenuScreen.this.activeTextField.setFocused(false);
            }

            ListMenuScreen.this.activeTextField = this;
         }
      }

      public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
         super.renderWidget(graphics, mouseX, mouseY, partialTick);
         if (this.clearable && !this.getValue().isEmpty()) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            boolean hovered = ScreenUtil.isMouseWithin(this.getX() + this.width - 15, this.getY() + 5, 9, 9, mouseX, mouseY);
            graphics.blit(IconButton.ICONS, this.getX() + this.width - 15, this.getY() + 5, 9, 9, hovered ? 9.0F : 0.0F, 55.0F, 9, 9, 64, 64);
         }
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (this.clearable
            && !this.getValue().isEmpty()
            && button == 0
            && ScreenUtil.isMouseWithin(this.getX() + this.width - 15, this.getY() + 5, 9, 9, (int)mouseX, (int)mouseY)) {
            this.playDownSound(ListMenuScreen.this.minecraft.getSoundManager());
            this.setValue("");
            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }

   protected interface IIgnoreSearch {
   }

   protected abstract class Item extends Entry<ListMenuScreen.Item> implements ILabelProvider, Comparable<ListMenuScreen.Item> {
      protected final Component label;
      @Nullable
      protected List<FormattedCharSequence> tooltip;

      public Item(Component label) {
         this.label = label;
      }

      public Item(String label) {
         this.label = Component.literal(label);
      }

      @Override
      public String getLabel() {
         return this.label.getString();
      }

      public void render(GuiGraphics graphics, int x, int top, int left, int width, int height, int mouseX, int mouseY, boolean selected, float partialTicks) {
         if (this.isMouseOver(mouseX, mouseY)) {
            ListMenuScreen.this.setActiveTooltip(this.tooltip);
         }
      }

      public List<? extends GuiEventListener> children() {
         return Collections.emptyList();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            public NarrationPriority narrationPriority() {
               return NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput output) {
               output.add(NarratedElementType.TITLE, Item.this.label);
            }
         });
      }

      public int compareTo(ListMenuScreen.Item o) {
         return this.label.getString().compareTo(o.label.getString());
      }
   }

   public class MultiTextItem extends ListMenuScreen.Item implements ListMenuScreen.IIgnoreSearch {
      private final Component bottomText;

      public MultiTextItem(Component topText, Component bottomText) {
         super(topText);
         this.bottomText = bottomText;
      }

      @Override
      public void render(GuiGraphics graphics, int x, int top, int left, int width, int height, int mouseX, int mouseY, boolean selected, float partialTicks) {
         graphics.drawCenteredString(ListMenuScreen.this.minecraft.font, this.label, left + width / 2, top, -1);
         graphics.drawCenteredString(ListMenuScreen.this.minecraft.font, this.bottomText, left + width / 2, top + 12, -1);
         if (this.isMouseOver(mouseX, mouseY)) {
            Style style = this.bottomText.getStyle();
            HoverEvent event = style.getHoverEvent();
            if (event != null && event.getAction() == net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT) {
               ListMenuScreen.this.setActiveTooltip((Component)event.getValue(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT), -219136);
            }
         }
      }
   }

   public class TitleItem extends ListMenuScreen.Item implements ListMenuScreen.IIgnoreSearch {
      public TitleItem(Component title) {
         super(title);
      }

      public TitleItem(String title) {
         super(Component.literal(title).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.YELLOW));
      }

      @Override
      public void render(GuiGraphics graphics, int x, int top, int left, int width, int height, int mouseX, int mouseY, boolean selected, float partialTicks) {
         graphics.drawCenteredString(ListMenuScreen.this.minecraft.font, this.label, left + width / 2, top + 5, 16777215);
      }
   }
}
