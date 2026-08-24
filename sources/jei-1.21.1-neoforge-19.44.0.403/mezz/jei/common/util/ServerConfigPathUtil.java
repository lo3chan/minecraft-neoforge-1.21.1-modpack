package mezz.jei.common.util;

import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import java.net.IDN;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import mezz.jei.common.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;

public final class ServerConfigPathUtil {
   private static final Path worldDirPath = Path.of("world");
   private static final Path serverDirPath = worldDirPath.resolve("server");

   private ServerConfigPathUtil() {
   }

   public static Optional<Path> getWorldPath(Path basePath) {
      Minecraft minecraft = Minecraft.getInstance();
      return Optional.ofNullable(minecraft.getConnection())
         .flatMap(
            clientPacketListener -> {
               Connection connection = clientPacketListener.getConnection();
               return connection.isMemoryConnection()
                  ? Optional.ofNullable(minecraft.getSingleplayerServer())
                     .flatMap(
                        minecraftServer -> Services.PLATFORM
                           .getWorldHelper()
                           .getLevelId(minecraftServer)
                           .map(PathUtil::sanitizePathName)
                           .map(name -> worldDirPath.resolve("local").resolve(name))
                     )
                  : Optional.ofNullable(minecraft.getCurrentServer())
                     .map(serverData -> getServerPath(basePath, serverData.name, serverData.ip, serverData.isLan()));
            }
         )
         .map(basePath::resolve);
   }

   public static Path getServerPath(String serverName, String serverAddress) {
      return getServerPath(serverName, serverAddress, false);
   }

   public static Path getServerPath(String serverName, String serverAddress, boolean isLan) {
      return isLan ? getNamedServerPath("%s (LAN connection)".formatted(serverName)) : parseServerAddress(serverAddress).map(serverAddressHostAndPort -> {
         String addressName = getAddressName(serverAddressHostAndPort);
         String name = "%s (%s)".formatted(serverName, addressName);
         return getNamedServerPath(name);
      }).orElseGet(() -> getLegacyServerPath(serverName, serverAddress));
   }

   public static Path getServerDirPath() {
      return serverDirPath;
   }

   public static Path getServerPath(Path basePath, String serverName, String serverAddress) {
      return getServerPath(basePath, serverName, serverAddress, false);
   }

   public static Path getServerPath(Path basePath, String serverName, String serverAddress, boolean isLan) {
      Path legacyServerPath = getLegacyServerPath(serverName, serverAddress);
      return Files.exists(basePath.resolve(legacyServerPath)) ? legacyServerPath : getServerPath(serverName, serverAddress, isLan);
   }

   private static String getAddressName(HostAndPort hostAndPort) {
      String host = hostAndPort.getHost();
      host = PathUtil.sanitizePathName(host.toLowerCase(Locale.ROOT));
      int port = hostAndPort.getPort();
      return port != 25565 ? "%s %d".formatted(host, port) : host;
   }

   private static Optional<HostAndPort> parseServerAddress(String serverAddressString) {
      try {
         HostAndPort hostAndPort = HostAndPort.fromString(serverAddressString).withDefaultPort(25565);
         String host = hostAndPort.getHost();
         if (!InetAddresses.isInetAddress(host)) {
            host = IDN.toASCII(host);
         }

         if (!host.isEmpty()) {
            return Optional.of(HostAndPort.fromParts(host, hostAndPort.getPort()));
         }
      } catch (IllegalArgumentException var3) {
      }

      return Optional.empty();
   }

   private static Path getLegacyServerPath(String serverName, String serverAddress) {
      String ipHashHex = Integer.toHexString(serverAddress.hashCode());
      String name = "%s_%s".formatted(serverName, ipHashHex);
      name = PathUtil.sanitizePathNameLegacy(name);
      return serverDirPath.resolve(name);
   }

   private static Path getNamedServerPath(String name) {
      name = PathUtil.sanitizePathName(name);
      return serverDirPath.resolve(name);
   }
}
