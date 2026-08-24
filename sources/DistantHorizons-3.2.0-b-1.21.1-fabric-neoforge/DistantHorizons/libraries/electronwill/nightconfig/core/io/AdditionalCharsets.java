package DistantHorizons.libraries.electronwill.nightconfig.core.io;

import java.nio.charset.Charset;

public final class AdditionalCharsets {
   public static final Charset UTF_8_BOM = new CharsetUnicodeBom(true);
   public static final Charset UTF_8_OR_16 = new CharsetUnicodeBom(false);

   private AdditionalCharsets() {
      assert false;
   }
}
