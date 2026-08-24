package amp_libs.org.bouncycastle.pqc.jcajce.interfaces;

import amp_libs.org.bouncycastle.pqc.jcajce.spec.PicnicParameterSpec;
import java.security.Key;

public interface PicnicKey extends Key {
   PicnicParameterSpec getParameterSpec();
}
