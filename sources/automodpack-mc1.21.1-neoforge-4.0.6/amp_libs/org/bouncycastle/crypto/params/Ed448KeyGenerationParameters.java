package amp_libs.org.bouncycastle.crypto.params;

import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class Ed448KeyGenerationParameters extends KeyGenerationParameters {
   public Ed448KeyGenerationParameters(SecureRandom var1) {
      super(var1, 448);
   }
}
