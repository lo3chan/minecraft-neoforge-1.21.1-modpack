package com.iafenvoy.jupiter.render.screen.dialog;

import com.iafenvoy.jupiter.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumSelectWidget<T extends Enum<T>> extends ObjectSelectionList<EnumSelectWidget.Entry<T>> {
   private final EnumSelectDialog<T> dialog;
   private final List<EnumSelectWidget.Entry<T>> entries = new ArrayList<>();

   public EnumSelectWidget(EnumSelectDialog<T> dialog, Minecraft client, int width, int height, int y) {
      super(client, width, height, y, 14);
      this.dialog = dialog;
   }

   public void update() {
      this.entries.clear();

      for (T x : (Enum[])this.dialog.getEntry().getDefaultValue().getDeclaringClass().getEnumConstants()) {
         this.entries.add(new EnumSelectWidget.Entry<>(this, x));
      }

      this.updateEntries();
      this.setSelected(this.entries.get(this.dialog.getEntry().getValue().ordinal()));
   }

   private void updateEntries() {
      this.clearEntries();
      this.entries.forEach(x$0 -> this.addEntry(x$0));
      this.setScrollAmount(0.0);
   }

   public void setSelected(@Nullable EnumSelectWidget.Entry<T> selected) {
      super.setSelected(selected);
      if (selected != null) {
         this.dialog.getEntry().setValue(selected.value);
      }
   }

   protected int getScrollbarPosition() {
      return this.getRight() - 8;
   }

   public int getRowWidth() {
      return this.width - 4;
   }

   public static class Entry<T extends Enum<T>> extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<EnumSelectWidget.Entry<T>> {
      private final Minecraft client = Minecraft.getInstance();
      private final EnumSelectWidget<T> widget;
      private final T value;

      public Entry(EnumSelectWidget<T> widget, T value) {
         this.widget = widget;
         this.value = value;
      }

      public void render(
         @NotNull GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick
      ) {
         graphics.drawCenteredString(this.client.font, this.value.name(), x + this.widget.width / 2, y + 1, -1);
      }

      public boolean mouseClicked(double x, double y, int button) {
         this.widget.setSelected(this);
         return false;
      }

      @NotNull
      public Component getNarration() {
         return TextUtil.empty();
      }
   }
}
