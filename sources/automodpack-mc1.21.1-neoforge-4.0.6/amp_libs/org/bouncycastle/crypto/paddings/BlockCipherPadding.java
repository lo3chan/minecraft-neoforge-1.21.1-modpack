package amp_libs.org.bouncycastle.crypto.paddings;

import amp_libs.org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;

public interface BlockCipherPadding {
   void init(SecureRandom var1) throws IllegalArgumentException;

   String getPaddingName();

   int addPadding(byte[] var1, int var2);

   int padCount(byte[] var1) throws InvalidCipherTextException;
}
