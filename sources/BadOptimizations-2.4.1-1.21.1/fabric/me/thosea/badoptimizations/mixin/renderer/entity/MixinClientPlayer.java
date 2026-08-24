package fabric.me.thosea.badoptimizations.mixin.renderer.entity;

import fabric.me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import net.minecraft.class_897;
import net.minecraft.class_8685.class_7920;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_742.class})
public abstract class MixinClientPlayer extends MixinEntity {
   @Shadow
   public abstract class_8685 method_52814();

   @Override
   public class_897<?> bo$getRenderer() {
      class_7920 model = this.method_52814().comp_1629();
      if (model == class_7920.field_41123) {
         return PlayerModelRendererHolder.WIDE_RENDERER;
      } else {
         return model == class_7920.field_41122 ? PlayerModelRendererHolder.SLIM_RENDERER : null;
      }
   }
}
