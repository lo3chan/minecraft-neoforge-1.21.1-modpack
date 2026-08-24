package pl.skidam.automodpack_core.loader;

import java.net.SocketAddress;

public interface GameCallService {
   boolean isPlayerAuthorized(SocketAddress var1, String var2);
}
