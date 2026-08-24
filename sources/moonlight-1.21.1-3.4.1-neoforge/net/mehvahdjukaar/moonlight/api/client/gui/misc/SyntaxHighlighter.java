package net.mehvahdjukaar.moonlight.api.client.gui.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

@FunctionalInterface
public interface SyntaxHighlighter {
   int FALLBACK_COLOR = ConfigGuiColors.SYNTAX_DEFAULT;

   int[] colors(String var1);

   default FormattedCharSequence highlightLine(String line) {
      if (line.isEmpty()) {
         return FormattedCharSequence.EMPTY;
      } else {
         int[] colors = this.colors(line);
         List<FormattedCharSequence> parts = new ArrayList<>();
         int runStart = 0;

         for (int i = 1; i <= line.length(); i++) {
            int prev = colorAt(colors, i - 1);
            if (i == line.length() || colorAt(colors, i) != prev) {
               parts.add(FormattedCharSequence.forward(line.substring(runStart, i), Style.EMPTY.withColor(TextColor.fromRgb(prev))));
               runStart = i;
            }
         }

         return FormattedCharSequence.fromList(parts);
      }
   }

   default BiFunction<String, Integer, FormattedCharSequence> formatter(final EditBox box) {
      return new BiFunction<String, Integer, FormattedCharSequence>() {
         private String cachedSource;
         private int[] cachedColors;

         public FormattedCharSequence apply(String chunk, Integer displayPos) {
            String source = box.getValue();
            if (!source.equals(this.cachedSource)) {
               this.cachedSource = source;
               this.cachedColors = SyntaxHighlighter.this.colors(source);
            }

            List<FormattedCharSequence> parts = new ArrayList<>(chunk.length());

            for (int i = 0; i < chunk.length(); i++) {
               int color = SyntaxHighlighter.colorAt(this.cachedColors, displayPos + i);
               parts.add(FormattedCharSequence.forward(String.valueOf(chunk.charAt(i)), Style.EMPTY.withColor(TextColor.fromRgb(color))));
            }

            return FormattedCharSequence.fromList(parts);
         }
      };
   }

   private static int colorAt(int[] colors, int index) {
      return index >= 0 && index < colors.length ? colors[index] : FALLBACK_COLOR;
   }
}
