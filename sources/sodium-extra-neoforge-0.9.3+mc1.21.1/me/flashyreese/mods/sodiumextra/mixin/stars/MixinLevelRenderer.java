package me.flashyreese.mods.sodiumextra.mixin.stars;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LevelRenderer.class})
public class MixinLevelRenderer {
   @WrapOperation(
      method = {"renderSky"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"
      )}
   )
   public float redirectGetStarBrightness(ClientLevel instance, float f, Operation<Float> original) {
      return SodiumExtraClientMod.options().detailSettings.stars ? (Float)original.call(new Object[]{instance, f}) : 0.0F;
   }
}
