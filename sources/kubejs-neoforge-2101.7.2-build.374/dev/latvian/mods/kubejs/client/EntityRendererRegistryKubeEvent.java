package dev.latvian.mods.kubejs.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

public class EntityRendererRegistryKubeEvent implements ClientKubeEvent {
   private final RegisterRenderers event;

   public EntityRendererRegistryKubeEvent(RegisterRenderers event) {
      this.event = event;
   }

   public void register(EntityType<?> type, EntityRendererProvider renderer) {
      this.event.registerEntityRenderer(type, renderer);
   }
}
