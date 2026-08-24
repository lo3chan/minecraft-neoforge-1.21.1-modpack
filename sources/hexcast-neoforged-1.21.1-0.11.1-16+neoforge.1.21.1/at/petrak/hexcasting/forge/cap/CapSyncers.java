package at.petrak.hexcasting.forge.cap;

import at.petrak.hexcasting.forge.network.MsgAltioraUpdateAck;
import at.petrak.hexcasting.forge.network.MsgPigmentUpdateAck;
import at.petrak.hexcasting.forge.network.MsgSentinelStatusUpdateAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

public class CapSyncers {
   @SubscribeEvent
   public static void copyDataOnDeath(Clone evt) {
      if (evt.getEntity() instanceof ServerPlayer player) {
         if (evt.getOriginal() instanceof ServerPlayer proto) {
            IXplatAbstractions x = IXplatAbstractions.INSTANCE;
            x.setFlight(player, x.getFlight(proto));
            x.setAltiora(player, x.getAltiora(proto));
            x.setSentinel(player, x.getSentinel(proto));
            x.setPigment(player, x.getPigment(proto));
            x.setStaffcastImage(player, x.getStaffcastVM(proto, InteractionHand.MAIN_HAND).getImage());
            x.setPatterns(player, x.getPatternsSavedInUi(proto));
         }
      }
   }

   @SubscribeEvent
   public static void syncDataOnLogin(PlayerLoggedInEvent evt) {
      if (evt.getEntity() instanceof ServerPlayer player) {
         syncSentinel(player);
         syncPigment(player);
         syncAltiora(player);
      }
   }

   @SubscribeEvent
   public static void syncDataOnRejoin(PlayerRespawnEvent evt) {
      if (evt.getEntity() instanceof ServerPlayer player) {
         syncSentinel(player);
         syncPigment(player);
         syncAltiora(player);
      }
   }

   public static void syncSentinel(ServerPlayer player) {
      IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, new MsgSentinelStatusUpdateAck(IXplatAbstractions.INSTANCE.getSentinel(player)));
   }

   public static void syncPigment(ServerPlayer player) {
      IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, new MsgPigmentUpdateAck(IXplatAbstractions.INSTANCE.getPigment(player)));
   }

   public static void syncAltiora(ServerPlayer player) {
      IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, new MsgAltioraUpdateAck(IXplatAbstractions.INSTANCE.getAltiora(player)));
   }
}
