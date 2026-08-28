/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.net.HostAndPort
 *  com.google.common.net.InetAddresses
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.Connection
 *  net.minecraft.server.MinecraftServer
 */
package mezz.jei.common.util;

import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import java.net.IDN;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.PathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;

public final class ServerConfigPathUtil {
    private static final Path worldDirPath = Path.of("world", new String[0]);
    private static final Path serverDirPath = worldDirPath.resolve("server");

    private ServerConfigPathUtil() {
    }

    public static Optional<Path> getWorldPath(Path basePath) {
        Minecraft minecraft = Minecraft.getInstance();
        return Optional.ofNullable(minecraft.getConnection()).flatMap(clientPacketListener -> {
            Connection connection = clientPacketListener.getConnection();
            if (connection.isMemoryConnection()) {
                return Optional.ofNullable(minecraft.getSingleplayerServer()).flatMap(minecraftServer -> Services.PLATFORM.getWorldHelper().getLevelId((MinecraftServer)minecraftServer).map(PathUtil::sanitizePathName).map(name -> worldDirPath.resolve("local").resolve((String)name)));
            }
            return Optional.ofNullable(minecraft.getCurrentServer()).map(serverData -> ServerConfigPathUtil.getServerPath(basePath, serverData.name, serverData.ip, serverData.isLan()));
        }).map(basePath::resolve);
    }

    public static Path getServerPath(String serverName, String serverAddress) {
        return ServerConfigPathUtil.getServerPath(serverName, serverAddress, false);
    }

    public static Path getServerPath(String serverName, String serverAddress, boolean isLan) {
        if (isLan) {
            return ServerConfigPathUtil.getNamedServerPath("%s (LAN connection)".formatted(serverName));
        }
        return ServerConfigPathUtil.parseServerAddress(serverAddress).map(serverAddressHostAndPort -> {
            String addressName = ServerConfigPathUtil.getAddressName(serverAddressHostAndPort);
            String name = "%s (%s)".formatted(serverName, addressName);
            return ServerConfigPathUtil.getNamedServerPath(name);
        }).orElseGet(() -> ServerConfigPathUtil.getLegacyServerPath(serverName, serverAddress));
    }

    public static Path getServerDirPath() {
        return serverDirPath;
    }

    public static Path getServerPath(Path basePath, String serverName, String serverAddress) {
        return ServerConfigPathUtil.getServerPath(basePath, serverName, serverAddress, false);
    }

    public static Path getServerPath(Path basePath, String serverName, String serverAddress, boolean isLan) {
        Path legacyServerPath = ServerConfigPathUtil.getLegacyServerPath(serverName, serverAddress);
        if (Files.exists(basePath.resolve(legacyServerPath), new LinkOption[0])) {
            return legacyServerPath;
        }
        return ServerConfigPathUtil.getServerPath(serverName, serverAddress, isLan);
    }

    private static String getAddressName(HostAndPort hostAndPort) {
        String host = hostAndPort.getHost();
        host = PathUtil.sanitizePathName(host.toLowerCase(Locale.ROOT));
        int port = hostAndPort.getPort();
        if (port != 25565) {
            return "%s %d".formatted(host, port);
        }
        return host;
    }

    private static Optional<HostAndPort> parseServerAddress(String serverAddressString) {
        try {
            HostAndPort hostAndPort = HostAndPort.fromString((String)serverAddressString).withDefaultPort(25565);
            String host = hostAndPort.getHost();
            if (!InetAddresses.isInetAddress((String)host)) {
                host = IDN.toASCII(host);
            }
            if (!host.isEmpty()) {
                return Optional.of(HostAndPort.fromParts((String)host, (int)hostAndPort.getPort()));
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
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

