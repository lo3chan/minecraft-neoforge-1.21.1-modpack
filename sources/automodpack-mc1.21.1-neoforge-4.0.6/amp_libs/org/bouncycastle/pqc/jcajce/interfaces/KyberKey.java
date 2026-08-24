package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import java.security.Key;

public interface KyberKey extends Key {
   KyberParameterSpec getParameterSpec();
}
