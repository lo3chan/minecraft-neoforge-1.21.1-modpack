package pl.skidam.automodpack_core.protocol;

import amp_libs.org.bouncycastle.asn1.x500.X500Name;
import amp_libs.org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import amp_libs.org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import amp_libs.org.bouncycastle.jce.provider.BouncyCastleProvider;
import amp_libs.org.bouncycastle.operator.ContentSigner;
import amp_libs.org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HexFormat;
import pl.skidam.automodpack_core.utils.CustomFileUtils;
import pl.skidam.automodpack_core.utils.LockFreeInputStream;

public class NetUtils {
   public static final int MAGIC_AMMH = 1095585096;
   public static final int MAGIC_AMOK = 1095585611;
   public static final byte PROTOCOL_VERSION = 1;
   public static final byte COMPRESSION_NONE = 0;
   public static final byte COMPRESSION_ZSTD = 1;
   public static final byte COMPRESSION_GZIP = 2;
   public static final byte ECHO_TYPE = 0;
   public static final byte FILE_REQUEST_TYPE = 1;
   public static final byte FILE_RESPONSE_TYPE = 2;
   public static final byte REFRESH_REQUEST_TYPE = 3;
   public static final byte END_OF_TRANSMISSION = 4;
   public static final byte ERROR = 5;
   public static final byte CONFIGURATION_ECHO_TYPE = 64;
   public static final byte CONFIGURATION_COMPRESSION_TYPE = 65;
   public static final byte CONFIGURATION_CHUNK_SIZE_TYPE = 66;
   public static final int DEFAULT_CHUNK_SIZE = 262144;
   public static final int MIN_CHUNK_SIZE = 8192;
   public static final int MAX_CHUNK_SIZE = 524288;

   public static String getFingerprint(X509Certificate cert) throws CertificateEncodingException {
      byte[] certificate = cert.getEncoded();

      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] fingerprint = digest.digest(certificate);
         return HexFormat.of().formatHex(fingerprint);
      } catch (NoSuchAlgorithmException var4) {
         throw new RuntimeException(var4);
      }
   }

   public static KeyPair generateKeyPair() throws Exception {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      return keyPairGenerator.generateKeyPair();
   }

   public static X509Certificate selfSign(KeyPair keyPair) throws Exception {
      Provider bcProvider = new BouncyCastleProvider();
      Security.addProvider(bcProvider);
      long now = System.currentTimeMillis();
      Date startDate = new Date(now);
      X500Name dnName = new X500Name("CN=AutoModpack Self Signed Certificate");
      BigInteger certSerialNumber = new BigInteger(Long.toString(now));
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(startDate);
      calendar.add(1, 1);
      Date endDate = calendar.getTime();
      String signatureAlgorithm = "SHA256WithRSA";
      ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());
      JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic());
      return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
   }

   public static void saveCertificate(X509Certificate cert, Path path) throws Exception {
      String certPem = "-----BEGIN CERTIFICATE-----\n" + formatBase64(Base64.getEncoder().encodeToString(cert.getEncoded())) + "-----END CERTIFICATE-----";
      CustomFileUtils.setupFilePaths(path);
      Files.writeString(path, certPem);
   }

   public static X509Certificate loadCertificate(Path path) throws Exception {
      if (!Files.exists(path)) {
         return null;
      } else {
         X509Certificate var3;
         try (InputStream in = new LockFreeInputStream(path)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            var3 = (X509Certificate)cf.generateCertificate(in);
         }

         return var3;
      }
   }

   public static void savePrivateKey(PrivateKey key, Path path) throws Exception {
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key.getEncoded());
      String keyPem = "-----BEGIN PRIVATE KEY-----\n" + formatBase64(Base64.getEncoder().encodeToString(keySpec.getEncoded())) + "-----END PRIVATE KEY-----";
      CustomFileUtils.setupFilePaths(path);
      Files.writeString(path, keyPem);
   }

   private static String formatBase64(String base64) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < base64.length(); i += 64) {
         sb.append(base64, i, Math.min(i + 64, base64.length()));
         sb.append("\n");
      }

      return sb.toString();
   }
}
