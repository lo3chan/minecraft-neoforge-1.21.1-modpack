package amp_libs.org.bouncycastle.crypto.generators;

import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.ECPublicKeyParameters;

public class DSTU4145KeyPairGenerator extends ECKeyPairGenerator {
   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      AsymmetricCipherKeyPair var1 = super.generateKeyPair();
      ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1.getPublic();
      ECPrivateKeyParameters var3 = (ECPrivateKeyParameters)var1.getPrivate();
      var2 = new ECPublicKeyParameters(var2.getQ().negate(), var2.getParameters());
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var2, (AsymmetricKeyParameter)var3);
   }
}
