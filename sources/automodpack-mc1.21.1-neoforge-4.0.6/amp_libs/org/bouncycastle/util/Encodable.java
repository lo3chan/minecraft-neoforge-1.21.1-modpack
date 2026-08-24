package amp_libs.org.bouncycastle.util;

import java.io.IOException;

public interface Encodable {
   byte[] getEncoded() throws IOException;
}
