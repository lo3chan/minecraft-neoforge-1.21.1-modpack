package amp_libs.org.bouncycastle.pqc.crypto.falcon;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class FalconKeyParameters extends AsymmetricKeyParameter {
   private final FalconParameters params;

   public FalconKeyParameters(boolean var1, FalconParameters var2) {
      super(var1);
      this.params = var2;
   }

   public FalconParameters getParameters() {
      return this.params;
   }
}
