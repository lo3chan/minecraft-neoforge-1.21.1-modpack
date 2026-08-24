package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.impl.LithostitchedInternalHooks;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {GameTestServer.class},
   priority = 1500
)
public abstract class GameTestServerMixin {
   @Inject(
      method = {"initServer"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/gametest/framework/GameTestServer;loadLevel()V",
         shift = Shift.BEFORE
      )}
   )
   private void initServer(CallbackInfoReturnable<Boolean> info) {
      LithostitchedInternalHooks.onServerAboutToStart((MinecraftServer)this);
   }
}
