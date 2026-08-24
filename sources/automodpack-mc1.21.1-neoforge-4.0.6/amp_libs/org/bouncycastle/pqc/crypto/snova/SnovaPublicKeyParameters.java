package amp_libs.org.bouncycastle.pqc.crypto.snova;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.util.Arrays;

public class SnovaPublicKeyParameters extends AsymmetricKeyParameter {
   private final byte[] publicKey;
   private final SnovaParameters parameters;

   public SnovaPublicKeyParameters(SnovaParameters var1, byte[] var2) {
      super(false);
      this.publicKey = Arrays.clone(var2);
      this.parameters = var1;
   }

   public byte[] getPublicKey() {
      return Arrays.clone(this.publicKey);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.publicKey);
   }

   public SnovaParameters getParameters() {
      return this.parameters;
   }
}
