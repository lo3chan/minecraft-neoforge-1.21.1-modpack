package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.world.entity.EntityType;

@FunctionalInterface
public interface AddLivingEntityRenderLayersCallback {
   EventInvoker<AddLivingEntityRenderLayersCallback> EVENT = EventInvoker.lookup(AddLivingEntityRenderLayersCallback.class);

   void addLivingEntityRenderLayers(EntityType<?> var1, LivingEntityRenderer<?, ?> var2, Context var3);

   static Model getPlayerModelType(PlayerRenderer playerRenderer) {
      return ((PlayerModel)playerRenderer.getModel()).slim ? Model.SLIM : Model.WIDE;
   }
}
