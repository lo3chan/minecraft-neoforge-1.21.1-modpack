package pl.skidam.automodpack_core.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import pl.skidam.automodpack_core.config.Jsons;

class PreValidationConnection {
   private final SSLSocket socket;

   public PreValidationConnection(InetSocketAddress resolvedHostAddress, Jsons.ModpackAddresses modpackAddresses, SSLContext sslContext) throws IOException {
      Socket plainSocket = new Socket();
      plainSocket.connect(resolvedHostAddress, 10000);
      plainSocket.setSoTimeout(10000);
      if (modpackAddresses.requiresMagic) {
         try {
            DataOutputStream plainOut = new DataOutputStream(new BufferedOutputStream(plainSocket.getOutputStream()));
            DataInputStream plainIn = new DataInputStream(new BufferedInputStream(plainSocket.getInputStream()));
            byte[] hostBytes = resolvedHostAddress.getHostString().getBytes(StandardCharsets.UTF_8);
            plainOut.writeInt(1095585096);
            plainOut.writeShort(hostBytes.length);
            plainOut.write(hostBytes);
            plainOut.flush();
            int handshakeResponse = plainIn.readInt();
            if (handshakeResponse != 1095585611) {
               throw new IOException("Invalid response from server: " + handshakeResponse);
            }
         } catch (IOException var13) {
            try {
               plainSocket.close();
            } catch (IOException var11) {
            }

            throw var13;
         }
      }

      SSLSocketFactory factory = sslContext.getSocketFactory();
      SSLSocket sslSocket = (SSLSocket)factory.createSocket(plainSocket, resolvedHostAddress.getHostString(), resolvedHostAddress.getPort(), true);
      sslSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
      sslSocket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256"});
      SSLParameters sslParameters = new SSLParameters();
      sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
      sslSocket.setSSLParameters(sslParameters);

      try {
         sslSocket.startHandshake();
      } catch (IOException var12) {
         try {
            sslSocket.close();
         } catch (IOException var10) {
         }

         throw var12;
      }

      this.socket = sslSocket;
   }

   protected SSLSocket getSocket() {
      return this.socket;
   }
}
