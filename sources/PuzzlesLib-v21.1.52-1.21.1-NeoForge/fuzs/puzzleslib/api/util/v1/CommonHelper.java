package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class CommonHelper {
   private CommonHelper() {
   }

   public static MinecraftServer getMinecraftServer() {
      return ProxyImpl.get().getMinecraftServer();
   }

   public static BlockableEventLoop<? super TickTask> getBlockableEventLoop(Level level) {
      return ProxyImpl.get().getBlockableEventLoop(level);
   }

   public static RegistryAccess getRegistryAccess() {
      return ProxyImpl.get().getRegistryAccess();
   }

   public static Player getClientPlayer() {
      return ProxyImpl.get().getClientPlayer();
   }

   public static Level getClientLevel() {
      return ProxyImpl.get().getClientLevel();
   }

   public static boolean hasControlDown() {
      return ProxyImpl.get().hasControlDown();
   }

   public static boolean hasShiftDown() {
      return ProxyImpl.get().hasShiftDown();
   }

   public static boolean hasAltDown() {
      return ProxyImpl.get().hasAltDown();
   }
}
