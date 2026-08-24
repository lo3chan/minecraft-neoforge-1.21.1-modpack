package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.MayoParameterSpec;
import java.security.Key;

public interface MayoKey extends Key {
   MayoParameterSpec getParameterSpec();
}
