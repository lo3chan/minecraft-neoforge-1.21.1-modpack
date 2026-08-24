package amp_libs.org.bouncycastle.crypto.generators;

import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import amp_libs.org.bouncycastle.crypto.CryptoServicePurpose;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import amp_libs.org.bouncycastle.crypto.constraints.ConstraintUtils;
import amp_libs.org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import amp_libs.org.bouncycastle.crypto.params.DSAParameters;
import amp_libs.org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import amp_libs.org.bouncycastle.math.ec.WNafUtil;
import amp_libs.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import java.security.SecureRandom;

public class DSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private DSAKeyGenerationParameters param;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (DSAKeyGenerationParameters)var1;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(
            "DSAKeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN
         )
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      DSAParameters var1 = this.param.getParameters();
      BigInteger var2 = generatePrivateKey(var1.getQ(), this.param.getRandom());
      BigInteger var3 = calculatePublicKey(var1.getP(), var1.getG(), var2);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new DSAPublicKeyParameters(var3, var1)), (AsymmetricKeyParameter)(new DSAPrivateKeyParameters(var2, var1))
      );
   }

   private static BigInteger generatePrivateKey(BigInteger var0, SecureRandom var1) {
      int var2 = var0.bitLength() >>> 2;

      BigInteger var3;
      do {
         var3 = BigIntegers.createRandomInRange(ONE, var0.subtract(ONE), var1);
      } while (WNafUtil.getNafWeight(var3) < var2);

      return var3;
   }

   private static BigInteger calculatePublicKey(BigInteger var0, BigInteger var1, BigInteger var2) {
      return var1.modPow(var2, var0);
   }
}
