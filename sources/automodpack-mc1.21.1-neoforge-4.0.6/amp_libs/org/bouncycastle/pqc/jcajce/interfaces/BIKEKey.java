package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;
import java.security.Key;

public interface BIKEKey extends Key {
   BIKEParameterSpec getParameterSpec();
}
