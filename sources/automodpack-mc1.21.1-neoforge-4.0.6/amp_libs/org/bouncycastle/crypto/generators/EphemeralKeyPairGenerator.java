package amp_libs.org.bouncycastle.crypto.generators;

import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import amp_libs.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import amp_libs.org.bouncycastle.crypto.EphemeralKeyPair;
import amp_libs.org.bouncycastle.crypto.KeyEncoder;

public class EphemeralKeyPairGenerator {
   private AsymmetricCipherKeyPairGenerator gen;
   private KeyEncoder keyEncoder;

   public EphemeralKeyPairGenerator(AsymmetricCipherKeyPairGenerator var1, KeyEncoder var2) {
      this.gen = var1;
      this.keyEncoder = var2;
   }

   public EphemeralKeyPair generate() {
      AsymmetricCipherKeyPair var1 = this.gen.generateKeyPair();
      return new EphemeralKeyPair(var1, this.keyEncoder);
   }
}
