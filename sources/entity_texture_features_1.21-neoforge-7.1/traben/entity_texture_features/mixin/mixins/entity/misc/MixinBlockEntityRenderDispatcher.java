package traben.entity_texture_features.mixin.mixins.entity.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin({BlockEntityRenderDispatcher.class})
public class MixinBlockEntityRenderDispatcher {
   private static final String RENDER_METHOD = "tryRender";

   @Inject(
      method = {"tryRender"},
      at = {@At("HEAD")}
   )
   private static void etf$grabContext(CallbackInfo ci, @Local(argsOnly = true) BlockEntity blockEntity) {
      ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity)blockEntity));
   }

   @Inject(
      method = {"tryRender"},
      at = {@At("RETURN")}
   )
   private static void etf$clearContext(CallbackInfo ci) {
      ETFRenderContext.reset();
   }

   @ModifyArg(
      method = {"setupAndRender"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"
      ),
      index = 4
   )
   private static int etf$vanillaLightOverride(int light) {
      return ETF.config().getConfig().getLightOverrideBE(light);
   }
}
