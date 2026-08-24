package vazkii.psi.common.core.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageLoopcastSync;

@EventBusSubscriber(
   modid = "psi"
)
public class LoopcastTrackingHandler {
   @SubscribeEvent
   public static void onPlayerStartTracking(StartTracking event) {
      if (event.getTarget() instanceof Player) {
         syncDataFor((Player)event.getTarget(), (ServerPlayer)event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
      syncDataFor(event.getEntity(), (ServerPlayer)event.getEntity());
   }

   @SubscribeEvent
   public static void onPlayerLogIn(PlayerLoggedInEvent event) {
      syncDataFor(event.getEntity(), (ServerPlayer)event.getEntity());
   }

   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      syncDataFor(event.getEntity(), (ServerPlayer)event.getEntity());
   }

   public static void syncDataFor(Player player, ServerPlayer receiver) {
      PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
      MessageRegister.sendToPlayer(receiver, new MessageLoopcastSync(player.getId(), data.loopcasting, data.loopcastHand));
   }

   public static void syncForTrackersAndSelf(ServerPlayer playerEntity) {
      PlayerDataHandler.PlayerData data = PlayerDataHandler.get(playerEntity);
      MessageLoopcastSync messageLoopcastSync = new MessageLoopcastSync(playerEntity.getId(), data.loopcasting, data.loopcastHand);
      MessageRegister.sendToPlayersTrackingEntity(playerEntity, messageLoopcastSync);
      MessageRegister.sendToPlayer(playerEntity, messageLoopcastSync);
   }
}
