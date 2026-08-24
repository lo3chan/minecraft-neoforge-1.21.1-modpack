package amp_libs.org.bouncycastle.crypto;

import java.security.SecureRandom;

public interface SecureRandomProvider {
   SecureRandom get();
}
