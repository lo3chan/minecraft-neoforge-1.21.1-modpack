package amp_libs.org.bouncycastle.jce;

import amp_libs.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import amp_libs.org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import amp_libs.org.bouncycastle.asn1.x9.X9ECParameters;
import amp_libs.org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import java.util.Enumeration;

public class ECGOST3410NamedCurveTable {
   public static ECNamedCurveParameterSpec getParameterSpec(String var0) {
      X9ECParameters var1 = ECGOST3410NamedCurves.getByNameX9(var0);
      if (var1 == null) {
         try {
            var1 = ECGOST3410NamedCurves.getByOIDX9(new ASN1ObjectIdentifier(var0));
         } catch (IllegalArgumentException var3) {
            return null;
         }
      }

      return var1 == null ? null : new ECNamedCurveParameterSpec(var0, var1.getCurve(), var1.getG(), var1.getN(), var1.getH(), var1.getSeed());
   }

   public static Enumeration getNames() {
      return ECGOST3410NamedCurves.getNames();
   }
}
