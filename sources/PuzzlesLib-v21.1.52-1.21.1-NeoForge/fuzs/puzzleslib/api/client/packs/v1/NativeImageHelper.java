package fuzs.puzzleslib.api.client.packs.v1;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;

public final class NativeImageHelper {
   private NativeImageHelper() {
   }

   public static byte[] asByteArray(NativeImage nativeImage) throws IOException {
      return nativeImage.asByteArray();
   }
}
