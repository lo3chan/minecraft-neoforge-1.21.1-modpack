package amp_libs.org.bouncycastle.jcajce.provider.asymmetric;

import amp_libs.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import amp_libs.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class IES {
   private static final String PREFIX = "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ies.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameters.IES", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ies.AlgorithmParametersSpi");
         var1.addAlgorithm("AlgorithmParameters.ECIES", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ies.AlgorithmParametersSpi");
      }
   }
}
