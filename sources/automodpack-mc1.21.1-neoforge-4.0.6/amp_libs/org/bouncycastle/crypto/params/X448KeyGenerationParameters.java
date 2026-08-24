package amp_libs.org.bouncycastle.crypto.params;

import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class X448KeyGenerationParameters extends KeyGenerationParameters {
   public X448KeyGenerationParameters(SecureRandom var1) {
      super(var1, 448);
   }
}
