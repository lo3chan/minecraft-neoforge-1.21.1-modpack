package amp_libs.org.bouncycastle.crypto;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.IOException;
import java.io.InputStream;

public interface KeyParser {
   AsymmetricKeyParameter readKey(InputStream var1) throws IOException;
}
