package amp_libs.org.bouncycastle.crypto.agreement;

import amp_libs.org.bouncycastle.crypto.BasicAgreement;
import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.params.ECDomainParameters;
import amp_libs.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.ECPublicKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.MQVPrivateParameters;
import amp_libs.org.bouncycastle.crypto.params.MQVPublicParameters;
import amp_libs.org.bouncycastle.math.ec.ECAlgorithms;
import amp_libs.org.bouncycastle.math.ec.ECConstants;
import amp_libs.org.bouncycastle.math.ec.ECCurve;
import amp_libs.org.bouncycastle.math.ec.ECPoint;
import amp_libs.org.bouncycastle.util.Properties;
import java.math.BigInteger;

public class ECMQVBasicAgreement implements BasicAgreement {
   MQVPrivateParameters privParams;

   @Override
   public void init(CipherParameters var1) {
      this.privParams = (MQVPrivateParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECMQV", this.privParams.getStaticPrivateKey()));
   }

   @Override
   public int getFieldSize() {
      return this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldElementEncodingLength();
   }

   @Override
   public BigInteger calculateAgreement(CipherParameters var1) {
      if (Properties.isOverrideSet("amp_libs.org.bouncycastle.ec.disable_mqv")) {
         throw new IllegalStateException("ECMQV explicitly disabled");
      } else {
         MQVPublicParameters var2 = (MQVPublicParameters)var1;
         ECPrivateKeyParameters var3 = this.privParams.getStaticPrivateKey();
         ECDomainParameters var4 = var3.getParameters();
         if (!var4.equals(var2.getStaticPublicKey().getParameters())) {
            throw new IllegalStateException("ECMQV public key components have wrong domain parameters");
         } else {
            ECPoint var5 = this.calculateMqvAgreement(
                  var4,
                  var3,
                  this.privParams.getEphemeralPrivateKey(),
                  this.privParams.getEphemeralPublicKey(),
                  var2.getStaticPublicKey(),
                  var2.getEphemeralPublicKey()
               )
               .normalize();
            if (var5.isInfinity()) {
               throw new IllegalStateException("Infinity is not a valid agreement value for MQV");
            } else {
               return var5.getAffineXCoord().toBigInteger();
            }
         }
      }
   }

   private ECPoint calculateMqvAgreement(
      ECDomainParameters var1,
      ECPrivateKeyParameters var2,
      ECPrivateKeyParameters var3,
      ECPublicKeyParameters var4,
      ECPublicKeyParameters var5,
      ECPublicKeyParameters var6
   ) {
      BigInteger var7 = var1.getN();
      int var8 = (var7.bitLength() + 1) / 2;
      BigInteger var9 = ECConstants.ONE.shiftLeft(var8);
      ECCurve var10 = var1.getCurve();
      ECPoint var11 = ECAlgorithms.cleanPoint(var10, var4.getQ());
      ECPoint var12 = ECAlgorithms.cleanPoint(var10, var5.getQ());
      ECPoint var13 = ECAlgorithms.cleanPoint(var10, var6.getQ());
      BigInteger var14 = var11.getAffineXCoord().toBigInteger();
      BigInteger var15 = var14.mod(var9);
      BigInteger var16 = var15.setBit(var8);
      BigInteger var17 = var2.getD().multiply(var16).add(var3.getD()).mod(var7);
      BigInteger var18 = var13.getAffineXCoord().toBigInteger();
      BigInteger var19 = var18.mod(var9);
      BigInteger var20 = var19.setBit(var8);
      BigInteger var21 = var1.getH().multiply(var17).mod(var7);
      return ECAlgorithms.sumOfTwoMultiplies(var12, var20.multiply(var21).mod(var7), var13, var21);
   }
}
