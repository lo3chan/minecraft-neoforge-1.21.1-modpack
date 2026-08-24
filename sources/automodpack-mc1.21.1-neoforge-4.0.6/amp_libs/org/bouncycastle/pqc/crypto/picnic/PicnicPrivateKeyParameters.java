package amp_libs.org.bouncycastle.pqc.crypto.picnic;

import amp_libs.org.bouncycastle.util.Arrays;

public class PicnicPrivateKeyParameters extends PicnicKeyParameters {
   private final byte[] privateKey;

   public PicnicPrivateKeyParameters(PicnicParameters var1, byte[] var2) {
      super(true, var1);
      this.privateKey = Arrays.clone(var2);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.privateKey);
   }
}
