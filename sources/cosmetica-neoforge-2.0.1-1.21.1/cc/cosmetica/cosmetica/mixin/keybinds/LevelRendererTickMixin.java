package cc.cosmetica.cosmetica.mixin.keybinds;

import cc.cosmetica.cosmetica.Keybinds;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class LevelRendererTickMixin {
   @Inject(
      at = {@At("RETURN")},
      method = {"tick()V"}
   )
   private void onRenderLevel(CallbackInfo info) {
      Keybinds.processKeybinds();
   }
}
