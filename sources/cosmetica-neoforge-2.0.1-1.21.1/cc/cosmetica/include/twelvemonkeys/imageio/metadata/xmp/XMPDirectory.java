package cc.cosmetica.include.twelvemonkeys.imageio.metadata.xmp;

import cc.cosmetica.include.twelvemonkeys.imageio.metadata.AbstractCompoundDirectory;
import cc.cosmetica.include.twelvemonkeys.imageio.metadata.Directory;
import java.util.Collection;

final class XMPDirectory extends AbstractCompoundDirectory {
   private final String toolkit;

   public XMPDirectory(Collection<? extends Directory> var1, String var2) {
      super(var1);
      this.toolkit = var2;
   }

   String getWriterToolkit() {
      return this.toolkit;
   }

   @Override
   public boolean isReadOnly() {
      return super.isReadOnly();
   }
}
