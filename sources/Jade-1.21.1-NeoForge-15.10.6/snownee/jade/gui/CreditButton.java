package snownee.jade.gui;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.CreateNarration;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.StringUtils;
import snownee.jade.util.SmoothChasingValue;

public class CreditButton extends Button {
   private final Component hoveredTitle;
   private final OnPress onHover;
   private final SmoothChasingValue progress = new SmoothChasingValue();
   private boolean oldHovered;
   private boolean showTranslators;
   private List<String> translators = List.of();
   private int translatorIndex;
   private float translatorTime;

   protected CreditButton(
      int x, int y, int width, int height, Component title, Component hoveredTitle, OnPress onPress, OnPress onHover, CreateNarration createNarration
   ) {
      super(x, y, width, height, title, onPress, createNarration);
      this.hoveredTitle = hoveredTitle;
      this.onHover = onHover;
   }

   protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float partialTicks) {
      boolean hovered = this.isHoveredOrFocused();
      if (!this.oldHovered && hovered) {
         this.progress.target(1.0F);
      } else if (!hovered) {
         this.progress.target(0.0F);
      } else if (this.progress.value > 0.5F) {
         this.progress.target(0.0F);
         this.onHover.onPress(this);
      }

      this.progress.tick(partialTicks);
      this.progress.value = Math.min(0.6F, this.progress.value);
      float alpha = hovered ? 170.0F : 85.0F;
      if (this.showTranslators && !this.translators.isEmpty()) {
         int cycleTime = 60;
         this.translatorTime += partialTicks;
         if (this.translatorTime > cycleTime) {
            this.nextTranslator();
         }

         if (!hovered && this.translators.size() > 1) {
            if (this.translatorTime < 5.0F) {
               alpha *= this.translatorTime / 5.0F;
            } else if (cycleTime - this.translatorTime < 5.0F) {
               alpha *= (cycleTime - this.translatorTime) / 5.0F;
            }

            alpha = Math.max(alpha, 17.0F);
         }
      }

      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(this.getX() + this.width * 0.5F, this.getY(), 0.0F);
      float scale = 1.0F + this.progress.value * 0.2F;
      guiGraphics.pose().scale(scale, scale, scale);
      Component credit = hovered ? this.hoveredTitle : this.getMessage();
      Font font = Minecraft.getInstance().font;
      guiGraphics.pose().translate(font.width(credit) * -0.5F, 0.0F, 0.0F);
      guiGraphics.drawString(font, credit, 0, 0, 16777215 | (int)alpha << 24);
      guiGraphics.pose().popPose();
      this.oldHovered = hovered;
   }

   public void showTranslators() {
      if (!this.showTranslators) {
         this.showTranslators = true;
         if (I18n.exists("gui.jade.translators") && !"placeholder ".equals(I18n.get("gui.jade.translated_by", new Object[]{""}))) {
            String s = I18n.get("gui.jade.translators", new Object[0]);
            if (!"Bob, Alice, Charlie".equals(s)) {
               this.translators = Stream.of(StringUtils.split(s, ',')).map(String::trim).filter(StringUtils::isNotEmpty).toList();
               if (this.translators.size() > 1) {
                  this.translatorIndex = RandomSource.create().nextInt(this.translators.size());
               }

               this.nextTranslator();
            }
         }
      }
   }

   private void nextTranslator() {
      this.setMessage(Component.translatable("gui.jade.translated_by", new Object[]{this.translators.get(this.translatorIndex)}));
      if (this.translators.size() > 1) {
         this.translatorIndex++;
         if (this.translatorIndex >= this.translators.size()) {
            this.translatorIndex = 0;
         }

         this.translatorTime = 0.0F;
      }
   }
}
