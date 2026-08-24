package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.client.model.Modelbox;
import net.mcreator.undeadrevamp.client.model.Modelcloggercrown;
import net.mcreator.undeadrevamp.client.model.Modelpromodialarmor;
import net.mcreator.undeadrevamp.client.model.Modelskeeperbox;
import net.mcreator.undeadrevamp.client.model.Modelsleepbomb;
import net.mcreator.undeadrevamp.client.model.Modelstonemask;
import net.mcreator.undeadrevamp.client.model.Modeltar;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class UndeadRevamp2ModModels {
   @SubscribeEvent
   public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(Modelcloggercrown.LAYER_LOCATION, Modelcloggercrown::createBodyLayer);
      event.registerLayerDefinition(Modelpromodialarmor.LAYER_LOCATION, Modelpromodialarmor::createBodyLayer);
      event.registerLayerDefinition(Modelskeeperbox.LAYER_LOCATION, Modelskeeperbox::createBodyLayer);
      event.registerLayerDefinition(Modelstonemask.LAYER_LOCATION, Modelstonemask::createBodyLayer);
      event.registerLayerDefinition(Modelsleepbomb.LAYER_LOCATION, Modelsleepbomb::createBodyLayer);
      event.registerLayerDefinition(Modelbox.LAYER_LOCATION, Modelbox::createBodyLayer);
      event.registerLayerDefinition(Modeltar.LAYER_LOCATION, Modeltar::createBodyLayer);
   }
}
