package amp_libs.org.bouncycastle.jcajce.provider.asymmetric;

import amp_libs.org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyFactorySpi;
import amp_libs.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import amp_libs.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class ElGamal {
   private static final String PREFIX = "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameterGenerator.ELGAMAL", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParameterGeneratorSpi");
         var1.addAlgorithm("AlgorithmParameterGenerator.ElGamal", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParameterGeneratorSpi");
         var1.addAlgorithm("AlgorithmParameters.ELGAMAL", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParametersSpi");
         var1.addAlgorithm("AlgorithmParameters.ElGamal", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.AlgorithmParametersSpi");
         var1.addAlgorithm("Cipher.ELGAMAL", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.CipherSpi$NoPadding");
         var1.addAlgorithm("Cipher.ElGamal", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.CipherSpi$NoPadding");
         var1.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/ECB/PKCS1PADDING", "ELGAMAL/PKCS1");
         var1.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/NONE/PKCS1PADDING", "ELGAMAL/PKCS1");
         var1.addAlgorithm("Alg.Alias.Cipher.ELGAMAL/NONE/NOPADDING", "ELGAMAL");
         var1.addAlgorithm("Cipher.ELGAMAL/PKCS1", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.CipherSpi$PKCS1v1_5Padding");
         var1.addAlgorithm("KeyFactory.ELGAMAL", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyFactorySpi");
         var1.addAlgorithm("KeyFactory.ElGamal", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.ELGAMAL", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyPairGeneratorSpi");
         var1.addAlgorithm("KeyPairGenerator.ElGamal", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.elgamal.KeyPairGeneratorSpi");
         KeyFactorySpi var2 = new KeyFactorySpi();
         this.registerOid(var1, OIWObjectIdentifiers.elGamalAlgorithm, "ELGAMAL", var2);
         this.registerOidAlgorithmParameterGenerator(var1, OIWObjectIdentifiers.elGamalAlgorithm, "ELGAMAL");
      }
   }
}
