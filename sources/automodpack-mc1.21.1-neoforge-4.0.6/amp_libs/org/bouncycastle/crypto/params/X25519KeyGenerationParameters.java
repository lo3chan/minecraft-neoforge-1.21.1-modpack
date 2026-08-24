package amp_libs.org.bouncycastle.crypto.params;

import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;

public class X25519KeyGenerationParameters extends KeyGenerationParameters {
   public X25519KeyGenerationParameters(SecureRandom var1) {
      super(var1, 255);
   }
}
