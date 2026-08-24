package amp_libs.org.bouncycastle.pqc.crypto.frodo;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class FrodoKeyParameters extends AsymmetricKeyParameter {
   private FrodoParameters params;

   public FrodoKeyParameters(boolean var1, FrodoParameters var2) {
      super(var1);
      this.params = var2;
   }

   public FrodoParameters getParameters() {
      return this.params;
   }
}
