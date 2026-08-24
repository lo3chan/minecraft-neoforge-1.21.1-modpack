package amp_libs.org.bouncycastle.jcajce.util;

import amp_libs.org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Provider;
import java.security.Security;

public class BCJcaJceHelper extends ProviderJcaJceHelper {
   private static volatile Provider bcProvider;

   private static synchronized Provider getBouncyCastleProvider() {
      Provider var0 = Security.getProvider("BC");
      if (var0 instanceof BouncyCastleProvider) {
         return var0;
      } else if (bcProvider != null) {
         return bcProvider;
      } else {
         bcProvider = new BouncyCastleProvider();
         return bcProvider;
      }
   }

   public BCJcaJceHelper() {
      super(getBouncyCastleProvider());
   }
}
