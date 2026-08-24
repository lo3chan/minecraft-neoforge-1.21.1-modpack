package amp_libs.org.bouncycastle.crypto.params;

import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class Ed25519KeyGenerationParameters extends KeyGenerationParameters {
   public Ed25519KeyGenerationParameters(SecureRandom var1) {
      super(var1, 256);
   }
}
