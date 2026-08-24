package amp_libs.org.bouncycastle.crypto;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface KeyEncoder {
   byte[] getEncoded(AsymmetricKeyParameter var1);
}
