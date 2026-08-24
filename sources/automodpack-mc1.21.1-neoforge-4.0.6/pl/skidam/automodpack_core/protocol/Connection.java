package pl.skidam.automodpack_core.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.protocol.compression.CompressionCodec;
import pl.skidam.automodpack_core.protocol.compression.CompressionFactory;
import pl.skidam.automodpack_core.utils.PlatformUtils;

class Connection implements AutoCloseable {
   private byte protocolVersion = 1;
   private byte compressionType = 1;
   private int chunkSize = 262144;
   private final byte[] secretBytes;
   private final SSLSocket socket;
   private final DataInputStream in;
   private final DataOutputStream out;
   private final ExecutorService executor = Executors.newSingleThreadExecutor();
   private final AtomicBoolean busy = new AtomicBoolean(false);

   public Connection(PreValidationConnection preValidationConnection, byte[] secretBytes) throws IOException {
      if (preValidationConnection.getSocket() != null && !preValidationConnection.getSocket().isClosed()) {
         this.socket = preValidationConnection.getSocket();
         this.secretBytes = secretBytes;
         this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
         this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));

         try {
            if (!PlatformUtils.canUseZstd()) {
               this.compressionType = 2;
            }

            this.compressionType = this.sendCompressionConfig(this.compressionType);
            this.chunkSize = this.sendChunkSizeConfig(262144);
            this.sendEchoConfig();
         } catch (IOException var4) {
            GlobalVariables.LOGGER.error("Failed to configure connection", var4);
            throw var4;
         }
      } else {
         throw new SSLHandshakeException("Server certificate invalid, connection closed");
      }
   }

   public boolean isActive() {
      return !this.socket.isClosed();
   }

   public boolean isBusy() {
      return this.busy.get();
   }

   public void setBusy(boolean value) {
      this.busy.set(value);
   }

   private CompressionCodec getCompressionCodec() {
      return CompressionFactory.getCodec(this.compressionType);
   }

   public CompletableFuture<Path> sendDownloadFile(byte[] fileHash, Path destination, IntConsumer chunkCallback) {
      if (destination == null) {
         throw new IllegalArgumentException("Destination cannot be null");
      } else {
         return CompletableFuture.supplyAsync(() -> {
            Exception exception = null;

            Path var7;
            try {
               ByteArrayOutputStream baos = new ByteArrayOutputStream(64 + fileHash.length);
               DataOutputStream dos = new DataOutputStream(baos);
               dos.writeByte(this.protocolVersion);
               dos.writeByte(1);
               dos.write(this.secretBytes);
               dos.writeInt(fileHash.length);
               dos.write(fileHash);
               this.writeProtocolMessage(baos.toByteArray());
               var7 = this.readFileResponse(destination, chunkCallback);
            } catch (Exception var11) {
               exception = var11;
               throw new CompletionException(var11);
            } finally {
               this.finalBlock(exception);
            }

            return var7;
         }, this.executor);
      }
   }

   public CompletableFuture<Path> sendRefreshRequest(byte[][] fileHashes, Path destination) {
      return CompletableFuture.supplyAsync(() -> {
         Exception exception = null;

         Path var15;
         try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeByte(this.protocolVersion);
            dos.writeByte(3);
            dos.write(this.secretBytes);
            dos.writeInt(fileHashes.length);
            if (fileHashes.length > 0) {
               dos.writeInt(fileHashes[0].length);

               for (byte[] hash : fileHashes) {
                  dos.write(hash);
               }
            }

            this.writeProtocolMessage(baos.toByteArray());
            var15 = this.readFileResponse(destination, null);
         } catch (Exception var13) {
            exception = var13;
            throw new CompletionException(var13);
         } finally {
            this.finalBlock(exception);
         }

         return var15;
      }, this.executor);
   }

   private void finalBlock(Exception exception) {
      try {
         int available;
         try {
            while ((available = this.in.available()) > 0) {
               this.in.skipBytes(available);
            }
         } catch (IOException var6) {
            if (exception == null) {
               throw new CompletionException(var6);
            }
         }
      } finally {
         if (exception == null) {
            this.setBusy(false);
         }
      }
   }

   private void writeProtocolMessage(byte[] payload) throws IOException {
      CompressionCodec codec = this.getCompressionCodec();
      int offset = 0;

      while (offset < payload.length) {
         int bytesToSend = Math.min(payload.length - offset, this.chunkSize);
         byte[] chunk = new byte[bytesToSend];
         System.arraycopy(payload, offset, chunk, 0, bytesToSend);
         byte[] compressedChunk = codec.compress(chunk);
         this.out.writeInt(compressedChunk.length);
         this.out.writeInt(chunk.length);
         this.out.write(compressedChunk);
         offset += bytesToSend;
      }

      this.out.flush();
   }

   private byte[] readProtocolMessageFrame() throws IOException {
      int compressedLength = this.in.readInt();
      int originalLength = this.in.readInt();
      int maxAllowedSize = this.chunkSize + 8192;
      if (compressedLength < 0 || compressedLength > maxAllowedSize) {
         throw new IOException("Frame compressed length (" + compressedLength + ") exceeds limit (" + maxAllowedSize + ")");
      } else if (originalLength >= 0 && originalLength <= this.chunkSize) {
         byte[] compressed = new byte[compressedLength];
         this.in.readFully(compressed);
         return this.getCompressionCodec().decompress(compressed, originalLength);
      } else {
         throw new IOException("Frame original length (" + originalLength + ") exceeds chunk size (" + this.chunkSize + ")");
      }
   }

   private Path readFileResponse(Path destination, IntConsumer chunkCallback) throws IOException {
      Path var22;
      try (DataInputStream headerIn = new DataInputStream(new ByteArrayInputStream(this.readProtocolMessageFrame()))) {
         byte version = headerIn.readByte();
         byte messageType = headerIn.readByte();
         if (messageType == 5) {
            int errLen = headerIn.readInt();
            byte[] errBytes = new byte[errLen];
            headerIn.readFully(errBytes);
            throw new IOException("Server error: " + new String(errBytes, StandardCharsets.UTF_8));
         }

         if (messageType == 4) {
            return destination;
         }

         if (messageType != 2) {
            throw new IOException("Unexpected message type: " + messageType);
         }

         long expectedFileSize = headerIn.readLong();
         long receivedBytes = 0L;

         try (OutputStream fos = new BufferedOutputStream(
               Files.newOutputStream(destination, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
            )) {
            while (receivedBytes < expectedFileSize) {
               byte[] dataFrame = this.readProtocolMessageFrame();
               int toWrite = Math.min(dataFrame.length, (int)(expectedFileSize - receivedBytes));
               fos.write(dataFrame, 0, toWrite);
               receivedBytes += toWrite;
               if (chunkCallback != null) {
                  chunkCallback.accept(toWrite);
               }
            }
         }

         try (DataInputStream var21 = new DataInputStream(new ByteArrayInputStream(this.readProtocolMessageFrame()))) {
            byte ver = var21.readByte();
            byte eotType = var21.readByte();
            if (ver != version || eotType != 4) {
               throw new IOException("Invalid EOT frame");
            }
         }

         var22 = destination;
      }

      return var22;
   }

   private byte sendCompressionConfig(byte desiredCompression) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeByte(this.protocolVersion);
      dos.writeByte(65);
      dos.writeByte(desiredCompression);
      this.out.write(baos.toByteArray());
      this.out.flush();
      byte version = this.in.readByte();
      byte type = this.in.readByte();
      if (type != 65) {
         throw new IOException("Unexpected response: " + type);
      } else {
         byte negotiated = this.in.readByte();
         if (negotiated != 0 && negotiated != 1 && negotiated != 2) {
            throw new IOException("Unsupported compression: " + negotiated);
         } else {
            return negotiated;
         }
      }
   }

   private int sendChunkSizeConfig(int desiredChunkSize) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeByte(this.protocolVersion);
      dos.writeByte(66);
      dos.writeInt(desiredChunkSize);
      this.out.write(baos.toByteArray());
      this.out.flush();
      byte version = this.in.readByte();
      byte type = this.in.readByte();
      if (type != 66) {
         throw new IOException("Unexpected response: " + type);
      } else {
         int negotiated = this.in.readInt();
         if (negotiated >= 8192 && negotiated <= 524288) {
            return negotiated;
         } else {
            throw new IOException("Chunk size out of bounds: " + negotiated);
         }
      }
   }

   private void sendEchoConfig() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeByte(this.protocolVersion);
      dos.writeByte(64);
      this.out.write(baos.toByteArray());
      this.out.flush();
   }

   @Override
   public void close() {
      try {
         this.socket.close();
      } catch (Exception var2) {
      }

      this.executor.shutdownNow();
   }
}
