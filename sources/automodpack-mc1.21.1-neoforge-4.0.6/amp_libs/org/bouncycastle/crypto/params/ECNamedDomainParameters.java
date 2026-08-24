package amp_libs.org.bouncycastle.crypto.params;

import amp_libs.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import amp_libs.org.bouncycastle.asn1.x9.ECNamedCurveTable;
import amp_libs.org.bouncycastle.asn1.x9.X9ECParameters;
import amp_libs.org.bouncycastle.crypto.ec.CustomNamedCurves;
import amp_libs.org.bouncycastle.math.ec.ECConstants;
import amp_libs.org.bouncycastle.math.ec.ECCurve;
import amp_libs.org.bouncycastle.math.ec.ECPoint;
import java.math.BigInteger;

public class ECNamedDomainParameters extends ECDomainParameters {
   private ASN1ObjectIdentifier name;

   public static ECNamedDomainParameters lookup(ASN1ObjectIdentifier var0) {
      X9ECParameters var1 = CustomNamedCurves.getByOID(var0);
      if (var1 == null) {
         var1 = ECNamedCurveTable.getByOID(var0);
      }

      return new ECNamedDomainParameters(var0, var1);
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, ECCurve var2, ECPoint var3, BigInteger var4) {
      this(var1, var2, var3, var4, ECConstants.ONE, null);
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, ECCurve var2, ECPoint var3, BigInteger var4, BigInteger var5) {
      this(var1, var2, var3, var4, var5, null);
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, ECCurve var2, ECPoint var3, BigInteger var4, BigInteger var5, byte[] var6) {
      super(var2, var3, var4, var5, var6);
      this.name = var1;
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, ECDomainParameters var2) {
      super(var2.getCurve(), var2.getG(), var2.getN(), var2.getH(), var2.getSeed());
      this.name = var1;
   }

   public ECNamedDomainParameters(ASN1ObjectIdentifier var1, X9ECParameters var2) {
      super(var2);
      this.name = var1;
   }

   public ASN1ObjectIdentifier getName() {
      return this.name;
   }
}
