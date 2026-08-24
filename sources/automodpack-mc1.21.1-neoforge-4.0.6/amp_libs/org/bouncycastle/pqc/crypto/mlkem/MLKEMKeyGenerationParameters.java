package amp_libs.org.bouncycastle.pqc.crypto.mlkem;

import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class MLKEMKeyGenerationParameters extends KeyGenerationParameters {
   private final MLKEMParameters params;

   public MLKEMKeyGenerationParameters(SecureRandom var1, MLKEMParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public MLKEMParameters getParameters() {
      return this.params;
   }
}
