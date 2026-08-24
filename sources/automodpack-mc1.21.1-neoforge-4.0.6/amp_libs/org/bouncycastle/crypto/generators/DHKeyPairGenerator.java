package amp_libs.org.bouncycastle.crypto.generators;

import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import amp_libs.org.bouncycastle.crypto.CryptoServicePurpose;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import amp_libs.org.bouncycastle.crypto.constraints.ConstraintUtils;
import amp_libs.org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import amp_libs.org.bouncycastle.crypto.params.DHParameters;
import amp_libs.org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.DHPublicKeyParameters;
import java.math.BigInteger;

public class DHKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private DHKeyGenerationParameters param;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (DHKeyGenerationParameters)var1;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(
            "DHKeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN
         )
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      DHKeyGeneratorHelper var1 = DHKeyGeneratorHelper.INSTANCE;
      DHParameters var2 = this.param.getParameters();
      BigInteger var3 = var1.calculatePrivate(var2, this.param.getRandom());
      BigInteger var4 = var1.calculatePublic(var2, var3);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new DHPublicKeyParameters(var4, var2)), (AsymmetricKeyParameter)(new DHPrivateKeyParameters(var3, var2))
      );
   }
}
