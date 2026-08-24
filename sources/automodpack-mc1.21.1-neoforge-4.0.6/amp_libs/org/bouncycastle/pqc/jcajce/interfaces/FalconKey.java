package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;
import java.security.Key;

public interface FalconKey extends Key {
   FalconParameterSpec getParameterSpec();
}
