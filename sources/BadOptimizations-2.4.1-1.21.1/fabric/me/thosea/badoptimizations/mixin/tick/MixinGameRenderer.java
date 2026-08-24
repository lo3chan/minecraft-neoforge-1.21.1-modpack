package fabric.me.thosea.badoptimizations.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_310;
import net.minecraft.class_3300;
import net.minecraft.class_4599;
import net.minecraft.class_5498;
import net.minecraft.class_7172;
import net.minecraft.class_742;
import net.minecraft.class_757;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_757.class})
public final class MixinGameRenderer {
   @Shadow
   @Final
   class_310 field_4015;
   private class_7172<Double> bo$fovEffectScale;

   @Inject(
      method = {"<init>(Lnet/minecraft/class_310;Lnet/minecraft/class_759;Lnet/minecraft/class_3300;Lnet/minecraft/class_4599;)V"},
      at = {@At("TAIL")}
   )
   private void afterCreate(class_310 client, class_759 heldItemRenderer, class_3300 resourceManager, class_4599 buffers, CallbackInfo ci) {
      this.bo$fovEffectScale = client.field_1690.method_42454();
   }

   @WrapOperation(
      method = {"method_3199()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_742;method_3118()F"
      )}
   )
   private float getPlayerFov(class_742 player, Operation<Float> original) {
      if ((Double)this.bo$fovEffectScale.method_41753() == 0.0) {
         return this.field_4015.field_1690.method_31044() == class_5498.field_26664 && player.method_31550() ? 0.1F : 1.0F;
      } else {
         return (Float)original.call(new Object[]{player});
      }
   }
}
