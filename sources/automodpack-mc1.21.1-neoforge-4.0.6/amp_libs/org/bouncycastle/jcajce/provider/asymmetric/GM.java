package amp_libs.org.bouncycastle.jcajce.provider.asymmetric;

import amp_libs.org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import amp_libs.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import amp_libs.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import java.util.HashMap;
import java.util.Map;

public class GM {
   private static final String PREFIX = "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.";
   private static final Map<String, String> generalSm2Attributes = new HashMap<>();

   static {
      generalSm2Attributes.put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
      generalSm2Attributes.put("SupportedKeyFormats", "PKCS#8|X.509");
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Signature.SHA256WITHSM2", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMSignatureSpi$sha256WithSM2");
         var1.addAlgorithm("Alg.Alias.Signature." + GMObjectIdentifiers.sm2sign_with_sha256, "SHA256WITHSM2");
         var1.addAlgorithm("Signature.SM3WITHSM2", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMSignatureSpi$sm3WithSM2");
         var1.addAlgorithm("Alg.Alias.Signature." + GMObjectIdentifiers.sm2sign_with_sm3, "SM3WITHSM2");
         var1.addAlgorithm("KeyPairGenerator.SM2", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMKeyPairGeneratorSpi$SM2");
         var1.addAlgorithm("Cipher.SM2", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2");
         var1.addAlgorithm("Alg.Alias.Cipher.SM2WITHSM3", "SM2");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_sm3, "SM2");
         var1.addAlgorithm("Cipher.SM2WITHBLAKE2B", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withBlake2b");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_blake2b512, "SM2WITHBLAKE2B");
         var1.addAlgorithm("Cipher.SM2WITHBLAKE2S", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withBlake2s");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_blake2s256, "SM2WITHBLAKE2S");
         var1.addAlgorithm("Cipher.SM2WITHWHIRLPOOL", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withWhirlpool");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_whirlpool, "SM2WITHWHIRLPOOL");
         var1.addAlgorithm("Cipher.SM2WITHMD5", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withMD5");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_md5, "SM2WITHMD5");
         var1.addAlgorithm("Cipher.SM2WITHRIPEMD160", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withRMD");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_rmd160, "SM2WITHRIPEMD160");
         var1.addAlgorithm("Cipher.SM2WITHSHA1", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha1");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_sha1, "SM2WITHSHA1");
         var1.addAlgorithm("Cipher.SM2WITHSHA224", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha224");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_sha224, "SM2WITHSHA224");
         var1.addAlgorithm("Cipher.SM2WITHSHA256", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha256");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_sha256, "SM2WITHSHA256");
         var1.addAlgorithm("Cipher.SM2WITHSHA384", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha384");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_sha384, "SM2WITHSHA384");
         var1.addAlgorithm("Cipher.SM2WITHSHA512", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi$SM2withSha512");
         var1.addAlgorithm("Alg.Alias.Cipher." + GMObjectIdentifiers.sm2encrypt_with_sha512, "SM2WITHSHA512");
      }
   }
}
