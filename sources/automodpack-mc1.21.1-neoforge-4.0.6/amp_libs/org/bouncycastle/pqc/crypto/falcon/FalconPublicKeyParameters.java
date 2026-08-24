package amp_libs.org.bouncycastle.pqc.crypto.falcon;

import amp_libs.org.bouncycastle.util.Arrays;

public class FalconPublicKeyParameters extends FalconKeyParameters {
   private final byte[] H;

   public FalconPublicKeyParameters(FalconParameters var1, byte[] var2) {
      super(false, var1);
      this.H = Arrays.clone(var2);
   }

   public byte[] getH() {
      return Arrays.clone(this.H);
   }
}
