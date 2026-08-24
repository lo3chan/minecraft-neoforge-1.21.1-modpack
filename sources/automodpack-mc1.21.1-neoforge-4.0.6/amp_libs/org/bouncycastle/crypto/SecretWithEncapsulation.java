package amp_libs.org.bouncycastle.crypto;

import javax.security.auth.Destroyable;

public interface SecretWithEncapsulation extends Destroyable {
   byte[] getSecret();

   byte[] getEncapsulation();
}
