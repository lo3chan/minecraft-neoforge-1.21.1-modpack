package amp_libs.org.bouncycastle.jce.interfaces;

import amp_libs.org.bouncycastle.jce.spec.ElGamalParameterSpec;
import javax.crypto.interfaces.DHKey;

public interface ElGamalKey extends DHKey {
   ElGamalParameterSpec getParameters();
}
