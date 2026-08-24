package amp_libs.org.bouncycastle.pqc.crypto.mlkem;

import amp_libs.org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import amp_libs.org.bouncycastle.crypto.SecretWithEncapsulation;
import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import java.security.SecureRandom;

public class MLKEMGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom sr;

   public MLKEMGenerator(SecureRandom var1) {
      this.sr = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      byte[] var2 = new byte[32];
      this.sr.nextBytes(var2);
      return this.internalGenerateEncapsulated(var1, var2);
   }

   public SecretWithEncapsulation internalGenerateEncapsulated(AsymmetricKeyParameter var1, byte[] var2) {
      MLKEMPublicKeyParameters var3 = (MLKEMPublicKeyParameters)var1;
      MLKEMEngine var4 = var3.getParameters().getEngine();
      var4.init(this.sr);
      byte[][] var5 = var4.kemEncrypt(var3, var2);
      return new SecretWithEncapsulationImpl(var5[0], var5[1]);
   }
}
