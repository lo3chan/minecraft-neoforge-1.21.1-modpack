package amp_libs.org.bouncycastle.jcajce.interfaces;

import amp_libs.org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;
import java.security.Key;

public interface SLHDSAKey extends Key {
   SLHDSAParameterSpec getParameterSpec();
}
