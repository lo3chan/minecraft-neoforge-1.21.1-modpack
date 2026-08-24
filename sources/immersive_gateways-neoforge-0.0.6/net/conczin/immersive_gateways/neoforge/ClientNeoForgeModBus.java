package net.conczin.immersive_gateways.neoforge;

import net.conczin.immersive_gateways.BlockEntityTypes;
import net.conczin.immersive_gateways.block.GatewayBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   modid = "immersive_gateways",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ClientNeoForgeModBus {
   @SubscribeEvent
   public static void registerBlockEntityRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer(BlockEntityTypes.GATEWAY, GatewayBlockEntityRenderer::new);
   }
}
