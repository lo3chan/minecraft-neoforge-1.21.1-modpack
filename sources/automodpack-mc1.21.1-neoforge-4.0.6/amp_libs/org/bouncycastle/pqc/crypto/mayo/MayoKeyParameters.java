package amp_libs.org.bouncycastle.pqc.crypto.mayo;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class MayoKeyParameters extends AsymmetricKeyParameter {
   private final MayoParameters params;

   public MayoKeyParameters(boolean var1, MayoParameters var2) {
      super(var1);
      this.params = var2;
   }

   public MayoParameters getParameters() {
      return this.params;
   }
}
