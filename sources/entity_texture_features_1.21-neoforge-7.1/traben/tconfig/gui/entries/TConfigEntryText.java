package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import traben.entity_texture_features.ETF;

public class TConfigEntryText extends TConfigEntry {
   protected final StringWidget widget = new StringWidget(this.getText(), Minecraft.getInstance().font);

   public TConfigEntryText(@Translatable String text, TConfigEntryText.TextAlignment alignment) {
      super(text, null);
      alignment.align(this.widget);
   }

   public TConfigEntryText(@Translatable String text) {
      this(text, TConfigEntryText.TextAlignment.CENTER);
   }

   public static Collection<TConfigEntry> fromLongOrMultilineTranslation(@Translatable String translationKey, int width) {
      return fromLongOrMultilineTranslation(translationKey, width, TConfigEntryText.TextAlignment.CENTER);
   }

   public static List<TConfigEntry> fromLongOrMultilineTranslation(@Translatable String translationKey, int width, TConfigEntryText.TextAlignment alignment) {
      Component translated = ETF.getTextFromTranslation(translationKey);
      List<FormattedText> lines = Minecraft.getInstance().font.getSplitter().splitLines(translated, width, Style.EMPTY);
      List<TConfigEntry> list = new ArrayList<>();
      String lastLine = null;

      for (FormattedText line : lines) {
         if (lastLine != null) {
            list.add(new TConfigEntryText.TwoLines(lastLine, line.getString(), alignment));
            lastLine = null;
         } else {
            lastLine = line.getString();
         }
      }

      if (lastLine != null) {
         list.add(new TConfigEntryText.TwoLines(lastLine, "", alignment));
      }

      return list;
   }

   @Override
   public AbstractWidget getWidget(int x, int y, int width, int height) {
      this.widget.setRectangle(width, height, x, y);
      return this.widget;
   }

   @Override
   boolean hasChangedFromInitial() {
      return false;
   }

   @Override
   boolean saveValuesToConfig() {
      return false;
   }

   @Override
   void setValuesToDefault() {
   }

   @Override
   void resetValuesToInitial() {
   }

   @Deprecated
   public static enum TextAlignment {
      LEFT,
      CENTER,
      RIGHT;

      private void align(StringWidget widget) {
         switch (this) {
            case LEFT:
               widget.alignLeft();
               break;
            case CENTER:
               widget.alignCenter();
               break;
            case RIGHT:
               widget.alignRight();
         }
      }
   }

   public static class TwoLines extends TConfigEntryText {
      protected final StringWidget widget2;

      public TwoLines(@Translatable String text1, @Translatable String text2) {
         this(text1, text2, TConfigEntryText.TextAlignment.CENTER);
      }

      public TwoLines(@Translatable String text1, @Translatable String text2, TConfigEntryText.TextAlignment alignment) {
         super(text1, alignment);
         this.widget2 = new StringWidget(ETF.getTextFromTranslation(text2), Minecraft.getInstance().font);
         alignment.align(this.widget2);
         if (!this.widget2.getMessage().getString().contains("§")) {
            this.widget2.setColor(13421772);
         }
      }

      @Override
      public AbstractWidget getWidget(int x, int y, int width, int height) {
         this.widget.setRectangle(width, height / 2, x, y);
         this.widget2.setRectangle(width, height / 2, x, y + height / 2 + 2);
         return this.widget;
      }

      @Override
      public void render(
         GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
      ) {
         this.lastWidgetRendered = this.getWidget(x, y, entryWidth, entryHeight);
         this.widget.render(context, mouseX, mouseY, tickDelta);
         this.widget2.render(context, mouseX, mouseY, tickDelta);
      }
   }
}
