package io.github.razordevs.deep_aether.init;

import io.github.razordevs.deep_aether.client.model.AerglowFishModel;
import io.github.razordevs.deep_aether.client.model.BabyZephyrModel;
import io.github.razordevs.deep_aether.client.model.ClassicEOTSSegmentModel;
import io.github.razordevs.deep_aether.client.model.EOTSModel;
import io.github.razordevs.deep_aether.client.model.EOTSSegmentModel;
import io.github.razordevs.deep_aether.client.model.GentleWindModel;
import io.github.razordevs.deep_aether.client.model.QuailModel;
import io.github.razordevs.deep_aether.client.model.ScarfModel;
import io.github.razordevs.deep_aether.client.model.VenomiteBubbleModel;
import io.github.razordevs.deep_aether.client.model.VenomiteModel;
import io.github.razordevs.deep_aether.client.model.WindflyModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.client.renderer.entity.AetherFishRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.BabyZephyrRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.DABoatRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.EOTSRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.EOTSSegmentRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.FireProjectileRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.GentleWindRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.QuailRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.StormArrowRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.VenomiteBubbleRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.VenomiteRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.WindCrystalRenderer;
import io.github.razordevs.deep_aether.client.renderer.entity.WindflyRenderer;
import io.github.razordevs.deep_aether.entity.DABoatEntity;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class DAEntityRenderers {
   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)DAEntities.AERGLOW_FISH.get(), AetherFishRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.QUAIL.get(), QuailRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.VENOMITE.get(), VenomiteRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.WINDFLY.get(), WindflyRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.EOTS_CONTROLLER.get(), EOTSRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.EOTS_SEGMENT.get(), EOTSSegmentRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.GENTLE_WIND.get(), GentleWindRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.WINDFLY.get(), WindflyRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)DABlockEntityTypes.SIGN.get(), SignRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)DABlockEntityTypes.HANGING_SIGN.get(), HangingSignRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.BOAT.get(), context -> new DABoatRenderer(context, false));
      event.registerEntityRenderer((EntityType)DAEntities.CHEST_BOAT.get(), context -> new DABoatRenderer(context, true));
      event.registerEntityRenderer((EntityType)DAEntities.QUAIL_EGG.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.FIRE_PROJECTILE.get(), FireProjectileRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.VENOMITE_BUBBLE.get(), VenomiteBubbleRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.WIND_CRYSTAL.get(), WindCrystalRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.STORM_ARROW.get(), StormArrowRenderer::new);
      event.registerEntityRenderer((EntityType)DAEntities.BABY_ZEPHYR.get(), BabyZephyrRenderer::new);
   }

   @SubscribeEvent
   public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(DAModelLayers.AERGLOW_FISH, AerglowFishModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.EOTS_CONTROLLER, EOTSModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.EOTS_SEGMENT, EOTSSegmentModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.EOTS_SEGMENT_CLASSIC, ClassicEOTSSegmentModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.GENTLE_WIND, GentleWindModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.QUAIL, QuailModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.VENOMITE_BUBBLE, VenomiteBubbleModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.VENOMITE, VenomiteModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.WINDFLY, WindflyModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.BABY_ZEPHYR, BabyZephyrModel::createBodyLayer);
      event.registerLayerDefinition(DAModelLayers.WIND_SHIELD, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(1.1F), false), 64, 64));
      event.registerLayerDefinition(
         DAModelLayers.WIND_SHIELD_SLIM, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(1.15F), true), 64, 64)
      );
      event.registerLayerDefinition(
         DAModelLayers.WIND_SHIELD_ARM, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.4F), false), 64, 64)
      );
      event.registerLayerDefinition(DAModelLayers.SCARF, ScarfModel::createBodyLayer);

      for (DABoatEntity.Type type : DABoatEntity.Type.values()) {
         event.registerLayerDefinition(
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("deep_aether", type.getModelLocation()), "main"), BoatModel::createBodyModel
         );
         event.registerLayerDefinition(
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("deep_aether", type.getChestModelLocation()), "main"), ChestBoatModel::createBodyModel
         );
      }
   }
}
