package fabric.me.thosea.badoptimizations.mixin.renderer.entity;

import fabric.me.thosea.badoptimizations.interfaces.EntityMethods;
import fabric.me.thosea.badoptimizations.interfaces.EntityTypeMethods;
import fabric.me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_3300;
import net.minecraft.class_742;
import net.minecraft.class_897;
import net.minecraft.class_898;
import net.minecraft.class_8685.class_7920;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {class_898.class},
   priority = 700
)
public abstract class MixinEntityRendererDispatcher {
   @Shadow
   private Map<class_1299<?>, class_897<?>> field_4696;
   @Shadow
   private Map<class_7920, class_897<? extends class_1657>> field_4687;

   @Overwrite
   public <T extends class_1297 & EntityMethods> class_897<? super T> method_3953(T entity) {
      class_897<class_1297> renderer = entity.bo$getRenderer();
      return renderer != null ? renderer : this.bo$getOtherRenderer(entity);
   }

   private <T extends class_1297> class_897<? super T> bo$getOtherRenderer(T entity) {
      if (entity instanceof class_742 player) {
         class_897<? extends class_1657> renderer = this.field_4687.get(player.method_52814().comp_1629());
         return (class_897<? super T>)(renderer != null ? renderer : this.field_4687.get(class_7920.field_41123));
      } else {
         return (class_897<? super T>)this.field_4696.get(entity.method_5864());
      }
   }

   @Inject(
      method = {"method_14491(Lnet/minecraft/class_3300;)V"},
      at = {@At("RETURN")}
   )
   private void afterReload(class_3300 manager, CallbackInfo ci) {
      for (Entry<class_1299<?>, class_897<?>> entry : this.field_4696.entrySet()) {
         ((EntityTypeMethods)entry.getKey()).bo$setRenderer(entry.getValue());
      }

      PlayerModelRendererHolder.WIDE_RENDERER = this.field_4687.get(class_7920.field_41123);
      PlayerModelRendererHolder.SLIM_RENDERER = this.field_4687.get(class_7920.field_41122);
   }
}
