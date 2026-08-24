package forge.me.thosea.badoptimizations.mixin.renderer.entity;

import forge.me.thosea.badoptimizations.interfaces.EntityMethods;
import forge.me.thosea.badoptimizations.interfaces.EntityTypeMethods;
import forge.me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {EntityRenderDispatcher.class},
   priority = 700
)
public abstract class MixinEntityRendererDispatcher {
   @Shadow
   private Map<EntityType<?>, EntityRenderer<?>> renderers;
   @Shadow
   private Map<Model, EntityRenderer<? extends Player>> playerRenderers;

   @Overwrite
   public <T extends Entity & EntityMethods> EntityRenderer<? super T> getRenderer(T entity) {
      EntityRenderer<Entity> renderer = entity.bo$getRenderer();
      return renderer != null ? renderer : this.bo$getOtherRenderer(entity);
   }

   private <T extends Entity> EntityRenderer<? super T> bo$getOtherRenderer(T entity) {
      if (entity instanceof AbstractClientPlayer player) {
         EntityRenderer<? extends Player> renderer = this.playerRenderers.get(player.getSkin().model());
         return (EntityRenderer<? super T>)(renderer != null ? renderer : this.playerRenderers.get(Model.WIDE));
      } else {
         return (EntityRenderer<? super T>)this.renderers.get(entity.getType());
      }
   }

   @Inject(
      method = {"onResourceManagerReload(Lnet/minecraft/server/packs/resources/ResourceManager;)V"},
      at = {@At("RETURN")}
   )
   private void afterReload(ResourceManager manager, CallbackInfo ci) {
      for (Entry<EntityType<?>, EntityRenderer<?>> entry : this.renderers.entrySet()) {
         ((EntityTypeMethods)entry.getKey()).bo$setRenderer(entry.getValue());
      }

      PlayerModelRendererHolder.WIDE_RENDERER = this.playerRenderers.get(Model.WIDE);
      PlayerModelRendererHolder.SLIM_RENDERER = this.playerRenderers.get(Model.SLIM);
   }
}
