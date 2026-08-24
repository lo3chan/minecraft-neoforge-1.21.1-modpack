package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import java.text.DecimalFormat;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;

public class NumberOptionWidget<T extends Number> extends EditBox implements ResetableWidget {
   public static final Pattern INTEGER_FILTER = Pattern.compile("-?[0-9]*");
   public static final Pattern DECIMAL_FILTER = Pattern.compile("-?[0-9]*(\\.[0-9]*)?");
   private static final int FOCUSED_EXTRA_WIDTH = 40;
   private static final int WIDTH = 80;
   private static final int FOCUSED_WIDTH = 120;
   private final Supplier<T> getter;
   private final Function<T, Boolean> setter;
   private final Function<String, T> parser;

   public NumberOptionWidget(Supplier<T> getter, Function<T, Boolean> setter, Function<String, T> parser, Pattern filter) {
      super(Minecraft.getInstance().font, 80, 16, CommonComponents.EMPTY);
      this.setMaxLength(32767);
      this.setBordered(false);
      this.setCanLoseFocus(true);
      this.getter = getter;
      this.setter = setter;
      this.parser = parser;
      DecimalFormat format = new DecimalFormat();
      format.setGroupingUsed(false);
      format.setMaximumFractionDigits(340);
      this.setValue(format.format(getter.get()));
      this.setFilter(s -> s.isEmpty() || filter.matcher(s).matches());
      this.setResponder();
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      this.updateIfFocused();
      graphics.blitSprite(ModSprites.BUTTON, this.getX(), this.getY(), this.width, this.height);
      graphics.enableScissor(this.getX() + 4, this.getY() + 4, this.getX() + this.width - 4, this.getY() + this.height - 4);
      PoseStack pose = graphics.pose();
      pose.pushPose();
      pose.translate(4.0F, 4.0F, 0.0F);
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);
      pose.popPose();
      graphics.disableScissor();
   }

   public void updateIfFocused() {
      if (this.width != 120 && this.isFocused()) {
         this.setWidth(120);
         this.setX(this.getX() - 40);
      } else if (this.width != 80 && !this.isFocused()) {
         this.setWidth(80);
         this.setX(this.getX() + 40);
      }
   }

   @Override
   public void reset() {
      this.setResponder(s -> {});
      this.setValue(this.getter.get().toString());
      this.setResponder();
   }

   public void setResponder() {
      this.setResponder(s -> {
         try {
            T value = this.parser.apply(s);
            if (this.setter.apply(value)) {
               this.setTextColor(14737632);
            } else {
               this.setTextColor(16711680);
            }
         } catch (NumberFormatException var3) {
            this.setTextColor(16711680);
         }
      });
   }
}
