package amp_libs.org.bouncycastle.crypto.agreement;

import amp_libs.org.bouncycastle.crypto.BasicAgreement;
import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.params.ECDomainParameters;
import amp_libs.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.ECPublicKeyParameters;
import amp_libs.org.bouncycastle.math.ec.ECAlgorithms;
import amp_libs.org.bouncycastle.math.ec.ECPoint;
import java.math.BigInteger;

public class ECDHCBasicAgreement implements BasicAgreement {
   ECPrivateKeyParameters key;

   @Override
   public void init(CipherParameters var1) {
      this.key = (ECPrivateKeyParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECCDH", this.key));
   }

   @Override
   public int getFieldSize() {
      return this.key.getParameters().getCurve().getFieldElementEncodingLength();
   }

   @Override
   public BigInteger calculateAgreement(CipherParameters var1) {
      ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1;
      ECDomainParameters var3 = this.key.getParameters();
      if (!var3.equals(var2.getParameters())) {
         throw new IllegalStateException("ECDHC public key has wrong domain parameters");
      } else {
         BigInteger var4 = var3.getH().multiply(this.key.getD()).mod(var3.getN());
         ECPoint var5 = ECAlgorithms.cleanPoint(var3.getCurve(), var2.getQ());
         if (var5.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECDHC");
         } else {
            ECPoint var6 = var5.multiply(var4).normalize();
            if (var6.isInfinity()) {
               throw new IllegalStateException("Infinity is not a valid agreement value for ECDHC");
            } else {
               return var6.getAffineXCoord().toBigInteger();
            }
         }
      }
   }
}
