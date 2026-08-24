package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import java.security.Key;

public interface DilithiumKey extends Key {
   DilithiumParameterSpec getParameterSpec();
}
