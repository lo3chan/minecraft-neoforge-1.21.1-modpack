package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.util.v1.CommonHelper;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface SidedProxy extends Proxy {
   void registerAllLoadingHandlers();

   void registerAllEventHandlers();

   default BlockableEventLoop<? super TickTask> getBlockableEventLoop(Level level) {
      if (level instanceof ServerLevel serverLevel) {
         return serverLevel.getServer();
      } else {
         throw new RuntimeException("Blockable event loop accessed for the wrong physical side!");
      }
   }

   default RegistryAccess getRegistryAccess() {
      return CommonHelper.getMinecraftServer() != null ? CommonHelper.getMinecraftServer().registryAccess() : null;
   }

   @Override
   default Player getClientPlayer() {
      throw new RuntimeException("Client player accessed for the wrong physical side!");
   }

   @Override
   default Level getClientLevel() {
      throw new RuntimeException("Client level accessed for the wrong physical side!");
   }

   @Override
   default boolean hasControlDown() {
      return false;
   }

   @Override
   default boolean hasShiftDown() {
      return false;
   }

   @Override
   default boolean hasAltDown() {
      return false;
   }

   @Override
   default List<Component> splitTooltipLines(Component component) {
      return Collections.singletonList(component);
   }

   default void registerConfigurationScreen(String modId, String... otherModIds) {
   }
}
