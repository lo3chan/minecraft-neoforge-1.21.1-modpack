package net.blay09.mods.balm.neoforge.client.renderer.entity.internal;

import java.util.function.Supplier;
import net.blay09.mods.balm.client.renderer.entity.BalmEntityRendererRegistrar;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

public class NeoForgeBalmEntityRendererRegistrar implements BalmEntityRendererRegistrar {
   private final RegisterRenderers event;

   public NeoForgeBalmEntityRendererRegistrar(RegisterRenderers event) {
      this.event = event;
   }

   @Override
   public <T extends Entity> void register(Holder<? extends EntityType<? extends T>> entityTypeHolder, EntityRendererProvider<? super T> provider) {
      this.event.registerEntityRenderer((EntityType)entityTypeHolder.value(), provider);
   }

   @Override
   public <T extends Entity> void register(
      String name, Supplier<? extends EntityType<? extends T>> entityTypeSupplier, EntityRendererProvider<? super T> provider
   ) {
      this.event.registerEntityRenderer(entityTypeSupplier.get(), provider);
   }
}
