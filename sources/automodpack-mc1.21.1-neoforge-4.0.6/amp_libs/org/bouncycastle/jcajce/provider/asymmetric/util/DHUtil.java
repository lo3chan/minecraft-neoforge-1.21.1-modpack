package amp_libs.org.bouncycastle.jcajce.provider.asymmetric.util;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.crypto.params.DHParameters;
import amp_libs.org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.DHPublicKeyParameters;
import amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;

public class DHUtil {
   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey var0) throws InvalidKeyException {
      if (var0 instanceof BCDHPublicKey) {
         return ((BCDHPublicKey)var0).engineGetKeyParameters();
      } else if (var0 instanceof DHPublicKey) {
         DHPublicKey var1 = (DHPublicKey)var0;
         return new DHPublicKeyParameters(var1.getY(), new DHParameters(var1.getParams().getP(), var1.getParams().getG(), null, var1.getParams().getL()));
      } else {
         throw new InvalidKeyException("can't identify DH public key.");
      }
   }

   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey var0) throws InvalidKeyException {
      if (var0 instanceof DHPrivateKey) {
         DHPrivateKey var1 = (DHPrivateKey)var0;
         return new DHPrivateKeyParameters(var1.getX(), new DHParameters(var1.getParams().getP(), var1.getParams().getG(), null, var1.getParams().getL()));
      } else {
         throw new InvalidKeyException("can't identify DH private key.");
      }
   }
}
