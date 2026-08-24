package com.seibel.distanthorizons.common.wrappers.gui.classicConfig;

import com.seibel.distanthorizons.common.wrappers.gui.GuiHelper_neoforge;
import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import com.seibel.distanthorizons.core.config.types.ConfigUIComment;
import com.seibel.distanthorizons.core.config.types.enums.EConfigCommentTextPosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ClassicConfigGUI$DhButtonEntry_neoforge extends Entry<ClassicConfigGUI$DhButtonEntry_neoforge> {
   private static final Font textRenderer = Minecraft.getInstance().font;
   private final DhConfigScreen_neoforge gui;
   private final AbstractWidget indexButton;
   private final AbstractWidget resetButton;
   private final AbstractWidget button;
   private final Component text;
   private final List<AbstractWidget> children = new ArrayList<>();
   @NotNull
   private final EConfigCommentTextPosition textPosition;
   public final AbstractConfigBase dhConfigType;
   public static final Map<AbstractWidget, Component> TEXT_BY_WIDGET = new HashMap<>();
   public static final Map<AbstractWidget, ClassicConfigGUI$DhButtonEntry_neoforge> BUTTON_BY_WIDGET = new HashMap<>();

   public ClassicConfigGUI$DhButtonEntry_neoforge(
      DhConfigScreen_neoforge gui,
      AbstractConfigBase dhConfigType,
      AbstractWidget button,
      Component text,
      AbstractWidget resetButton,
      AbstractWidget indexButton
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

   public void render(GuiGraphics matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
      try {
         if (this.button != null) {
            GuiHelper_neoforge.SetY(this.button, y);
            this.button.render(matrices, mouseX, mouseY, tickDelta);
         }

         if (this.resetButton != null) {
            GuiHelper_neoforge.SetY(this.resetButton, y);
            this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
         }

         if (this.indexButton != null) {
            GuiHelper_neoforge.SetY(this.indexButton, y);
            this.indexButton.render(matrices, mouseX, mouseY, tickDelta);
         }

         if (this.text != null) {
            int translatedLength = textRenderer.width(this.text);
            int textXPos;
            if (this.textPosition == EConfigCommentTextPosition.RIGHT_JUSTIFIED) {
               textXPos = this.gui.width - translatedLength - 8 - 10 - 150 - 5 - 60;
            } else if (this.textPosition == EConfigCommentTextPosition.CENTERED_OVER_BUTTONS) {
               textXPos = this.gui.width - translatedLength / 2 - 100 - 10;
            } else {
               if (this.textPosition != EConfigCommentTextPosition.CENTER_OF_SCREEN) {
                  throw new UnsupportedOperationException("No text position render defined for [" + this.textPosition + "]");
               }

               textXPos = this.gui.width / 2 - translatedLength / 2;
            }

            matrices.drawString(textRenderer, this.text, textXPos, y + 5, 16777215);
         }
      } catch (Exception var13) {
         ClassicConfigGUI_neoforge.RATE_LIMITED_LOGGER.error("Unexpected gui rendering issue: [" + var13.getMessage() + "]", var13);
      }
   }

   @NotNull
   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   @NotNull
   public List<? extends NarratableEntry> narratables() {
      return this.children;
   }
}
