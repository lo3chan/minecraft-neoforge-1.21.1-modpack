package cc.cosmetica.include.twelvemonkeys.imageio.plugins.webp;

import cc.cosmetica.include.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;

final class WebPProviderInfo extends ReaderWriterProviderInfo {
   WebPProviderInfo() {
      super(
         WebPProviderInfo.class,
         new String[]{"webp", "WEBP", "wbp", "WBP"},
         new String[]{"wbp", "webp"},
         new String[]{"image/webp", "image/x-webp"},
         "cc.cosmetica.include.twelvemonkeys.imageio.plugins.webp.WebPImageReader",
         new String[]{"cc.cosmetica.include.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi"},
         null,
         null,
         false,
         null,
         null,
         null,
         null,
         true,
         null,
         null,
         null,
         null
      );
   }
}
