package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.OverlayLayer;
import net.mehvahdjukaar.moonlight.api.client.gui.PopupHost;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.DropdownWidget;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.IconButton;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

class ListEditScreen extends Screen implements PopupHost {
   private final Screen parent;
   private final Consumer<List<String>> onApply;
   private final ConfigOption.ListValue option;
   private final List<String> working;
   @Nullable
   private final List<String> options;
   private final OverlayLayer overlay = new OverlayLayer();
   private ConfigRowList list;

   ListEditScreen(ConfigOption.ListValue option, List<String> initial, Screen parent, Consumer<List<String>> onApply) {
      super(option.title());
      this.option = option;
      this.working = new ArrayList<>(initial);
      this.parent = parent;
      this.onApply = onApply;
      this.options = option.options == null ? null : option.options.get();
   }

   @Override
   public OverlayLayer getOverlayLayer() {
      return this.overlay;
   }

   protected void init() {
      this.overlay.clear();
      this.list = new ConfigRowList(this.minecraft, this.width, this.height - 44 - 58, 44, 24);
      this.rebuildRows();
      this.addRenderableWidget(this.list);
      int cx = this.width / 2;
      Component addLabel = Component.literal("+ ")
         .withStyle(ChatFormatting.AQUA)
         .append(Component.translatable("gui.moonlight.config.list_add").withStyle(ChatFormatting.RESET));
      this.addRenderableWidget(Button.builder(addLabel, b -> {
         this.working.add(this.options != null && !this.options.isEmpty() ? (String)this.options.getFirst() : "");
         this.rebuildRows();
         this.list.setScrollAmount(this.list.getMaxScroll());
      }).bounds(cx - 100, this.height - 52, 200, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
         this.onApply.accept(new ArrayList<>(this.working));
         this.onClose();
      }).bounds(cx - 100, this.height - 28, 96, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(cx + 4, this.height - 28, 96, 20).build());
   }

   private void rebuildRows() {
      this.overlay.clear();
      List<ConfigListRow> rows = new ArrayList<>();

      for (int i = 0; i < this.working.size(); i++) {
         rows.add(new ListEditScreen.EntryRow(i));
      }

      this.list.setRows(rows);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.overlay.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      return this.overlay.mouseScrolled(mouseX, mouseY, scrollY) ? true : super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
   }

   public boolean keyPressed(int key, int scanCode, int modifiers) {
      return this.overlay.keyPressed(key, scanCode, modifiers) ? true : super.keyPressed(key, scanCode, modifiers);
   }

   public boolean charTyped(char c, int modifiers) {
      return this.overlay.charTyped(c, modifiers) ? true : super.charTyped(c, modifiers);
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      GuiHelper.renderHeaderBar(graphics, this.font, this.title, this.width, 44);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      this.overlay.render(graphics, mouseX, mouseY);
   }

   private class EntryRow extends ConfigListRow {
      private final AbstractWidget editor;
      @Nullable
      private final EditBox box;
      private final Button remove;
      private final List<AbstractWidget> children;
      private final int index;

      EntryRow(int index) {
         this.index = index;
         int editorWidth = 256;
         if (ListEditScreen.this.options != null) {
            this.box = null;
            this.editor = new DropdownWidget(
               editorWidth,
               20,
               ListEditScreen.this.options,
               ListEditScreen.this.option.icon,
               ListEditScreen.this.working.get(index),
               v -> ListEditScreen.this.working.set(index, v)
            );
         } else {
            EditBox b = new EditBox(ListEditScreen.this.font, 0, 0, editorWidth, 20, Component.empty());
            b.setMaxLength(32767);
            b.setValue(ListEditScreen.this.working.get(index));
            b.setResponder(s -> {
               ListEditScreen.this.working.set(index, s);
               b.setTextColor(ListEditScreen.this.option.isValidEntry(b.getValue()) ? ConfigGuiColors.TEXT : ConfigGuiColors.ERROR);
            });
            b.setTextColor(ListEditScreen.this.option.isValidEntry(b.getValue()) ? ConfigGuiColors.TEXT : ConfigGuiColors.ERROR);
            this.box = b;
            this.editor = b;
         }

         this.remove = new IconButton(0, 0, 20, 20, Component.empty(), MoonlightIcons.DELETE, 12, 12, btn -> {
            ListEditScreen.this.working.remove(index);
            ListEditScreen.this.rebuildRows();
         });
         this.children = List.of(this.editor, this.remove);
      }

      public void render(GuiGraphics graphics, int i, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
         int cy = top + (height - 20) / 2;
         this.editor.setX(left);
         this.editor.setY(cy);
         this.editor.render(graphics, mouseX, mouseY, partialTick);
         this.remove.setX(left + width - 20);
         this.remove.setY(cy);
         this.remove.render(graphics, mouseX, mouseY, partialTick);
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      @Nullable
      @Override
      Component getTooltip(int mouseX, int mouseY) {
         return null;
      }
   }
}
