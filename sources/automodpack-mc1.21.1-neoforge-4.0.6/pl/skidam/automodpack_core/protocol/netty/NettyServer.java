package pl.skidam.automodpack_core.protocol.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.config.ConfigTools;
import pl.skidam.automodpack_core.protocol.NetUtils;
import pl.skidam.automodpack_core.protocol.netty.handler.ProtocolServerHandler;
import pl.skidam.automodpack_core.utils.AddressHelpers;
import pl.skidam.automodpack_core.utils.CustomThreadFactoryBuilder;
import pl.skidam.automodpack_core.utils.ObservableMap;

public class NettyServer {
   public static final AttributeKey<SocketAddress> REAL_REMOTE_ADDR = AttributeKey.valueOf("REAL_REMOTE_ADDR");
   public static final AttributeKey<Byte> COMPRESSION_TYPE = AttributeKey.valueOf("COMPRESSION_TYPE");
   public static final AttributeKey<Integer> CHUNK_SIZE = AttributeKey.valueOf("CHUNK_SIZE");
   public static final AttributeKey<Byte> PROTOCOL_VERSION = AttributeKey.valueOf("PROTOCOL_VERSION");
   private final Map<Channel, String> connections = Collections.synchronizedMap(new HashMap<>());
   private final Map<String, Path> paths = Collections.synchronizedMap(new HashMap<>());
   private MultithreadEventLoopGroup eventLoopGroup;
   private ChannelFuture serverChannel;
   private Boolean shouldHost = false;
   private String certificateFingerprint;
   private SslContext sslCtx;

   public void addConnection(Channel channel, String secret) {
      synchronized (this.connections) {
         this.connections.put(channel, secret);
      }
   }

   public void removeConnection(Channel channel) {
      synchronized (this.connections) {
         this.connections.remove(channel);
      }
   }

   public Map<Channel, String> getConnections() {
      return this.connections;
   }

   public String getCertificateFingerprint() {
      return this.certificateFingerprint;
   }

   public void addPaths(ObservableMap<String, Path> paths) {
      this.paths.putAll(paths.getMap());
      paths.addOnPutCallback(this.paths::put);
      paths.addOnRemoveCallback(this.paths::remove);
   }

   public void removePaths(ObservableMap<String, Path> paths) {
      paths.getMap().forEach(this.paths::remove);
   }

   public Optional<Path> getPath(String hash) {
      return Optional.ofNullable(this.paths.get(hash));
   }

   public Optional<ChannelFuture> start() {
      if (!GlobalVariables.serverConfig.modpackHost) {
         GlobalVariables.LOGGER.warn("Modpack hosting is disabled in config");
         return Optional.empty();
      } else {
         try {
            if (GlobalVariables.serverConfig.disableInternalTLS && GlobalVariables.serverConfig.bindPort != -1) {
               GlobalVariables.LOGGER
                  .warn("Internal TLS is disabled. Clients will not be able to connect directly; you must use e.g. a reverse proxy with TLS.");
            } else {
               if (GlobalVariables.serverConfig.disableInternalTLS) {
                  GlobalVariables.LOGGER
                     .error(
                        "Internal TLS cannot be disabled. You have to bind modpack host on a separate port, preferably also on a loopback address or atleast some private one."
                     );
               }

               if (!Files.exists(GlobalVariables.serverCertFile) || !Files.exists(GlobalVariables.serverPrivateKeyFile)) {
                  KeyPair keyPair = NetUtils.generateKeyPair();
                  X509Certificate cert = NetUtils.selfSign(keyPair);
                  NetUtils.saveCertificate(cert, GlobalVariables.serverCertFile);
                  NetUtils.savePrivateKey(keyPair.getPrivate(), GlobalVariables.serverPrivateKeyFile);
               }

               X509Certificate cert = NetUtils.loadCertificate(GlobalVariables.serverCertFile);
               if (cert == null) {
                  throw new IllegalStateException("Server certificate couldn't be loaded");
               }

               this.sslCtx = SslContextBuilder.forServer(GlobalVariables.serverCertFile.toFile(), GlobalVariables.serverPrivateKeyFile.toFile())
                  .sslProvider(SslProvider.JDK)
                  .protocols(new String[]{"TLSv1.3"})
                  .ciphers(Arrays.asList("TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256"))
                  .sessionTimeout(1800L)
                  .build();
               this.certificateFingerprint = NetUtils.getFingerprint(cert);
               if (this.certificateFingerprint != null) {
                  GlobalVariables.LOGGER.warn("Certificate fingerprint: {}", this.certificateFingerprint);
               }
            }

            if (!this.canStart()) {
               new TrafficShaper(null);
               return Optional.empty();
            }

            String address = GlobalVariables.serverConfig.bindAddress;
            int port = GlobalVariables.serverConfig.bindPort;
            InetSocketAddress bindAddress = null;
            if (port != -1) {
               if (address != null && !address.isBlank()) {
                  bindAddress = new InetSocketAddress(address, port);
               } else {
                  bindAddress = new InetSocketAddress(port);
               }
            }

            GlobalVariables.LOGGER.info("Starting modpack host server on {}", bindAddress);
            Class<? extends ServerChannel> socketChannelClass;
            if (Epoll.isAvailable()) {
               socketChannelClass = EpollServerSocketChannel.class;
               this.eventLoopGroup = new EpollEventLoopGroup(
                  new CustomThreadFactoryBuilder().setNameFormat("AutoModpack Epoll Server IO #%d").setDaemon(true).build()
               );
            } else {
               socketChannelClass = NioServerSocketChannel.class;
               this.eventLoopGroup = new NioEventLoopGroup(new CustomThreadFactoryBuilder().setNameFormat("AutoModpack Server IO #%d").setDaemon(true).build());
            }

            new TrafficShaper(this.eventLoopGroup);
            this.serverChannel = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(socketChannelClass))
                  .childOption(ChannelOption.TCP_NODELAY, true)
                  .childHandler(new ChannelInitializer<SocketChannel>() {
                     protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("automodpack", new ProtocolServerHandler(NettyServer.this.sslCtx));
                     }
                  })
                  .group(this.eventLoopGroup)
                  .localAddress(bindAddress))
               .bind()
               .syncUninterruptibly();
         } catch (Exception var5) {
            GlobalVariables.LOGGER.error("Failed to start Netty server", var5);
            return Optional.empty();
         }

         return Optional.ofNullable(this.serverChannel);
      }
   }

   public boolean shouldHost() {
      return this.shouldHost;
   }

   public boolean stop() {
      try {
         if (this.serverChannel != null) {
            this.serverChannel.channel().close().sync();
            this.serverChannel = null;
         }

         this.shouldHost = false;
         TrafficShaper.close();
         if (this.eventLoopGroup != null) {
            this.eventLoopGroup.shutdownGracefully().sync();
         }

         return true;
      } catch (InterruptedException var2) {
         GlobalVariables.LOGGER.error("Interrupted server channel", var2);
         return false;
      }
   }

   public boolean isRunning() {
      return this.serverChannel == null ? this.shouldHost : this.serverChannel.channel().isOpen();
   }

   public SslContext getSslCtx() {
      return this.sslCtx;
   }

   private boolean canStart() {
      if (this.isRunning() || !GlobalVariables.serverConfig.modpackHost) {
         return false;
      } else if (this.paths.isEmpty()) {
         GlobalVariables.LOGGER.warn("No file to host. Can't start modpack host server.");
         return false;
      } else {
         if (GlobalVariables.serverConfig.updateIpsOnEveryStart) {
            String publicIp = AddressHelpers.getPublicIp();
            if (publicIp != null) {
               GlobalVariables.serverConfig.addressToSend = publicIp;
               GlobalVariables.LOGGER.warn("Setting Host IP to {}", GlobalVariables.serverConfig.addressToSend);
            } else {
               GlobalVariables.LOGGER.error("Couldn't get public IP, please change it manually! ");
            }

            try {
               ConfigTools.save(GlobalVariables.serverConfigFile, GlobalVariables.serverConfig);
            } catch (Exception var3) {
               var3.printStackTrace();
            }
         }

         this.shouldHost = true;
         if (GlobalVariables.serverConfig.bindPort == -1) {
            GlobalVariables.LOGGER.info("Hosting modpack on Minecraft port");
            return false;
         } else {
            return true;
         }
      }
   }
}
