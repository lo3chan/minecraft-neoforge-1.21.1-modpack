package net.mehvahdjukaar.moonlight.api.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TextUtil {
   private static final FormattedCharSequence CURSOR_MARKER = FormattedCharSequence.forward("_", Style.EMPTY);

   public static Pair<List<FormattedCharSequence>, Float> fitLinesToBox(Font font, FormattedText text, float width, float height) {
      int fontWidth = font.width(text);

      int scalingFactor;
      List<FormattedCharSequence> splitLines;
      float maxLines;
      do {
         scalingFactor = Mth.floor(Mth.sqrt(fontWidth * 8.0F / (width * height)));
         splitLines = font.split(text, Mth.floor(width * scalingFactor));
         maxLines = height * scalingFactor / 8.0F;
         fontWidth++;
      } while (maxLines < splitLines.size());

      return Pair.of(splitLines, 1.0F / scalingFactor);
   }

   public static FormattedText parseText(String s, @Nullable Provider provider) {
      try {
         if (provider != null) {
            FormattedText mutableComponent = Serializer.fromJson(s, provider);
            if (mutableComponent != null) {
               return mutableComponent;
            }
         }
      } catch (Exception var3) {
      }

      return FormattedText.of(s);
   }

   @Deprecated(
      forRemoval = true
   )
   public static FormattedText parseText(String s) {
      return parseText(s, Utils.hackyGetRegistryAccess());
   }

   @Deprecated(
      forRemoval = true
   )
   public static void renderGuiLine(
      TextUtil.RenderProperties properties,
      String string,
      Font font,
      GuiGraphics graphics,
      BufferSource buffer,
      int cursorPos,
      int selectionPos,
      boolean isSelected,
      boolean blink,
      int yOffset
   ) {
      renderGuiLine(properties, string, font, graphics, cursorPos, selectionPos, isSelected, blink, yOffset, 9);
   }

   public static void renderGuiLine(
      TextUtil.RenderProperties properties,
      String string,
      Font font,
      GuiGraphics graphics,
      int cursorPos,
      int selectionPos,
      boolean isSelected,
      boolean blink,
      int yOffset,
      int textLineHeight
   ) {
      PoseStack poseStack = graphics.pose();
      poseStack.pushPose();
      int textColor = properties.textColor;
      int twoTextLineHeight = 2 * textLineHeight;
      if (string != null) {
         int strWidth = font.width(string);
         int centerStr = strWidth - strWidth / 2;
         if (font.isBidirectional()) {
            string = font.bidirectionalShaping(string);
         }

         float centerX = -font.width(string) / 2.0F;
         graphics.drawString(font, string, (int)centerX, yOffset, textColor, false);
         if (isSelected) {
            if (blink && cursorPos >= string.length()) {
               graphics.drawString(font, "_", centerStr, yOffset, textColor, false);
            }

            if (blink && cursorPos < string.length()) {
               graphics.fill(centerStr, yOffset - 1, centerStr + 1, yOffset + textLineHeight, 0xFF000000 | textColor);
            }

            if (selectionPos != cursorPos) {
               int minC = Math.min(cursorPos, selectionPos);
               int maxC = Math.max(cursorPos, selectionPos);
               int s = font.width(string.substring(0, minC)) - strWidth / 2;
               int t = font.width(string.substring(0, maxC)) - strWidth / 2;
               int startX = Math.min(s, t);
               int v = Math.max(s, t);
               graphics.fill(RenderType.guiTextHighlight(), startX, yOffset, v, yOffset + textLineHeight, -16776961);
            }
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static void renderGuiText(
      TextUtil.RenderProperties properties,
      String[] guiLines,
      Font font,
      GuiGraphics graphics,
      BufferSource buffer,
      int cursorPos,
      int selectionPos,
      int currentLine,
      boolean blink,
      int lineSpacing
   ) {
      renderGuiText(properties, guiLines, font, graphics, cursorPos, selectionPos, currentLine, blink, lineSpacing);
   }

   public static void renderGuiText(
      TextUtil.RenderProperties properties,
      String[] guiLines,
      Font font,
      GuiGraphics graphics,
      int cursorPos,
      int selectionPos,
      int currentLine,
      boolean blink,
      int lineSpacing
   ) {
      int nOfLines = guiLines.length;

      for (int line = 0; line < nOfLines; line++) {
         int yOffset = line * lineSpacing - nOfLines * 5;
         renderGuiLine(properties, guiLines[line], font, graphics, cursorPos, selectionPos, line == currentLine, blink, yOffset, lineSpacing);
      }
   }

   public static void renderLine(
      FormattedCharSequence formattedCharSequences,
      Font font,
      float yOffset,
      PoseStack poseStack,
      MultiBufferSource buffer,
      TextUtil.RenderProperties properties
   ) {
      if (formattedCharSequences != null) {
         float x = -font.width(formattedCharSequences) / 2.0F;
         renderLineInternal(formattedCharSequences, font, x, yOffset, poseStack.last().pose(), buffer, properties);
      }
   }

   public static void renderAllLines(
      FormattedCharSequence[] charSequences, int ySeparation, Font font, PoseStack poseStack, MultiBufferSource buffer, TextUtil.RenderProperties properties
   ) {
      for (int i = 0; i < charSequences.length; i++) {
         renderLine(charSequences[i], font, ySeparation * i, poseStack, buffer, properties);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   private static void renderLineInternal(
      FormattedCharSequence formattedCharSequences,
      Font font,
      float xOffset,
      float yOffset,
      Matrix4f matrix4f,
      MultiBufferSource buffer,
      TextUtil.RenderProperties properties
   ) {
      if (properties.outline) {
         font.drawInBatch8xOutline(formattedCharSequences, xOffset, yOffset, properties.textColor, properties.darkenedColor, matrix4f, buffer, properties.light);
      } else {
         font.drawInBatch(formattedCharSequences, xOffset, yOffset, properties.darkenedColor, false, matrix4f, buffer, DisplayMode.NORMAL, 0, properties.light);
      }
   }

   private static int getDarkenedColor(int color, boolean glowing, float mult) {
      return color == DyeColor.BLACK.getTextColor() && glowing ? -988212 : ColorUtils.multiply(color, 0.4F * (glowing ? 1.0F : mult));
   }

   private static int getDarkenedColor(int color, boolean glowing) {
      return getDarkenedColor(color, glowing, 1.0F);
   }

   public static TextUtil.RenderProperties renderProperties(
      DyeColor dyeColor, boolean glowing, int combinedLight, Style style, Vector3f normal, BooleanSupplier isVeryNear
   ) {
      return renderProperties(dyeColor, glowing, 1.0F, combinedLight, style, normal, isVeryNear);
   }

   public static TextUtil.RenderProperties renderProperties(
      DyeColor dyeColor, boolean glowing, float darkColorMult, int combinedLight, Style style, Vector3f normal, BooleanSupplier isVeryNear
   ) {
      boolean outline = glowing && (dyeColor == DyeColor.BLACK || isVeryNear.getAsBoolean());
      int textColor = dyeColor.getTextColor();
      float shading = ColorUtils.getShading(normal);
      int color = glowing ? textColor : ColorUtils.multiply(textColor, shading);
      int dark;
      if (glowing && !outline) {
         dark = color;
      } else {
         dark = getDarkenedColor(textColor, glowing, darkColorMult * shading);
      }

      return new TextUtil.RenderProperties(color, dark, outline, glowing ? 15728880 : combinedLight, style);
   }

   public record RenderProperties(int textColor, int darkenedColor, boolean outline, int light, Style style) {
   }
}
