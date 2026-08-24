package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.SnovaParameterSpec;
import java.security.Key;

public interface SnovaKey extends Key {
   SnovaParameterSpec getParameterSpec();
}
