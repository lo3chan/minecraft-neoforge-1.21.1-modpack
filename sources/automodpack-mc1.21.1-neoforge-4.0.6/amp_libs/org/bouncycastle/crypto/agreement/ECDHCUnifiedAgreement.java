package amp_libs.org.bouncycastle.crypto.agreement;

import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.crypto.CryptoServicesRegistrar;
import amp_libs.org.bouncycastle.crypto.params.ECDHUPrivateParameters;
import amp_libs.org.bouncycastle.crypto.params.ECDHUPublicParameters;
import amp_libs.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;

public class ECDHCUnifiedAgreement {
   private ECDHUPrivateParameters privParams;

   public void init(CipherParameters var1) {
      this.privParams = (ECDHUPrivateParameters)var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECCDHU", this.privParams.getStaticPrivateKey()));
   }

   public int getFieldSize() {
      return this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldElementEncodingLength();
   }

   public byte[] calculateAgreement(CipherParameters var1) {
      ECDHUPublicParameters var2 = (ECDHUPublicParameters)var1;
      ECDHCBasicAgreement var3 = new ECDHCBasicAgreement();
      ECDHCBasicAgreement var4 = new ECDHCBasicAgreement();
      var3.init(this.privParams.getStaticPrivateKey());
      BigInteger var5 = var3.calculateAgreement(var2.getStaticPublicKey());
      var4.init(this.privParams.getEphemeralPrivateKey());
      BigInteger var6 = var4.calculateAgreement(var2.getEphemeralPublicKey());
      int var7 = this.getFieldSize();
      byte[] var8 = new byte[var7 * 2];
      BigIntegers.asUnsignedByteArray(var6, var8, 0, var7);
      BigIntegers.asUnsignedByteArray(var5, var8, var7, var7);
      return var8;
   }
}
