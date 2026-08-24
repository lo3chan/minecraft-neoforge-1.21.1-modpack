package amp_libs.org.bouncycastle.crypto.generators;

import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import amp_libs.org.bouncycastle.crypto.CryptoServicePurpose;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import amp_libs.org.bouncycastle.crypto.constraints.ConstraintUtils;
import amp_libs.org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.crypto.params.DHParameters;
import amp_libs.org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import amp_libs.org.bouncycastle.crypto.params.ElGamalParameters;
import amp_libs.org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import java.math.BigInteger;

public class ElGamalKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private ElGamalKeyGenerationParameters param;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (ElGamalKeyGenerationParameters)var1;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(
            "ElGamalKeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN
         )
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      DHKeyGeneratorHelper var1 = DHKeyGeneratorHelper.INSTANCE;
      ElGamalParameters var2 = this.param.getParameters();
      DHParameters var3 = new DHParameters(var2.getP(), var2.getG(), null, var2.getL());
      BigInteger var4 = var1.calculatePrivate(var3, this.param.getRandom());
      BigInteger var5 = var1.calculatePublic(var3, var4);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new ElGamalPublicKeyParameters(var5, var2)), (AsymmetricKeyParameter)(new ElGamalPrivateKeyParameters(var4, var2))
      );
   }
}
