package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.utils.KeyCodeHelper;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class KeybindOptionWidget extends BaseWidget {
   public static final int WIDTH = 80;
   public static final int HEIGHT = 16;
   private final Supplier<Integer> getter;
   private final Consumer<Integer> setter;
   private boolean isEditing = false;

   public KeybindOptionWidget(Supplier<Integer> getter, Consumer<Integer> setter) {
      super(80, 16);
      this.getter = getter;
      this.setter = setter;
   }

   private Component getDisplay() {
      int key = this.getter.get();
      MutableComponent display = key == 0 ? Component.literal("None") : KeyCodeHelper.getKeyName(key).copy();
      if (this.isEditing) {
         boolean strikethrough = System.currentTimeMillis() / 500L % 2L == 0L;
         return Component.literal("> ")
            .withColor(-9276296)
            .append(display.withStyle(style -> style.withUnderlined(strikethrough).withColor(-329226)))
            .append(Component.literal(" <"));
      } else {
         return display;
      }
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.BUTTON, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      renderScrollingString(
         graphics,
         this.font,
         this.getDisplay(),
         this.getX() + 4,
         this.getY() + 2,
         this.getX() + this.getWidth() - 4,
         this.getY() + this.getHeight() - 2,
         -9276296
      );
   }

   public void onClick(double mouseX, double mouseY) {
      this.isEditing = true;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.isEditing) {
         this.setter.accept(-100 - button);
         this.isEditing = false;
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean keyPressed(int i, int j, int k) {
      if (this.isEditing) {
         this.setter.accept(i == 256 ? 0 : i);
         this.isEditing = false;
         return true;
      } else {
         return super.keyPressed(i, j, k);
      }
   }
}
