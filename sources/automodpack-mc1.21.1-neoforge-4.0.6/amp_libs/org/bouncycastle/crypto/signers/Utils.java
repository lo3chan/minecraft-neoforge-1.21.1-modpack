package amp_libs.org.bouncycastle.crypto.signers;

import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.crypto.CryptoServiceProperties;
import amp_libs.org.bouncycastle.crypto.CryptoServicePurpose;
import amp_libs.org.bouncycastle.crypto.constraints.ConstraintUtils;
import amp_libs.org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import amp_libs.org.bouncycastle.crypto.params.DSAKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.ECKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.GOST3410KeyParameters;

class Utils {
   static CryptoServiceProperties getDefaultProperties(String var0, DSAKeyParameters var1, boolean var2) {
      return new DefaultServiceProperties(var0, ConstraintUtils.bitsOfSecurityFor(var1.getParameters().getP()), var1, getPurpose(var2));
   }

   static CryptoServiceProperties getDefaultProperties(String var0, GOST3410KeyParameters var1, boolean var2) {
      return new DefaultServiceProperties(var0, ConstraintUtils.bitsOfSecurityFor(var1.getParameters().getP()), var1, getPurpose(var2));
   }

   static CryptoServiceProperties getDefaultProperties(String var0, ECKeyParameters var1, boolean var2) {
      return new DefaultServiceProperties(var0, ConstraintUtils.bitsOfSecurityFor(var1.getParameters().getCurve()), var1, getPurpose(var2));
   }

   static CryptoServiceProperties getDefaultProperties(String var0, int var1, CipherParameters var2, boolean var3) {
      return new DefaultServiceProperties(var0, var1, var2, getPurpose(var3));
   }

   static CryptoServicePurpose getPurpose(boolean var0) {
      return var0 ? CryptoServicePurpose.SIGNING : CryptoServicePurpose.VERIFYING;
   }
}
