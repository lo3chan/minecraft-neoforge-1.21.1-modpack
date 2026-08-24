package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;
import java.security.Key;

public interface HQCKey extends Key {
   HQCParameterSpec getParameterSpec();
}
