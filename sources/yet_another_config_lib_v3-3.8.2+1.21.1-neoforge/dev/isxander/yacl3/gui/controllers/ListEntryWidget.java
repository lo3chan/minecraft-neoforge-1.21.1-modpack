package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.ListOptionEntry;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ListEntryWidget extends AbstractWidget implements ContainerEventHandler {
   private final TooltipButtonWidget removeButton;
   private final TooltipButtonWidget moveUpButton;
   private final TooltipButtonWidget moveDownButton;
   private final AbstractWidget entryWidget;
   private final ListOption<?> listOption;
   private final ListOptionEntry<?> listOptionEntry;
   private final String optionNameString;
   private GuiEventListener focused;
   private boolean dragging;

   public ListEntryWidget(YACLScreen screen, ListOptionEntry<?> listOptionEntry, AbstractWidget entryWidget) {
      super(
         entryWidget.getDimension()
            .withHeight(
               Math.min(entryWidget.getDimension().height(), 20)
                  - (listOptionEntry.parentGroup().indexOf(listOptionEntry) == listOptionEntry.parentGroup().options().size() - 1 ? 0 : 2)
            )
      );
      this.listOptionEntry = listOptionEntry;
      this.listOption = listOptionEntry.parentGroup();
      this.optionNameString = listOptionEntry.name().getString().toLowerCase();
      this.entryWidget = entryWidget;
      Dimension<Integer> dim = entryWidget.getDimension();
      entryWidget.setDimension(dim.clone().move(40, 0).expand(-60, 0));
      this.removeButton = new TooltipButtonWidget(
         screen, dim.xLimit() - 20, dim.y(), 20, 20, Component.literal("❌"), Component.translatable("yacl.list.remove"), btn -> {
            this.listOption.removeEntry(listOptionEntry);
            this.updateButtonStates();
         }
      );
      this.moveUpButton = new TooltipButtonWidget(
         screen, dim.x(), dim.y(), 20, 20, Component.literal("↑"), Component.translatable("yacl.list.move_up"), btn -> {
            int index = this.listOption.indexOf(listOptionEntry) - 1;
            if (index >= 0) {
               this.listOption.removeEntry(listOptionEntry);
               this.listOption.insertEntry(index, listOptionEntry);
               this.updateButtonStates();
            }
         }
      );
      this.moveDownButton = new TooltipButtonWidget(
         screen, dim.x() + 20, dim.y(), 20, 20, Component.literal("↓"), Component.translatable("yacl.list.move_down"), btn -> {
            int index = this.listOption.indexOf(listOptionEntry) + 1;
            if (index < this.listOption.options().size()) {
               this.listOption.removeEntry(listOptionEntry);
               this.listOption.insertEntry(index, listOptionEntry);
               this.updateButtonStates();
            }
         }
      );
      this.updateButtonStates();
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      this.updateButtonStates();
      this.removeButton.setY(this.getDimension().y());
      this.moveUpButton.setY(this.getDimension().y());
      this.moveDownButton.setY(this.getDimension().y());
      this.entryWidget.setDimension(this.entryWidget.getDimension().withY(this.getDimension().y()));
      this.removeButton.render(graphics, mouseX, mouseY, delta);
      this.moveUpButton.render(graphics, mouseX, mouseY, delta);
      this.moveDownButton.render(graphics, mouseX, mouseY, delta);
      this.entryWidget.render(graphics, mouseX, mouseY, delta);
   }

   protected void updateButtonStates() {
      this.removeButton.active = this.listOption.available() && this.listOption.numberOfEntries() > this.listOption.minimumNumberOfEntries();
      this.moveUpButton.active = this.listOption.indexOf(this.listOptionEntry) > 0 && this.listOption.available();
      this.moveDownButton.active = this.listOption.indexOf(this.listOptionEntry) < this.listOption.options().size() - 1 && this.listOption.available();
   }

   @Override
   public void unfocus() {
      this.entryWidget.unfocus();
   }

   @Override
   public void updateNarration(NarrationElementOutput builder) {
      this.entryWidget.updateNarration(builder);
   }

   @Override
   public boolean matchesSearch(String query) {
      return this.optionNameString.contains(query.toLowerCase());
   }

   public List<? extends GuiEventListener> children() {
      return ImmutableList.of(this.moveUpButton, this.moveDownButton, this.entryWidget, this.removeButton);
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(@Nullable GuiEventListener focused) {
      this.focused = focused;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return super.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return super.mouseReleased(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
      return super.mouseDragged(mouseX, mouseY, button, dx, dy);
   }

   @Override
   public boolean keyPressed(int keycode, int scancode, int modifiers) {
      return super.keyPressed(keycode, scancode, modifiers);
   }

   @Override
   public boolean keyReleased(int keycode, int scancode, int modifiers) {
      return super.keyReleased(keycode, scancode, modifiers);
   }

   @Override
   public boolean charTyped(char codePoint, int modifiers) {
      return super.charTyped(codePoint, modifiers);
   }
}
