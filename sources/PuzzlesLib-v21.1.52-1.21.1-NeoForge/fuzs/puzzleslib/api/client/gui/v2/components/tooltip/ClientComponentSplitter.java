package fuzs.puzzleslib.api.client.gui.v2.components.tooltip;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

@Deprecated
public final class ClientComponentSplitter {
   private ClientComponentSplitter() {
   }

   public static Stream<FormattedCharSequence> splitTooltipLines(FormattedText... tooltipLines) {
      return splitTooltipLines(170, Arrays.asList(tooltipLines));
   }

   public static Stream<FormattedCharSequence> splitTooltipLines(int maxWidth, FormattedText... tooltipLines) {
      return splitTooltipLines(maxWidth, Arrays.asList(tooltipLines));
   }

   public static Stream<FormattedCharSequence> splitTooltipLines(List<? extends FormattedText> tooltipLines) {
      return splitTooltipLines(170, tooltipLines);
   }

   public static Stream<FormattedCharSequence> splitTooltipLines(int maxWidth, List<? extends FormattedText> tooltipLines) {
      return tooltipLines.stream().flatMap(formattedText -> {
         List<FormattedCharSequence> lines = Minecraft.getInstance().font.split(formattedText, maxWidth);
         return lines.isEmpty() ? Stream.of(FormattedCharSequence.EMPTY) : lines.stream();
      });
   }

   public static Stream<FormattedCharSequence> processTooltipLines(FormattedText... tooltipLines) {
      return processTooltipLines(Arrays.asList(tooltipLines));
   }

   public static Stream<FormattedCharSequence> processTooltipLines(List<? extends FormattedText> tooltipLines) {
      return tooltipLines.stream().map(formattedText -> ClientLanguage.getInstance().getVisualOrder(formattedText));
   }
}
