package amp_libs.org.bouncycastle.pqc.crypto.newhope;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.util.Arrays;

public class NHPublicKeyParameters extends AsymmetricKeyParameter {
   final byte[] pubData;

   public NHPublicKeyParameters(byte[] var1) {
      super(false);
      this.pubData = Arrays.clone(var1);
   }

   public byte[] getPubData() {
      return Arrays.clone(this.pubData);
   }
}
