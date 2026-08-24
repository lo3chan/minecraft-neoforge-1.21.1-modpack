package com.iafenvoy.origins.network;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.network.payload.ChooseOriginC2SPayload;
import com.iafenvoy.origins.network.payload.ChooseRandomOriginC2SPayload;
import com.iafenvoy.origins.network.payload.ConfirmOriginS2CPayload;
import com.iafenvoy.origins.network.payload.PowerToggleC2SPayload;
import com.iafenvoy.origins.util.HolderHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ServerNetworkHandler {
   static void onChooseOrigin(ChooseOriginC2SPayload packet, IPayloadContext context) {
      if (context.player() instanceof ServerPlayer player) {
         OriginDataHolder var7 = OriginDataHolder.get(player);
         Holder layer = packet.layer();
         if (var7.hasOriginInLayer(layer)) {
            Origins.LOGGER
               .warn("Player {} tried to choose origin for layer \"{}\" while having one already.", player.getName().getString(), HolderHelper.string(layer));
         } else {
            Optional<Holder<Origin>> optional = packet.origin();
            if (optional.isPresent()) {
               Holder<Origin> origin = optional.get();
               if (!((Origin)origin.value()).unchoosable() && !((Layer)layer.value()).collectOrigins(context.player()).noneMatch(origin::equals)) {
                  var7.setOrigin(layer, origin);
                  Origins.LOGGER
                     .info(
                        "Player {} chose origin \"{}\" for layer \"{}\"",
                        new Object[]{player.getName().getString(), HolderHelper.string(origin), HolderHelper.string(layer)}
                     );
               } else {
                  Origins.LOGGER
                     .warn(
                        "Player {} tried to choose unchoosable origin \"{}\" from layer \"{}\"!",
                        new Object[]{player.getName().getString(), HolderHelper.string(origin), HolderHelper.string(layer)}
                     );
                  var7.clearOrigin(layer);
               }
            } else {
               randomOrigin(player, var7, layer);
            }

            context.reply(new ConfirmOriginS2CPayload(layer, var7.getOrigin(layer)));
            var7.getData().setSelecting(false);
            var7.sync();
         }
      }
   }

   static void onChooseRandomOrigin(ChooseRandomOriginC2SPayload packet, IPayloadContext context) {
      if (context.player() instanceof ServerPlayer player) {
         OriginDataHolder var5 = OriginDataHolder.get(player);
         Holder layer = packet.layer();
         if (var5.hasOriginInLayer(layer)) {
            Origins.LOGGER
               .warn(
                  "Player {} tried to choose random origin for layer \"{}\" while having one already.",
                  player.getName().getString(),
                  HolderHelper.string(layer)
               );
         } else {
            randomOrigin(player, var5, layer);
            context.reply(new ConfirmOriginS2CPayload(layer, var5.getOrigin(layer)));
            var5.getData().setSelecting(false);
            var5.sync();
         }
      }
   }

   private static void randomOrigin(ServerPlayer player, OriginDataHolder holder, Holder<Layer> layer) {
      List<Holder<Origin>> randomOriginIds = ((Layer)layer.value()).collectRandomizableOrigins(player).toList();
      if (((Layer)layer.value()).allowRandom() && !randomOriginIds.isEmpty()) {
         Holder<Origin> origin = randomOriginIds.get(player.getRandom().nextInt(randomOriginIds.size()));
         holder.setOrigin(layer, origin);
         Origins.LOGGER.info("Player {} was randomly assigned the following origin: {}", player.getName().getString(), HolderHelper.string(origin));
      } else {
         Origins.LOGGER
            .warn("Player {} tried to choose a random origin for layer \"{}\", which is not allowed!", player.getName().getString(), HolderHelper.string(layer));
         holder.clearOrigin(layer);
      }
   }

   public static void onPowerToggle(PowerToggleC2SPayload payload, IPayloadContext context) {
      if (context.player() instanceof ServerPlayer player) {
         PowerHelper.get(player).toggle(payload.key());
      }
   }
}
