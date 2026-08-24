package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.components.base.ListWidget;
import com.teamresourceful.resourcefulconfig.client.screens.base.OverlayScreen;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SelectWidget extends BaseWidget {
   private static final int WIDTH = 80;
   private static final int FOCUSED_LIST = 80;
   private final Component heading;
   private final Enum<?>[] options;
   private final Supplier<Enum<?>[]> getter;
   private final Consumer<Enum<?>[]> setter;

   public SelectWidget(Component heading, Enum<?>[] options, Supplier<Enum<?>[]> getter, Consumer<Enum<?>[]> setter) {
      super(80, 16);
      this.heading = heading;
      this.options = options;
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.ofButton(this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
      renderScrollingString(
         graphics, this.font, this.heading, this.getX() + 4, this.getY() + 4, this.getX() + this.getWidth() - 16, this.getY() + this.getHeight() - 4, -9276296
      );
      graphics.blitSprite(ModSprites.CHEVRON_DOWN, this.getX() + this.getWidth() - 12, this.getY() + 4, 8, 8);
   }

   public void onClick(double d, double e) {
      Minecraft.getInstance().setScreen(new SelectWidget.SelectOverlay(this));
   }

   private static class SelectItem extends BaseWidget implements ListWidget.Item {
      private final Enum<?> option;
      private final BooleanSupplier selected;
      private final Consumer<Enum<?>> setter;

      public SelectItem(Enum<?> option, BooleanSupplier selected, Consumer<Enum<?>> setter) {
         super(80, 12);
         this.option = option;
         this.selected = selected;
         this.setter = setter;
      }

      @Override
      protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         graphics.blitSprite(ModSprites.ofButton(this.isHovered()), this.getX() + 1, this.getY(), this.getWidth() - 1, this.getHeight());
         if (this.selected.getAsBoolean()) {
            graphics.blitSprite(ModSprites.CHECK, this.getX() + 4, this.getY() + 2, 8, 8);
         }

         int color = this.isHovered() ? -329226 : -9276296;
         renderScrollingString(
            graphics,
            this.font,
            Translatable.toComponent(this.option),
            this.getX() + 16,
            this.getY() + 1,
            this.getX() + this.getWidth() - 4,
            this.getY() + this.getHeight() - 1,
            color
         );
      }

      public void onClick(double mouseX, double e) {
         this.setter.accept(this.option);
      }

      @Override
      public void setItemWidth(int width) {
         this.setWidth(width);
      }
   }

   private static class SelectList extends ListWidget {
      public SelectList(int x, int y, int height) {
         super(x + 1, y, 78, height);
      }

      public static SelectWidget.SelectList of(SelectWidget widget) {
         int windowHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
         int widgetY = widget.getY() + widget.getHeight();
         int listHeight = Math.min(widget.options.length * 12, 96) + 1;
         if (widgetY + listHeight > windowHeight) {
            widgetY = widget.getY() - listHeight - 1;
         }

         return new SelectWidget.SelectList(widget.getX(), widgetY, listHeight);
      }

      @Override
      public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         graphics.blitSprite(ModSprites.ACCENT, this.getX() - 1, this.getY() - 1, this.getWidth() + 2, this.getHeight() + 2);
         graphics.blitSprite(ModSprites.BUTTON, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         super.renderWidget(graphics, mouseX, mouseY, partialTicks);
      }
   }

   private static class SelectOverlay extends OverlayScreen {
      private final SelectWidget widget;

      protected SelectOverlay(SelectWidget widget) {
         super(Minecraft.getInstance().screen);
         this.widget = widget;
      }

      @Override
      protected void init() {
         SelectWidget.SelectList list = (SelectWidget.SelectList)this.addRenderableWidget(SelectWidget.SelectList.of(this.widget));

         for (Enum<?> option : this.widget.options) {
            list.add(new SelectWidget.SelectItem(option, () -> {
               Set<Enum<?>> set = Set.of(this.widget.getter.get());
               return set.contains(option);
            }, value -> {
               Set<Enum<?>> set = new HashSet<>(Set.of(this.widget.getter.get()));
               if (set.contains(value)) {
                  set.remove(value);
               } else {
                  set.add(value);
               }

               this.widget.setter.accept(set.toArray(new Enum[0]));
            }));
         }
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (this.getChildAt(mouseX, mouseY).isEmpty()) {
            this.onClose();
            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }
}
