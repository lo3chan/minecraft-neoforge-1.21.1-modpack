package amp_libs.org.bouncycastle.crypto.params;

import amp_libs.org.bouncycastle.math.ec.ECPoint;

public class ECPublicKeyParameters extends ECKeyParameters {
   private final ECPoint q;

   public ECPublicKeyParameters(ECPoint var1, ECDomainParameters var2) {
      super(false, var2);
      this.q = var2.validatePublicPoint(var1);
   }

   public ECPoint getQ() {
      return this.q;
   }
}
