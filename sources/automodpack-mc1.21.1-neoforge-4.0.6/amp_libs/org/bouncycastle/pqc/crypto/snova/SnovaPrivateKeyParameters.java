package amp_libs.org.bouncycastle.pqc.crypto.snova;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.util.Arrays;

public class SnovaPrivateKeyParameters extends AsymmetricKeyParameter {
   private final byte[] privateKey;
   private final SnovaParameters parameters;

   public SnovaPrivateKeyParameters(SnovaParameters var1, byte[] var2) {
      super(true);
      this.privateKey = Arrays.clone(var2);
      this.parameters = var1;
   }

   public byte[] getPrivateKey() {
      return Arrays.clone(this.privateKey);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.privateKey);
   }

   public SnovaParameters getParameters() {
      return this.parameters;
   }
}
