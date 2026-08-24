package amp_libs.org.bouncycastle.pqc.crypto.hqc;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class HQCKeyParameters extends AsymmetricKeyParameter {
   private final HQCParameters params;

   public HQCKeyParameters(boolean var1, HQCParameters var2) {
      super(var1);
      this.params = var2;
   }

   public HQCParameters getParameters() {
      return this.params;
   }
}
