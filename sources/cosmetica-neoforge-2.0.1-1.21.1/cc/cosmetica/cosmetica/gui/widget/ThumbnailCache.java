package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.CosmeticaModel;
import cc.cosmetica.core.api.texture.CosmeticaTexture.Builder;

public final class ThumbnailCache {
   private static CachedImage[] cache = new CachedImage[16];
   private static CachedImage[] browseCache = new CachedImage[64];
   private static int next = 0;
   private static int nextBrowse = 0;

   private ThumbnailCache() {
   }

   public static CachedImage getOrCreateImage(Builder texture, boolean useBrowseCache) {
      CachedImage image = CosmeticaModel.getOrCreateCosmeticaImage(texture);
      if (useBrowseCache) {
         for (CachedImage i : browseCache) {
            if (i == image) {
               return image;
            }
         }

         browseCache[nextBrowse] = image;
         nextBrowse = nextBrowse + 1 & 63;
         return image;
      } else {
         for (CachedImage ix : cache) {
            if (ix == image) {
               return image;
            }
         }

         cache[next] = image;
         next = next + 1 & 15;
         return image;
      }
   }
}
