package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface Proxy {
   Proxy INSTANCE = (Proxy)Util.make(
      () -> ModLoaderEnvironment.INSTANCE.isClient() ? ServiceProviderHelper.load(ClientProxyImpl.class) : ServiceProviderHelper.load(ProxyImpl.class)
   );

   Player getClientPlayer();

   Level getClientLevel();

   ClientPacketListener getClientPacketListener();

   boolean hasControlDown();

   boolean hasShiftDown();

   boolean hasAltDown();

   List<Component> splitTooltipLines(Component var1);
}
