package amp_libs.org.bouncycastle.crypto.parsers;

import amp_libs.org.bouncycastle.crypto.KeyParser;
import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.crypto.params.DHParameters;
import amp_libs.org.bouncycastle.crypto.params.DHPublicKeyParameters;
import amp_libs.org.bouncycastle.util.io.Streams;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class DHIESPublicKeyParser implements KeyParser {
   private DHParameters dhParams;

   public DHIESPublicKeyParser(DHParameters var1) {
      this.dhParams = var1;
   }

   @Override
   public AsymmetricKeyParameter readKey(InputStream var1) throws IOException {
      byte[] var2 = new byte[(this.dhParams.getP().bitLength() + 7) / 8];
      Streams.readFully(var1, var2, 0, var2.length);
      return new DHPublicKeyParameters(new BigInteger(1, var2), this.dhParams);
   }
}
