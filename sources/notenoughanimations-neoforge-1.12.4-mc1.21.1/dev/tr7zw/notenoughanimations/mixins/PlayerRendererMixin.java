package dev.tr7zw.notenoughanimations.mixins;

import dev.tr7zw.notenoughanimations.renderlayer.SwordRenderLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerRenderer.class})
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public PlayerRendererMixin(Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
      super(context, entityModel, f);
   }

   @Inject(
      method = {"<init>*(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V"},
      at = {@At("RETURN")}
   )
   public void onCreate(CallbackInfo info) {
      this.addLayer(new SwordRenderLayer(this));
   }
}
