package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import java.security.Key;

public interface NTRUKey extends Key {
   NTRUParameterSpec getParameterSpec();
}
