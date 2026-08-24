package amp_libs.org.bouncycastle.jcajce.provider.asymmetric.gost;

import amp_libs.org.bouncycastle.crypto.params.GOST3410Parameters;
import amp_libs.org.bouncycastle.util.Arrays;
import amp_libs.org.bouncycastle.util.Fingerprint;
import amp_libs.org.bouncycastle.util.Strings;
import java.math.BigInteger;

class GOSTUtil {
   static String privateKeyToString(String var0, BigInteger var1, GOST3410Parameters var2) {
      StringBuilder var3 = new StringBuilder();
      String var4 = Strings.lineSeparator();
      BigInteger var5 = var2.getA().modPow(var1, var2.getP());
      var3.append(var0);
      var3.append(" Private Key [").append(generateKeyFingerprint(var5, var2)).append("]").append(var4);
      var3.append("                  Y: ").append(var5.toString(16)).append(var4);
      return var3.toString();
   }

   static String publicKeyToString(String var0, BigInteger var1, GOST3410Parameters var2) {
      StringBuilder var3 = new StringBuilder();
      String var4 = Strings.lineSeparator();
      var3.append(var0);
      var3.append(" Public Key [").append(generateKeyFingerprint(var1, var2)).append("]").append(var4);
      var3.append("                 Y: ").append(var1.toString(16)).append(var4);
      return var3.toString();
   }

   private static String generateKeyFingerprint(BigInteger var0, GOST3410Parameters var1) {
      return new Fingerprint(Arrays.concatenate(var0.toByteArray(), var1.getP().toByteArray(), var1.getA().toByteArray())).toString();
   }
}
