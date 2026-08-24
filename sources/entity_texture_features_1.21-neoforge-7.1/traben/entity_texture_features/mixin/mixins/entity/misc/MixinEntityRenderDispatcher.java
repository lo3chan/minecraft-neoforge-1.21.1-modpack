package traben.entity_texture_features.mixin.mixins.entity.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin({EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private <E extends Entity> void etf$grabContext(CallbackInfo ci, @Local(argsOnly = true) E entity) {
      ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity)entity));
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private void etf$clearContext(CallbackInfo ci) {
      ETFRenderContext.reset();
   }
}
