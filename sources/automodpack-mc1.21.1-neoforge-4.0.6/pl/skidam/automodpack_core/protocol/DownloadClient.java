package pl.skidam.automodpack_core.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.config.Jsons;

public class DownloadClient implements AutoCloseable {
   private final List<Connection> connections = new ArrayList<>();
   private InetSocketAddress address = null;

   public DownloadClient(Jsons.ModpackAddresses modpackAddresses, byte[] secretBytes, int poolSize, Function<X509Certificate, Boolean> trustedByUserCallback) throws IOException {
      if (poolSize < 1) {
         throw new IllegalArgumentException("Pool size must be greater than 0");
      } else {
         KeyStore keyStore = this.loadDefaultKeyStore();
         DownloadClient.InitialConnectionResult probe = this.establishProbeConnection(modpackAddresses, keyStore, trustedByUserCallback);
         if (probe.connection.getSocket() != null && !probe.connection.getSocket().isClosed()) {
            if (secretBytes == null) {
               probe.connection().getSocket().close();
            } else {
               this.connections.add(new Connection(probe.connection, secretBytes));
            }
         }

         if (secretBytes != null) {
            int remainingNeeded = poolSize - this.connections.size();
            if (remainingNeeded >= 1) {
               List<Connection> newConnections = IntStream.range(0, remainingNeeded).parallel().mapToObj(i -> {
                  try {
                     return new Connection(this.getPreValidationConnection(modpackAddresses, probe.sslContext), secretBytes);
                  } catch (IOException var6x) {
                     throw new CompletionException(var6x);
                  }
               }).toList();
               this.connections.addAll(newConnections);
               GlobalVariables.LOGGER.info("Download client initialized with {} connections to {}", this.connections.size(), modpackAddresses.hostAddress);
            }
         }
      }
   }

   private DownloadClient.InitialConnectionResult establishProbeConnection(
      Jsons.ModpackAddresses addresses, KeyStore keyStore, Function<X509Certificate, Boolean> trustCallback
   ) throws IOException {
      AtomicReference<X509Certificate[]> capturedChain = new AtomicReference<>();
      SSLContext context = this.createSSLContext(keyStore, capturedChain::set);

      try {
         PreValidationConnection conn = this.getPreValidationConnection(addresses, context);
         return new DownloadClient.InitialConnectionResult(conn, context);
      } catch (IOException var7) {
         return this.recoverProbeConnection(var7, addresses, keyStore, trustCallback, capturedChain.get());
      }
   }

   private DownloadClient.InitialConnectionResult recoverProbeConnection(
      IOException originalError, Jsons.ModpackAddresses addresses, KeyStore keyStore, Function<X509Certificate, Boolean> trustCallback, X509Certificate[] chain
   ) throws IOException {
      if (chain != null && chain.length != 0 && trustCallback != null) {
         boolean isTrusted = trustCallback.apply(chain[0]);
         if (!isTrusted) {
            throw new IOException("User rejected the certificate.", originalError);
         } else {
            try {
               keyStore.setCertificateEntry(addresses.hostAddress.getHostString(), chain[0]);
               SSLContext trustedContext = this.createSSLContext(keyStore, null);
               PreValidationConnection retryConn = this.getPreValidationConnection(addresses, trustedContext);
               return new DownloadClient.InitialConnectionResult(retryConn, trustedContext);
            } catch (KeyStoreException var9) {
               throw new IOException("Failed to update KeyStore with trusted certificate", var9);
            }
         }
      } else {
         throw originalError;
      }
   }

   private KeyStore loadDefaultKeyStore() {
      try {
         KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
         keyStore.load(null);
         return keyStore;
      } catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException var2) {
         throw new RuntimeException("Failed to initialize KeyStore", var2);
      }
   }

   private PreValidationConnection getPreValidationConnection(Jsons.ModpackAddresses modpackAddresses, SSLContext sharedContext) throws IOException {
      String hostName = modpackAddresses.hostAddress.getHostString();
      if (this.address == null) {
         this.address = new InetSocketAddress(hostName, modpackAddresses.hostAddress.getPort());
         if (this.address.isUnresolved()) {
            throw new IOException("Failed to resolve host address: " + hostName);
         }
      }

      return new PreValidationConnection(this.address, modpackAddresses, sharedContext);
   }

   private SSLContext createSSLContext(KeyStore trustedCertificates, Consumer<X509Certificate[]> onValidating) {
      try {
         SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
         X509ExtendedTrustManager trustManager = new CustomizableTrustManager(trustedCertificates, onValidating);
         sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
         SSLSessionContext sessionContext = sslContext.getClientSessionContext();
         sessionContext.setSessionTimeout(1800);
         sessionContext.setSessionCacheSize(20);
         return sslContext;
      } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException var6) {
         throw new RuntimeException("Failed to initialize SSLContext", var6);
      }
   }

   public static DownloadClient tryCreate(
      Jsons.ModpackAddresses modpackAddresses, byte[] secretBytes, int poolSize, Function<X509Certificate, Boolean> trustedByUserCallback
   ) {
      try {
         return new DownloadClient(modpackAddresses, secretBytes, poolSize, trustedByUserCallback);
      } catch (IOException var5) {
         GlobalVariables.LOGGER.error("Failed to create download client: {}", var5.getMessage());
         GlobalVariables.LOGGER.debug(var5);
         return null;
      }
   }

   private synchronized Connection getFreeConnection() {
      Iterator<Connection> iterator = this.connections.iterator();

      while (iterator.hasNext()) {
         Connection conn = iterator.next();
         if (!conn.isBusy()) {
            if (!conn.isActive()) {
               iterator.remove();
               return this.getFreeConnection();
            }

            conn.setBusy(true);
            return conn;
         }
      }

      throw new IllegalStateException("No available connections");
   }

   public CompletableFuture<Path> downloadFile(byte[] fileHash, Path destination, IntConsumer chunkCallback) {
      return this.getFreeConnection().sendDownloadFile(fileHash, destination, chunkCallback);
   }

   public CompletableFuture<Path> requestRefresh(byte[][] fileHashes, Path destination) {
      return this.getFreeConnection().sendRefreshRequest(fileHashes, destination);
   }

   @Override
   public void close() {
      for (Connection conn : this.connections) {
         conn.close();
      }

      this.connections.clear();
   }

   private record InitialConnectionResult(PreValidationConnection connection, SSLContext sslContext) {
   }
}
