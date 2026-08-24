package amp_libs.org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import amp_libs.org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import amp_libs.org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import java.util.HashMap;
import java.util.Map;

class Utils {
   private static Map parameters = new HashMap();

   static MLDSAParameters getParameters(String var0) {
      return (MLDSAParameters)parameters.get(var0);
   }

   static {
      parameters.put(MLDSAParameterSpec.ml_dsa_44.getName(), MLDSAParameters.ml_dsa_44);
      parameters.put(MLDSAParameterSpec.ml_dsa_65.getName(), MLDSAParameters.ml_dsa_65);
      parameters.put(MLDSAParameterSpec.ml_dsa_87.getName(), MLDSAParameters.ml_dsa_87);
      parameters.put(MLDSAParameterSpec.ml_dsa_44_with_sha512.getName(), MLDSAParameters.ml_dsa_44_with_sha512);
      parameters.put(MLDSAParameterSpec.ml_dsa_65_with_sha512.getName(), MLDSAParameters.ml_dsa_65_with_sha512);
      parameters.put(MLDSAParameterSpec.ml_dsa_87_with_sha512.getName(), MLDSAParameters.ml_dsa_87_with_sha512);
   }
}
