package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec;
import java.security.Key;

public interface SPHINCSPlusKey extends Key {
   SPHINCSPlusParameterSpec getParameterSpec();
}
