package amp_libs.org.bouncycastle.jcajce.provider.asymmetric;

import amp_libs.org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import amp_libs.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import amp_libs.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi;

public class Falcon {
   private static final String PREFIX = "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.FALCON", "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi");
         this.addKeyFactoryAlgorithm(
            var1,
            "FALCON-512",
            "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi$Falcon512",
            BCObjectIdentifiers.falcon_512,
            new FalconKeyFactorySpi.Falcon512()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "FALCON-1024",
            "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi$Falcon1024",
            BCObjectIdentifiers.falcon_1024,
            new FalconKeyFactorySpi.Falcon1024()
         );
         var1.addAlgorithm("KeyPairGenerator.FALCON", "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyPairGeneratorSpi");
         this.addKeyPairGeneratorAlgorithm(
            var1, "FALCON-512", "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyPairGeneratorSpi$Falcon512", BCObjectIdentifiers.falcon_512
         );
         this.addKeyPairGeneratorAlgorithm(
            var1, "FALCON-1024", "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyPairGeneratorSpi$Falcon1024", BCObjectIdentifiers.falcon_1024
         );
         this.addSignatureAlgorithm(var1, "FALCON", "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.SignatureSpi$Base", BCObjectIdentifiers.falcon);
         this.addSignatureAlgorithm(
            var1, "FALCON-512", "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.SignatureSpi$Falcon512", BCObjectIdentifiers.falcon_512
         );
         this.addSignatureAlgorithm(
            var1, "FALCON-1024", "amp_libs.org.bouncycastle.pqc.jcajce.provider.falcon.SignatureSpi$Falcon1024", BCObjectIdentifiers.falcon_1024
         );
         var1.addAlgorithm("Alg.Alias.Signature." + BCObjectIdentifiers.old_falcon_512, "FALCON-512");
         var1.addAlgorithm("Alg.Alias.Signature.OID." + BCObjectIdentifiers.old_falcon_512, "FALCON-512");
         var1.addAlgorithm("Alg.Alias.Signature." + BCObjectIdentifiers.old_falcon_1024, "FALCON-1024");
         var1.addAlgorithm("Alg.Alias.Signature.OID." + BCObjectIdentifiers.old_falcon_1024, "FALCON-1024");
      }
   }
}
