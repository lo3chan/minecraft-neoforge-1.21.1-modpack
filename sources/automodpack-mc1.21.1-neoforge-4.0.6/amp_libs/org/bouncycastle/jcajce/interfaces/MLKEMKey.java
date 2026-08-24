package amp_libs.org.bouncycastle.jcajce.interfaces;

import amp_libs.org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import java.security.Key;

public interface MLKEMKey extends Key {
   MLKEMParameterSpec getParameterSpec();
}
