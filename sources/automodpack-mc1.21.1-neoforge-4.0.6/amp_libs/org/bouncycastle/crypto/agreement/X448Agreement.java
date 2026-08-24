package amp_libs.org.bouncycastle.crypto.agreement;

import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.RawAgreement;
import amp_libs.org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import amp_libs.org.bouncycastle.crypto.params.X448PublicKeyParameters;

public final class X448Agreement implements RawAgreement {
   private X448PrivateKeyParameters privateKey;

   @Override
   public void init(CipherParameters var1) {
      this.privateKey = (X448PrivateKeyParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("X448", this.privateKey));
   }

   @Override
   public int getAgreementSize() {
      return 56;
   }

   @Override
   public void calculateAgreement(CipherParameters var1, byte[] var2, int var3) {
      this.privateKey.generateSecret((X448PublicKeyParameters)var1, var2, var3);
   }
}
