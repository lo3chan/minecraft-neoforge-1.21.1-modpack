package tallestegg.guardvillagers.client;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import tallestegg.guardvillagers.GuardEntityType;
import tallestegg.guardvillagers.client.models.GuardArmorModel;
import tallestegg.guardvillagers.client.models.GuardModel;
import tallestegg.guardvillagers.client.models.GuardSteveModel;
import tallestegg.guardvillagers.client.renderer.GuardRenderer;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class GuardClientEvents {
   public static ModelLayerLocation GUARD = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("modded/guardvillagers/guard"), "main");
   public static ModelLayerLocation GUARD_STEVE = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("modded/guardvillagers/guard_steve"), "main");
   public static ModelLayerLocation GUARD_ARMOR_OUTER = new ModelLayerLocation(
      ResourceLocation.withDefaultNamespace("modded/guardvillagers/guard"), "armor_outer"
   );
   public static ModelLayerLocation GUARD_ARMOR_INNER = new ModelLayerLocation(
      ResourceLocation.withDefaultNamespace("modded/guardvillagers/guard"), "armor_inner"
   );
   public static ModelLayerLocation GUARD_PLAYER_ARMOR_OUTER = new ModelLayerLocation(
      ResourceLocation.withDefaultNamespace("modded/guardvillagers/guard_steve"), "armor_outer"
   );
   public static ModelLayerLocation GUARD_PLAYER_ARMOR_INNER = new ModelLayerLocation(
      ResourceLocation.withDefaultNamespace("modded/guardvillagers/guard_steve"), "armor_inner"
   );

   @SubscribeEvent
   public static void layerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(GUARD, GuardModel::createBodyLayer);
      event.registerLayerDefinition(GUARD_STEVE, GuardSteveModel::createMesh);
      event.registerLayerDefinition(GUARD_ARMOR_OUTER, GuardArmorModel::createOuterArmorLayer);
      event.registerLayerDefinition(GUARD_ARMOR_INNER, GuardArmorModel::createInnerArmorLayer);
      event.registerLayerDefinition(
         GUARD_PLAYER_ARMOR_INNER, () -> LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(0.5F)), 64, 32)
      );
      event.registerLayerDefinition(
         GUARD_PLAYER_ARMOR_OUTER, () -> LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(1.0F)), 64, 32)
      );
   }

   @SubscribeEvent
   public static void entityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)GuardEntityType.GUARD.get(), GuardRenderer::new);
   }
}
