package pl.skidam.automodpack_core.protocol;

import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

public class CustomizableTrustManager extends X509ExtendedTrustManager {
   private final X509ExtendedTrustManager defaultTrustManager = createTrustManager(null);
   private final X509ExtendedTrustManager customTrustManager;
   private final Consumer<X509Certificate[]> onValidating;
   private final X509Certificate[] cachedAcceptedIssuers;

   public CustomizableTrustManager(KeyStore customStore, Consumer<X509Certificate[]> onValidating) throws KeyStoreException {
      this.customTrustManager = customStore != null && customStore.size() > 0 ? createTrustManager(customStore) : null;
      this.onValidating = onValidating;
      List<X509Certificate> issuers = new ArrayList<>(Arrays.asList(this.defaultTrustManager.getAcceptedIssuers()));
      if (this.customTrustManager != null) {
         issuers.addAll(Arrays.asList(this.customTrustManager.getAcceptedIssuers()));
      }

      this.cachedAcceptedIssuers = issuers.toArray(new X509Certificate[0]);
   }

   private static X509ExtendedTrustManager createTrustManager(KeyStore keyStore) throws KeyStoreException {
      try {
         TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
         tmf.init(keyStore);

         for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509ExtendedTrustManager) {
               return (X509ExtendedTrustManager)tm;
            }
         }

         throw new IllegalStateException("No X509ExtendedTrustManager found");
      } catch (NoSuchAlgorithmException var6) {
         throw new RuntimeException("Default algorithm unavailable", var6);
      }
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
      return this.cachedAcceptedIssuers;
   }

   @Override
   public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      if (this.onValidating != null) {
         this.onValidating.accept(chain);
      }

      try {
         this.defaultTrustManager.checkServerTrusted(chain, authType);
      } catch (CertificateException var4) {
         if (this.customTrustManager == null) {
            throw var4;
         }

         this.customTrustManager.checkServerTrusted(chain, authType);
      }
   }

   @Override
   public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
      if (this.onValidating != null) {
         this.onValidating.accept(chain);
      }

      try {
         this.defaultTrustManager.checkServerTrusted(chain, authType, socket);
      } catch (CertificateException var5) {
         if (this.customTrustManager == null) {
            throw var5;
         }

         this.customTrustManager.checkServerTrusted(chain, authType);
      }
   }

   @Override
   public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
      if (this.onValidating != null) {
         this.onValidating.accept(chain);
      }

      try {
         this.defaultTrustManager.checkServerTrusted(chain, authType, engine);
      } catch (CertificateException var5) {
         if (this.customTrustManager == null) {
            throw var5;
         }

         this.customTrustManager.checkServerTrusted(chain, authType);
      }
   }

   @Override
   public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      if (this.onValidating != null) {
         this.onValidating.accept(chain);
      }

      try {
         this.defaultTrustManager.checkClientTrusted(chain, authType);
      } catch (CertificateException var4) {
         if (this.customTrustManager == null) {
            throw var4;
         }

         this.customTrustManager.checkClientTrusted(chain, authType);
      }
   }

   @Override
   public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
      if (this.onValidating != null) {
         this.onValidating.accept(chain);
      }

      try {
         this.defaultTrustManager.checkClientTrusted(chain, authType, socket);
      } catch (CertificateException var5) {
         if (this.customTrustManager == null) {
            throw var5;
         }

         this.customTrustManager.checkClientTrusted(chain, authType);
      }
   }

   @Override
   public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
      if (this.onValidating != null) {
         this.onValidating.accept(chain);
      }

      try {
         this.defaultTrustManager.checkClientTrusted(chain, authType, engine);
      } catch (CertificateException var5) {
         if (this.customTrustManager == null) {
            throw var5;
         }

         this.customTrustManager.checkClientTrusted(chain, authType);
      }
   }
}
