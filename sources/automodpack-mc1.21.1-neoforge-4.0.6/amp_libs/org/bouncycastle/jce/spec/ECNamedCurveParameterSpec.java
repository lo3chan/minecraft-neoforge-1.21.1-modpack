package amp_libs.org.bouncycastle.jce.spec;

import amp_libs.org.bouncycastle.math.ec.ECCurve;
import amp_libs.org.bouncycastle.math.ec.ECPoint;
import java.math.BigInteger;

public class ECNamedCurveParameterSpec extends ECParameterSpec {
   private String name;

   public ECNamedCurveParameterSpec(String var1, ECCurve var2, ECPoint var3, BigInteger var4) {
      super(var2, var3, var4);
      this.name = var1;
   }

   public ECNamedCurveParameterSpec(String var1, ECCurve var2, ECPoint var3, BigInteger var4, BigInteger var5) {
      super(var2, var3, var4, var5);
      this.name = var1;
   }

   public ECNamedCurveParameterSpec(String var1, ECCurve var2, ECPoint var3, BigInteger var4, BigInteger var5, byte[] var6) {
      super(var2, var3, var4, var5, var6);
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }
}
