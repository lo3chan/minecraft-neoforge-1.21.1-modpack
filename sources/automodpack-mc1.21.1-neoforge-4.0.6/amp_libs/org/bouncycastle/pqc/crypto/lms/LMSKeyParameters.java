package amp_libs.org.bouncycastle.pqc.crypto.lms;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import amp_libs.org.bouncycastle.util.Encodable;
import java.io.IOException;

public abstract class LMSKeyParameters extends AsymmetricKeyParameter implements Encodable {
   protected LMSKeyParameters(boolean var1) {
      super(var1);
   }

   @Override
   public abstract byte[] getEncoded() throws IOException;
}
