package com.seibel.distanthorizons.common.wrappers.gui.classicConfig;

import com.seibel.distanthorizons.common.wrappers.gui.GuiHelper_fabric;
import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import com.seibel.distanthorizons.core.config.types.ConfigUIComment;
import com.seibel.distanthorizons.core.config.types.enums.EConfigCommentTextPosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class ClassicConfigGUI$DhButtonEntry_fabric extends class_4266<ClassicConfigGUI$DhButtonEntry_fabric> {
   private static final class_327 textRenderer = class_310.method_1551().field_1772;
   private final DhConfigScreen_fabric gui;
   private final class_339 indexButton;
   private final class_339 resetButton;
   private final class_339 button;
   private final class_2561 text;
   private final List<class_339> children = new ArrayList<>();
   @NotNull
   private final EConfigCommentTextPosition textPosition;
   public final AbstractConfigBase dhConfigType;
   public static final Map<class_339, class_2561> TEXT_BY_WIDGET = new HashMap<>();
   public static final Map<class_339, ClassicConfigGUI$DhButtonEntry_fabric> BUTTON_BY_WIDGET = new HashMap<>();

   public ClassicConfigGUI$DhButtonEntry_fabric(
      DhConfigScreen_fabric gui, AbstractConfigBase dhConfigType, class_339 button, class_2561 text, class_339 resetButton, class_339 indexButton
   ) {
      TEXT_BY_WIDGET.put(button, text);
      BUTTON_BY_WIDGET.put(button, this);
      this.gui = gui;
      this.dhConfigType = dhConfigType;
      this.button = button;
      this.resetButton = resetButton;
      this.text = text;
      this.indexButton = indexButton;
      if (button != null) {
         this.children.add(button);
      }

      if (resetButton != null) {
         this.children.add(resetButton);
      }

      if (indexButton != null) {
         this.children.add(indexButton);
      }

      EConfigCommentTextPosition textPosition = null;
      if (this.dhConfigType instanceof ConfigUIComment) {
         textPosition = ((ConfigUIComment)this.dhConfigType).textPosition;
      }

      if (textPosition == null) {
         if (this.button != null) {
            textPosition = EConfigCommentTextPosition.RIGHT_JUSTIFIED;
         } else {
            textPosition = EConfigCommentTextPosition.CENTERED_OVER_BUTTONS;
         }
      }

      this.textPosition = textPosition;
   }

   public void method_25343(
      class_332 matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
   ) {
      try {
         if (this.button != null) {
            GuiHelper_fabric.SetY(this.button, y);
            this.button.method_25394(matrices, mouseX, mouseY, tickDelta);
         }

         if (this.resetButton != null) {
            GuiHelper_fabric.SetY(this.resetButton, y);
            this.resetButton.method_25394(matrices, mouseX, mouseY, tickDelta);
         }

         if (this.indexButton != null) {
            GuiHelper_fabric.SetY(this.indexButton, y);
            this.indexButton.method_25394(matrices, mouseX, mouseY, tickDelta);
         }

         if (this.text != null) {
            int translatedLength = textRenderer.method_27525(this.text);
            int textXPos;
            if (this.textPosition == EConfigCommentTextPosition.RIGHT_JUSTIFIED) {
               textXPos = this.gui.field_22789 - translatedLength - 8 - 10 - 150 - 5 - 60;
            } else if (this.textPosition == EConfigCommentTextPosition.CENTERED_OVER_BUTTONS) {
               textXPos = this.gui.field_22789 - translatedLength / 2 - 100 - 10;
            } else {
               if (this.textPosition != EConfigCommentTextPosition.CENTER_OF_SCREEN) {
                  throw new UnsupportedOperationException("No text position render defined for [" + this.textPosition + "]");
               }

               textXPos = this.gui.field_22789 / 2 - translatedLength / 2;
            }

            matrices.method_27535(textRenderer, this.text, textXPos, y + 5, 16777215);
         }
      } catch (Exception var13) {
         ClassicConfigGUI_fabric.RATE_LIMITED_LOGGER.error("Unexpected gui rendering issue: [" + var13.getMessage() + "]", var13);
      }
   }

   @NotNull
   public List<? extends class_364> method_25396() {
      return this.children;
   }

   @NotNull
   public List<? extends class_6379> method_37025() {
      return this.children;
   }
}
