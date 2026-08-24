package amp_libs.org.bouncycastle.pqc.crypto;

import amp_libs.org.bouncycastle.crypto.CipherParameters;

public interface MessageSigner {
   void init(boolean var1, CipherParameters var2);

   byte[] generateSignature(byte[] var1);

   boolean verifySignature(byte[] var1, byte[] var2);
}
