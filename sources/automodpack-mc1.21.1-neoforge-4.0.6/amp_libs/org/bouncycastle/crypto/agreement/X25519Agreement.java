package amp_libs.org.bouncycastle.crypto.agreement;

import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.RawAgreement;
import amp_libs.org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.X25519PublicKeyParameters;

public final class X25519Agreement implements RawAgreement {
   private X25519PrivateKeyParameters privateKey;

   @Override
   public void init(CipherParameters var1) {
      this.privateKey = (X25519PrivateKeyParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("X25519", this.privateKey));
   }

   @Override
   public int getAgreementSize() {
      return 32;
   }

   @Override
   public void calculateAgreement(CipherParameters var1, byte[] var2, int var3) {
      this.privateKey.generateSecret((X25519PublicKeyParameters)var1, var2, var3);
   }
}
