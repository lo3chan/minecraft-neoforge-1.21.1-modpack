package amp_libs.org.bouncycastle.crypto.digests;

import amp_libs.org.bouncycastle.crypto.EncodableService;

public interface EncodableDigest extends EncodableService {
   @Override
   byte[] getEncodedState();
}
