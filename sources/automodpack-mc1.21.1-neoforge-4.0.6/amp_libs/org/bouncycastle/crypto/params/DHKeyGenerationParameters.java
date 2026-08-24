package amp_libs.org.bouncycastle.crypto.params;

import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class DHKeyGenerationParameters extends KeyGenerationParameters {
   private DHParameters params;

   public DHKeyGenerationParameters(SecureRandom var1, DHParameters var2) {
      super(var1, getStrength(var2));
      this.params = var2;
   }

   public DHParameters getParameters() {
      return this.params;
   }

   static int getStrength(DHParameters var0) {
      return var0.getL() != 0 ? var0.getL() : var0.getP().bitLength();
   }
}
