package amp_libs.org.bouncycastle.crypto.modes;

import amp_libs.org.bouncycastle.crypto.BlockCipher;
import amp_libs.org.bouncycastle.crypto.MultiBlockCipher;

public interface CBCModeCipher extends MultiBlockCipher {
   BlockCipher getUnderlyingCipher();
}
