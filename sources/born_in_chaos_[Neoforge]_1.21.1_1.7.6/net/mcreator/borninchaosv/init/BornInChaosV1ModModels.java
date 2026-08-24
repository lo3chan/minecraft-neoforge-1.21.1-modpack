package net.mcreator.borninchaosv.init;

import net.mcreator.borninchaosv.client.model.ModelArmorDarkMetal18;
import net.mcreator.borninchaosv.client.model.ModelBootDarkMetal18;
import net.mcreator.borninchaosv.client.model.ModelDarcFar18;
import net.mcreator.borninchaosv.client.model.ModelHelmetDarkMetal18;
import net.mcreator.borninchaosv.client.model.ModelPumpkin_Bl;
import net.mcreator.borninchaosv.client.model.Modeldamneddemomanshat;
import net.mcreator.borninchaosv.client.model.Modeldarkbib;
import net.mcreator.borninchaosv.client.model.Modeldarkboots;
import net.mcreator.borninchaosv.client.model.Modeldarkhelmet;
import net.mcreator.borninchaosv.client.model.Modelkillerrabbitears;
import net.mcreator.borninchaosv.client.model.Modellordpumpkinheadhat;
import net.mcreator.borninchaosv.client.model.Modelmagic_arrow18;
import net.mcreator.borninchaosv.client.model.Modelmissionaryhat;
import net.mcreator.borninchaosv.client.model.Modelnightrobeboot;
import net.mcreator.borninchaosv.client.model.Modelnightrobebreastplate;
import net.mcreator.borninchaosv.client.model.Modelnightrobemasc;
import net.mcreator.borninchaosv.client.model.Modelphantombomb;
import net.mcreator.borninchaosv.client.model.Modelpumpkinbullet;
import net.mcreator.borninchaosv.client.model.Modelspinyshellarmorchestplate;
import net.mcreator.borninchaosv.client.model.Modelspinyshellarmorhelmet;
import net.mcreator.borninchaosv.client.model.Modelspiritguidesombrero;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class BornInChaosV1ModModels {
   @SubscribeEvent
   public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(Modelpumpkinbullet.LAYER_LOCATION, Modelpumpkinbullet::createBodyLayer);
      event.registerLayerDefinition(Modelnightrobemasc.LAYER_LOCATION, Modelnightrobemasc::createBodyLayer);
      event.registerLayerDefinition(Modelspiritguidesombrero.LAYER_LOCATION, Modelspiritguidesombrero::createBodyLayer);
      event.registerLayerDefinition(ModelArmorDarkMetal18.LAYER_LOCATION, ModelArmorDarkMetal18::createBodyLayer);
      event.registerLayerDefinition(ModelBootDarkMetal18.LAYER_LOCATION, ModelBootDarkMetal18::createBodyLayer);
      event.registerLayerDefinition(ModelPumpkin_Bl.LAYER_LOCATION, ModelPumpkin_Bl::createBodyLayer);
      event.registerLayerDefinition(ModelDarcFar18.LAYER_LOCATION, ModelDarcFar18::createBodyLayer);
      event.registerLayerDefinition(Modelnightrobebreastplate.LAYER_LOCATION, Modelnightrobebreastplate::createBodyLayer);
      event.registerLayerDefinition(Modelspinyshellarmorhelmet.LAYER_LOCATION, Modelspinyshellarmorhelmet::createBodyLayer);
      event.registerLayerDefinition(Modelspinyshellarmorchestplate.LAYER_LOCATION, Modelspinyshellarmorchestplate::createBodyLayer);
      event.registerLayerDefinition(Modelmissionaryhat.LAYER_LOCATION, Modelmissionaryhat::createBodyLayer);
      event.registerLayerDefinition(ModelHelmetDarkMetal18.LAYER_LOCATION, ModelHelmetDarkMetal18::createBodyLayer);
      event.registerLayerDefinition(Modelmagic_arrow18.LAYER_LOCATION, Modelmagic_arrow18::createBodyLayer);
      event.registerLayerDefinition(Modeldarkbib.LAYER_LOCATION, Modeldarkbib::createBodyLayer);
      event.registerLayerDefinition(Modeldarkhelmet.LAYER_LOCATION, Modeldarkhelmet::createBodyLayer);
      event.registerLayerDefinition(Modellordpumpkinheadhat.LAYER_LOCATION, Modellordpumpkinheadhat::createBodyLayer);
      event.registerLayerDefinition(Modelkillerrabbitears.LAYER_LOCATION, Modelkillerrabbitears::createBodyLayer);
      event.registerLayerDefinition(Modeldamneddemomanshat.LAYER_LOCATION, Modeldamneddemomanshat::createBodyLayer);
      event.registerLayerDefinition(Modelnightrobeboot.LAYER_LOCATION, Modelnightrobeboot::createBodyLayer);
      event.registerLayerDefinition(Modeldarkboots.LAYER_LOCATION, Modeldarkboots::createBodyLayer);
      event.registerLayerDefinition(Modelphantombomb.LAYER_LOCATION, Modelphantombomb::createBodyLayer);
   }
}
