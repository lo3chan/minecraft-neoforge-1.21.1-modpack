package amp_libs.org.bouncycastle.crypto.params;

import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class ElGamalKeyGenerationParameters extends KeyGenerationParameters {
   private ElGamalParameters params;

   public ElGamalKeyGenerationParameters(SecureRandom var1, ElGamalParameters var2) {
      super(var1, getStrength(var2));
      this.params = var2;
   }

   public ElGamalParameters getParameters() {
      return this.params;
   }

   static int getStrength(ElGamalParameters var0) {
      return var0.getL() != 0 ? var0.getL() : var0.getP().bitLength();
   }
}
