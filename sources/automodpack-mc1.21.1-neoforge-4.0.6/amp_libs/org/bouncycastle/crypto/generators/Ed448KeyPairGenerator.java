package amp_libs.org.bouncycastle.crypto.generators;

import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import amp_libs.org.bouncycastle.crypto.CryptoServicePurpose;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.KeyGenerationParameters;
import amp_libs.org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import java.security.SecureRandom;

public class Ed448KeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("Ed448KeyGen", 224, null, CryptoServicePurpose.KEYGEN));
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      Ed448PrivateKeyParameters var1 = new Ed448PrivateKeyParameters(this.random);
      Ed448PublicKeyParameters var2 = var1.generatePublicKey();
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var2, (AsymmetricKeyParameter)var1);
   }
}
