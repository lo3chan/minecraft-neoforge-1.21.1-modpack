package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.network.S2CRidePlayerPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.network.PacketDistributor;

public class SaddledProperty extends Property {
   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      Player user = event.getEntity();
      if (user != event.getTarget() && user.startRiding(event.getTarget())) {
         event.setCanceled(true);
         event.setCancellationResult(InteractionResult.SUCCESS);
         if (event.getTarget() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new S2CRidePlayerPayload(user.getId(), false), new CustomPacketPayload[0]);
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 14313004;
   }
}
