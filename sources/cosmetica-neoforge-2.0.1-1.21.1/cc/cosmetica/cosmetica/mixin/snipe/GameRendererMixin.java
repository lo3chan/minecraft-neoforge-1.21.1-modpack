package cc.cosmetica.cosmetica.mixin.snipe;

import cc.cosmetica.cosmetica.util.Sniper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Shadow
   @Final
   Minecraft minecraft;

   @Inject(
      at = {@At("RETURN")},
      method = {"pick(F)V"}
   )
   private void onPick(float yawProbably, CallbackInfo info) {
      Sniper.updateTargetPlayer(this.minecraft, yawProbably);
   }
}
