package amp_libs.org.bouncycastle.jcajce.provider.asymmetric;

import amp_libs.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import amp_libs.org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyFactorySpi;
import amp_libs.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import amp_libs.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import java.util.HashMap;
import java.util.Map;

public class DH {
   private static final String PREFIX = "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.";
   private static final Map<String, String> generalDhAttributes = new HashMap<>();

   static {
      generalDhAttributes.put("SupportedKeyClasses", "javax.crypto.interfaces.DHPublicKey|javax.crypto.interfaces.DHPrivateKey");
      generalDhAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyPairGenerator.DH", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.DIFFIEHELLMAN", "DH");
         var1.addAttributes("KeyAgreement.DH", DH.generalDhAttributes);
         var1.addAlgorithm("KeyAgreement.DH", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi");
         var1.addAlgorithm("Alg.Alias.KeyAgreement.DIFFIEHELLMAN", "DH");
         var1.addAlgorithm(
            "KeyAgreement", PKCSObjectIdentifiers.id_alg_ESDH, "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHwithRFC2631KDF"
         );
         var1.addAlgorithm(
            "KeyAgreement", PKCSObjectIdentifiers.id_alg_SSDH, "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHwithRFC2631KDF"
         );
         var1.addAlgorithm("KeyFactory.DH", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory.DIFFIEHELLMAN", "DH");
         var1.addAlgorithm("AlgorithmParameters.DH", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.AlgorithmParametersSpi");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.DIFFIEHELLMAN", "DH");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.DIFFIEHELLMAN", "DH");
         var1.addAlgorithm("AlgorithmParameterGenerator.DH", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.AlgorithmParameterGeneratorSpi");
         var1.addAlgorithm("Cipher.IES", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.IESCipher$IES");
         var1.addAlgorithm("Cipher.IESwithAES-CBC", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.IESCipher$IESwithAESCBC");
         var1.addAlgorithm("Cipher.IESWITHAES-CBC", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.IESCipher$IESwithAESCBC");
         var1.addAlgorithm("Cipher.IESWITHDESEDE-CBC", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.IESCipher$IESwithDESedeCBC");
         var1.addAlgorithm("Cipher.DHIES", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.IESCipher$IES");
         var1.addAlgorithm("Cipher.DHIESwithAES-CBC", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.IESCipher$IESwithAESCBC");
         var1.addAlgorithm("Cipher.DHIESWITHAES-CBC", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.IESCipher$IESwithAESCBC");
         var1.addAlgorithm("Cipher.DHIESWITHDESEDE-CBC", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.IESCipher$IESwithDESedeCBC");
         var1.addAlgorithm("KeyAgreement.DHWITHSHA1KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHwithSHA1KDF");
         var1.addAlgorithm("KeyAgreement.DHWITHSHA224KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHwithSHA224KDF");
         var1.addAlgorithm("KeyAgreement.DHWITHSHA256KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHwithSHA256KDF");
         var1.addAlgorithm("KeyAgreement.DHWITHSHA384KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHwithSHA384KDF");
         var1.addAlgorithm("KeyAgreement.DHWITHSHA512KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHwithSHA512KDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA1KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA1KDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA224KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA224KDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA256KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA256KDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA384KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA384KDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA512KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA512KDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA1CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA1CKDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA224CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA224CKDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA256CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA256CKDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA384CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA384CKDF");
         var1.addAlgorithm("KeyAgreement.DHUWITHSHA512CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$DHUwithSHA512CKDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA1KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA1KDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA224KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA224KDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA256KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA256KDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA384KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA384KDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA512KDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA512KDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA1CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA1CKDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA224CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA224CKDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA256CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA256CKDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA384CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA384CKDF");
         var1.addAlgorithm("KeyAgreement.MQVWITHSHA512CKDF", "amp_libs.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi$MQVwithSHA512CKDF");
         this.registerOid(var1, PKCSObjectIdentifiers.dhKeyAgreement, "DH", new KeyFactorySpi());
         this.registerOid(var1, X9ObjectIdentifiers.dhpublicnumber, "DH", new KeyFactorySpi());
      }
   }
}
