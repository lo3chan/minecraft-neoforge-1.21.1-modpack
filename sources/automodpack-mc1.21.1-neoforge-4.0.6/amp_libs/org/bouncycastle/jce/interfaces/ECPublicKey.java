package amp_libs.org.bouncycastle.jce.interfaces;

import amp_libs.org.bouncycastle.math.ec.ECPoint;
import java.security.PublicKey;

public interface ECPublicKey extends ECKey, PublicKey {
   ECPoint getQ();
}
