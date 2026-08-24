package pl.skidam.automodpack_core.protocol.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.stream.ChunkedNioStream;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.auth.Secrets;
import pl.skidam.automodpack_core.config.Jsons;
import pl.skidam.automodpack_core.modpack.ModpackContent;
import pl.skidam.automodpack_core.protocol.netty.NettyServer;
import pl.skidam.automodpack_core.protocol.netty.message.ProtocolMessage;
import pl.skidam.automodpack_core.protocol.netty.message.request.EchoMessage;
import pl.skidam.automodpack_core.protocol.netty.message.request.FileRequestMessage;
import pl.skidam.automodpack_core.protocol.netty.message.request.RefreshRequestMessage;
import pl.skidam.automodpack_core.utils.LockFreeInputStream;

public class ServerMessageHandler extends SimpleChannelInboundHandler<ProtocolMessage> {
   private final Map<byte[], String> secretLookup = new HashMap<>();
   private byte protocolVersion;
   private int chunkSize;

   public void handlerRemoved(ChannelHandlerContext ctx) {
      GlobalVariables.hostServer.removeConnection(ctx.channel());
   }

   protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) throws Exception {
      byte clientProtocolVersion = msg.getVersion();
      SocketAddress address = (SocketAddress)ctx.channel().attr(NettyServer.REAL_REMOTE_ADDR).get();
      if (!this.validateSecret(ctx, address, msg.getSecret())) {
         this.sendError(ctx, clientProtocolVersion, "Authentication failed");
      } else {
         this.protocolVersion = (Byte)ctx.pipeline().channel().attr(NettyServer.PROTOCOL_VERSION).get();
         this.chunkSize = (Integer)ctx.pipeline().channel().attr(NettyServer.CHUNK_SIZE).get();
         switch (msg.getType()) {
            case 0:
               EchoMessage echoMsg = (EchoMessage)msg;
               ByteBuf echoBuf = Unpooled.buffer(2 + msg.getSecret().length + echoMsg.getData().length);
               echoBuf.writeByte(clientProtocolVersion);
               echoBuf.writeByte(0);
               echoBuf.writeBytes(echoMsg.getSecret());
               echoBuf.writeBytes(echoMsg.getData());
               ctx.writeAndFlush(echoBuf);
               ctx.channel().close();
               break;
            case 1:
               FileRequestMessage fileRequest = (FileRequestMessage)msg;
               this.sendFile(ctx, fileRequest.getFileHash());
               break;
            case 2:
            default:
               this.sendError(ctx, clientProtocolVersion, "Unknown message type");
               break;
            case 3:
               RefreshRequestMessage refreshRequest = (RefreshRequestMessage)msg;
               this.refreshModpackFiles(ctx, refreshRequest.getFileHashesList());
         }
      }
   }

   private void refreshModpackFiles(ChannelHandlerContext context, byte[][] FileHashesList) throws IOException {
      Set<String> hashes = new HashSet<>();

      for (byte[] hash : FileHashesList) {
         hashes.add(new String(hash));
      }

      GlobalVariables.LOGGER.info("Received refresh request for files of hashes: {}", hashes);
      Set<CompletableFuture<Void>> creationFutures = new HashSet<>();
      Set<ModpackContent> modpacks = new HashSet<>();

      for (String hash : hashes) {
         Optional<Path> optionalPath = this.resolvePath(hash);
         if (!optionalPath.isEmpty()) {
            Path path = optionalPath.get();
            ModpackContent modpack = null;

            for (ModpackContent content : GlobalVariables.modpackExecutor.modpacks.values()) {
               if (content.pathsMap.getMap().containsKey(hash)) {
                  modpack = content;
                  break;
               }
            }

            if (modpack != null) {
               modpacks.add(modpack);
               creationFutures.add(modpack.replaceAsync(path));
            }
         }
      }

      creationFutures.forEach(CompletableFuture::join);
      modpacks.forEach(modpackContent -> {
         Optional<Jsons.ModpackContentFields> optionalPreviousModpackContent = modpackContent.getPreviousContent();
         if (optionalPreviousModpackContent.isEmpty()) {
            GlobalVariables.LOGGER.error("Could not find previous modpack content for modpack while refreshing it: {}", modpackContent.getModpackName());
         } else {
            Jsons.ModpackContentFields previousModpackContent = optionalPreviousModpackContent.get();
            modpackContent.saveModpackContent(previousModpackContent.nonModpackFilesToDelete);
         }
      });
      GlobalVariables.LOGGER.info("Sending new modpack-content.json");
      this.sendFile(context, new byte[0]);
   }

   private boolean validateSecret(ChannelHandlerContext ctx, SocketAddress address, byte[] secret) {
      String decodedSecret = this.secretLookup.get(secret);
      boolean addConnection = false;
      if (decodedSecret == null) {
         decodedSecret = Base64.getUrlEncoder().withoutPadding().encodeToString(secret);
         addConnection = true;
         this.secretLookup.put(secret, decodedSecret);
      }

      boolean valid = Secrets.isSecretValid(decodedSecret, address);
      if (addConnection && valid) {
         GlobalVariables.hostServer.addConnection(ctx.channel(), decodedSecret);
      }

      return valid;
   }

   private void sendFile(ChannelHandlerContext ctx, byte[] bsha1) throws IOException {
      String sha1 = new String(bsha1, CharsetUtil.UTF_8);
      Optional<Path> optionalPath = this.resolvePath(sha1);
      if (!optionalPath.isEmpty() && Files.exists(optionalPath.get())) {
         Path path = optionalPath.get();
         long fileSize = Files.size(path);
         ByteBuf responseHeader = Unpooled.buffer(10);
         responseHeader.writeByte(this.protocolVersion);
         responseHeader.writeByte(2);
         responseHeader.writeLong(fileSize);
         ctx.writeAndFlush(responseHeader);
         if (fileSize == 0L) {
            this.sendEOT(ctx);
         } else {
            ReadableByteChannel channel = null;

            try {
               channel = LockFreeInputStream.openChannel(path);
               ChunkedNioStream chunkedStream = new ChunkedNioStream(channel, this.chunkSize);
               ctx.writeAndFlush(chunkedStream).addListener((ChannelFutureListener)future -> {
                  if (future.isSuccess()) {
                     this.sendEOT(ctx);
                  } else {
                     Throwable cause = future.cause();
                     this.sendError(ctx, this.protocolVersion, "File transfer error: " + (cause != null ? cause.getMessage() : "Unknown"));
                  }
               });
            } catch (Exception var13) {
               if (channel != null) {
                  try {
                     channel.close();
                  } catch (IOException var12) {
                  }
               }

               this.sendError(ctx, this.protocolVersion, "File transfer error: " + var13.getMessage());
            }
         }
      } else {
         this.sendError(ctx, this.protocolVersion, "File not found");
      }
   }

   public Optional<Path> resolvePath(String sha1) {
      return sha1.isBlank() ? Optional.of(GlobalVariables.hostModpackContentFile) : GlobalVariables.hostServer.getPath(sha1);
   }

   private void sendError(ChannelHandlerContext ctx, byte version, String errorMessage) {
      byte[] errMsgBytes = errorMessage.getBytes(CharsetUtil.UTF_8);
      ByteBuf errorBuf = Unpooled.buffer(6 + errMsgBytes.length);
      errorBuf.writeByte(version);
      errorBuf.writeByte(5);
      errorBuf.writeInt(errMsgBytes.length);
      errorBuf.writeBytes(errMsgBytes);
      ctx.writeAndFlush(errorBuf);
      ctx.channel().close();
   }

   private void sendEOT(ChannelHandlerContext ctx) {
      ByteBuf eot = Unpooled.buffer(2);
      eot.writeByte(this.protocolVersion);
      eot.writeByte(4);
      ctx.writeAndFlush(eot);
   }
}
