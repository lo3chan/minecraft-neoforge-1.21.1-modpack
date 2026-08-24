package amp_libs.org.bouncycastle.crypto.modes;

import amp_libs.org.bouncycastle.crypto.BlockCipher;
import amp_libs.org.bouncycastle.crypto.MultiBlockCipher;
import amp_libs.org.bouncycastle.crypto.SkippingStreamCipher;

public interface CTRModeCipher extends MultiBlockCipher, SkippingStreamCipher {
   BlockCipher getUnderlyingCipher();
}
