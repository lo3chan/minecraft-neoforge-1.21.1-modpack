package codx.codxlib.neoforge;

import codx.codxlib.CodxLibMod;
import codx.codxlib.api.CodxLibCommands;
import codx.codxlib.api.UpdateChecker;
import codx.codxlib.neoforge.network.CodxLibNeoForgeNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

@Mod("codxlib")
public class CodxLibNeoForge {
   public CodxLibNeoForge(IEventBus modEventBus) {
      CodxLibMod.commonInit();
      modEventBus.addListener(CodxLibNeoForgeNetwork::register);
      CodxLibNeoForgeNetwork.installSenders();
      if (FMLEnvironment.dist.isClient()) {
         CodxLibNeoForgeClient.init();
      }

      NeoForge.EVENT_BUS.addListener(event -> CodxLibCommands.register(event.getDispatcher()));
      NeoForge.EVENT_BUS.addListener(event -> UpdateChecker.onServerStarted(event.getServer()));
      NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
   }

   private void onPlayerJoin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         UpdateChecker.onPlayerJoin(player.level().getServer(), player);
      }
   }
}
