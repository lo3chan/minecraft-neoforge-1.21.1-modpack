package cc.cosmetica.include.twelvemonkeys.imageio.plugins.webp;

import cc.cosmetica.include.twelvemonkeys.imageio.StandardImageMetadataSupport;
import cc.cosmetica.include.twelvemonkeys.lang.Validate;
import javax.imageio.ImageTypeSpecifier;

final class WebPImageMetadata extends StandardImageMetadataSupport {
   WebPImageMetadata(ImageTypeSpecifier var1, VP8xChunk var2) {
      super(
         builder(var1)
            .withCompressionTypeName(Validate.notNull(var2, "header").isLossless ? "VP8L" : "VP8")
            .withCompressionLossless(var2.isLossless)
            .withPixelAspectRatio(1.0)
            .withFormatVersion("1.0")
      );
   }
}
