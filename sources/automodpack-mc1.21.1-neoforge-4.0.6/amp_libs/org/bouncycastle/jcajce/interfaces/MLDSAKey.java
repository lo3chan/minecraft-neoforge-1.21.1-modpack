package amp_libs.org.bouncycastle.jcajce.interfaces;

import amp_libs.org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import java.security.Key;

public interface MLDSAKey extends Key {
   MLDSAParameterSpec getParameterSpec();
}
