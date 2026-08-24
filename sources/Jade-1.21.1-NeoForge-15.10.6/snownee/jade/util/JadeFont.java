package snownee.jade.util;

import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.providers.BitmapProvider.Glyph;

public class JadeFont extends Font {
   public JadeFont(Font font) {
      super(font.fonts, font.filterFishyGlyphs);
      this.splitter = new StringSplitter((i, style) -> {
         GlyphInfo glyphInfo = this.getFontSet(style.getFont()).getGlyphInfo(i, this.filterFishyGlyphs);
         return isTooLarge(glyphInfo, 9) ? 0.0F : glyphInfo.getAdvance(style.isBold());
      });
   }

   public static boolean isTooLarge(GlyphInfo glyphInfo, int lineHeight) {
      return glyphInfo instanceof Glyph glyph && glyph.height() * glyph.scale() > lineHeight + 4;
   }
}
