package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.components.base.ListWidget;
import com.teamresourceful.resourcefulconfig.client.screens.base.OverlayScreen;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class DropdownWidget extends BaseWidget {
   private static final int WIDTH = 80;
   private final Enum<?>[] options;
   private final Supplier<Enum<?>> getter;
   private final Consumer<Enum<?>> setter;
   private Component title = Component.empty();

   public DropdownWidget(int width, Enum<?>[] options, Supplier<Enum<?>> getter, Consumer<Enum<?>> setter) {
      super(width, 16);
      this.options = options;
      this.getter = getter;
      this.setter = setter;
   }

   public DropdownWidget(Enum<?>[] options, Supplier<Enum<?>> getter, Consumer<Enum<?>> setter) {
      this(80, options, getter, setter);
   }

   public static <T extends Enum<?>> DropdownWidget of(T[] options, Supplier<T> getter, Consumer<T> setter) {
      return new DropdownWidget(options, getter::get, t -> setter.accept((T)t));
   }

   public DropdownWidget setTitle(Component title) {
      this.title = title;
      return this;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.ofButton(this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
      renderScrollingString(
         graphics,
         this.font,
         Translatable.toComponent(this.getter.get(), this.title),
         this.getX() + 4,
         this.getY() + 4,
         this.getX() + this.getWidth() - 16,
         this.getY() + this.getHeight() - 4,
         -9276296
      );
      graphics.blitSprite(ModSprites.CHEVRON_DOWN, this.getX() + this.getWidth() - 12, this.getY() + 4, 8, 8);
   }

   public void onClick(double d, double e) {
      Minecraft.getInstance().setScreen(new DropdownWidget.DropdownOverlay(this));
   }

   private static class DropdownItem extends BaseWidget implements ListWidget.Item {
      private final Enum<?> option;
      private final Consumer<Enum<?>> setter;

      public DropdownItem(Enum<?> option, Consumer<Enum<?>> setter) {
         super(80, 12);
         this.option = option;
         this.setter = setter;
      }

      @Override
      protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         graphics.blitSprite(ModSprites.ofButton(this.isHovered()), this.getX() + 1, this.getY(), this.getWidth() - 1, this.getHeight());
         int color = this.isHovered() ? -329226 : -9276296;
         renderScrollingString(
            graphics,
            this.font,
            Translatable.toComponent(this.option),
            this.getX() + 4,
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

   private static class DropdownList extends ListWidget {
      public DropdownList(int x, int y, int height) {
         super(x + 1, y, 78, height);
      }

      public static DropdownWidget.DropdownList of(DropdownWidget widget) {
         int windowHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
         int widgetY = widget.getY() + widget.getHeight();
         int listHeight = Math.min(widget.options.length * 12, 96) + 1;
         if (widgetY + listHeight > windowHeight) {
            widgetY = widget.getY() - listHeight - 1;
         }

         return new DropdownWidget.DropdownList(widget.getX(), widgetY, listHeight);
      }

      @Override
      public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         graphics.blitSprite(ModSprites.ACCENT, this.getX() - 1, this.getY() - 1, this.getWidth() + 2, this.getHeight() + 2);
         graphics.blitSprite(ModSprites.BUTTON, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         super.renderWidget(graphics, mouseX, mouseY, partialTicks);
      }
   }

   private static class DropdownOverlay extends OverlayScreen {
      private final DropdownWidget widget;

      protected DropdownOverlay(DropdownWidget widget) {
         super(Minecraft.getInstance().screen);
         this.widget = widget;
      }

      @Override
      protected void init() {
         DropdownWidget.DropdownList list = (DropdownWidget.DropdownList)this.addRenderableWidget(DropdownWidget.DropdownList.of(this.widget));

         for (Enum<?> option : this.widget.options) {
            list.add(new DropdownWidget.DropdownItem(option, value -> {
               this.widget.setter.accept(value);
               this.onClose();
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
