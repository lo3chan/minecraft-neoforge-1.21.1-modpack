package fuzs.puzzleslib.api.client.gui.v2.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.puzzleslib.api.util.v1.ComponentHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public final class ClientComponentSplitter {
   private ClientComponentSplitter() {
   }

   public static List<Component> splitTooltipComponents(FormattedText... tooltipLines) {
      return splitTooltipLines(tooltipLines).map(ComponentHelper::getAsComponent).toList();
   }

   public static List<Component> splitTooltipComponents(List<? extends FormattedText> tooltipLines) {
      return splitTooltipLines(tooltipLines).map(ComponentHelper::getAsComponent).toList();
   }

   public static Stream<FormattedCharSequence> splitTooltipLines(FormattedText... tooltipLines) {
      return splitTooltipLines(Arrays.asList(tooltipLines));
   }

   public static Stream<FormattedCharSequence> splitTooltipLines(List<? extends FormattedText> tooltipLines) {
      return splitTooltipLines(170, tooltipLines);
   }

   public static Stream<FormattedCharSequence> splitTooltipLines(int maxWidth, FormattedText... tooltipLines) {
      return splitTooltipLines(maxWidth, Arrays.asList(tooltipLines));
   }

   public static Stream<FormattedCharSequence> splitTooltipLines(int maxWidth, List<? extends FormattedText> tooltipLines) {
      return tooltipLines.stream().flatMap(formattedText -> {
         List<FormattedCharSequence> lines;
         if (RenderSystem.isOnRenderThread()) {
            lines = Minecraft.getInstance().font.split(formattedText, maxWidth);
         } else {
            lines = Language.getInstance().getVisualOrder(Collections.singletonList(formattedText));
         }

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
