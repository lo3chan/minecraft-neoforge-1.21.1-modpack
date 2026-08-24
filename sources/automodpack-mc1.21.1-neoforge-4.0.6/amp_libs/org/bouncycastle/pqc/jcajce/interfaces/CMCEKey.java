package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.CMCEParameterSpec;
import java.security.Key;

public interface CMCEKey extends Key {
   CMCEParameterSpec getParameterSpec();
}
