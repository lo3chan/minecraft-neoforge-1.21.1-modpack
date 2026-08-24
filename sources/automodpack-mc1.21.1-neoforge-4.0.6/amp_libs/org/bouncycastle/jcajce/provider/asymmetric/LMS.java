package amp_libs.org.bouncycastle.jcajce.provider.asymmetric;

import amp_libs.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import amp_libs.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import amp_libs.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class LMS {
   private static final String PREFIX = "amp_libs.org.bouncycastle.pqc.jcajce.provider.lms.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.LMS", "amp_libs.org.bouncycastle.pqc.jcajce.provider.lms.LMSKeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory." + PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, "LMS");
         var1.addAlgorithm("KeyPairGenerator.LMS", "amp_libs.org.bouncycastle.pqc.jcajce.provider.lms.LMSKeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator." + PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, "LMS");
         var1.addAlgorithm("Signature.LMS", "amp_libs.org.bouncycastle.pqc.jcajce.provider.lms.LMSSignatureSpi$generic");
         var1.addAlgorithm("Alg.Alias.Signature." + PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, "LMS");
      }
   }
}
