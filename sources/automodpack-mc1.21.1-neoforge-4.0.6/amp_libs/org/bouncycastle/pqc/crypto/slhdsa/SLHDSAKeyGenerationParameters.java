package amp_libs.org.bouncycastle.pqc.crypto.slhdsa;

import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class SLHDSAKeyGenerationParameters extends KeyGenerationParameters {
   private final SLHDSAParameters parameters;

   public SLHDSAKeyGenerationParameters(SecureRandom var1, SLHDSAParameters var2) {
      super(var1, -1);
      this.parameters = var2;
   }

   SLHDSAParameters getParameters() {
      return this.parameters;
   }
}
