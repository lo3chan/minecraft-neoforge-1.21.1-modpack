package amp_libs.org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import amp_libs.org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import amp_libs.org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import java.util.HashMap;
import java.util.Map;

class Utils {
   private static Map parameters = new HashMap();

   static MLKEMParameters getParameters(String var0) {
      return (MLKEMParameters)parameters.get(var0);
   }

   static {
      parameters.put(MLKEMParameterSpec.ml_kem_512.getName(), MLKEMParameters.ml_kem_512);
      parameters.put(MLKEMParameterSpec.ml_kem_768.getName(), MLKEMParameters.ml_kem_768);
      parameters.put(MLKEMParameterSpec.ml_kem_1024.getName(), MLKEMParameters.ml_kem_1024);
   }
}
