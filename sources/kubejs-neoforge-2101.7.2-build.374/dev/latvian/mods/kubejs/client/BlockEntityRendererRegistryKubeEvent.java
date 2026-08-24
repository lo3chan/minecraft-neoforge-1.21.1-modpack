package dev.latvian.mods.kubejs.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

public class BlockEntityRendererRegistryKubeEvent implements ClientKubeEvent {
   private final RegisterRenderers event;

   public BlockEntityRendererRegistryKubeEvent(RegisterRenderers event) {
      this.event = event;
   }

   public void register(BlockEntityType<?> type, BlockEntityRendererProvider renderer) {
      this.event.registerBlockEntityRenderer(type, renderer);
   }
}
