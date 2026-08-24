package amp_libs.org.bouncycastle.crypto.engines;

import amp_libs.org.bouncycastle.crypto.CryptoServicePurpose;

class Utils {
   static CryptoServicePurpose getPurpose(boolean var0) {
      return var0 ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION;
   }
}
