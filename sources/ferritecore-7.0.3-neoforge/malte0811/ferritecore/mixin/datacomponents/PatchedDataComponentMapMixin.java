package malte0811.ferritecore.mixin.datacomponents;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PatchedDataComponentMap.class})
public class PatchedDataComponentMapMixin {
   @Shadow
   private Reference2ObjectMap<DataComponentType<?>, Optional<?>> patch;
   @Shadow
   private boolean copyOnWrite;

   @Inject(
      method = {"applyPatch(Lnet/minecraft/core/component/DataComponentPatch;)V", "restorePatch"},
      at = {@At("RETURN")}
   )
   private void saveMemoryIfEmpty(CallbackInfo ci) {
      if (this.patch.isEmpty()) {
         this.patch = Reference2ObjectMaps.emptyMap();
         this.copyOnWrite = true;
      }
   }

   @Inject(
      method = {"set", "remove"},
      at = {@At("RETURN")}
   )
   private void saveMemoryIfEmptyWithReturn(CallbackInfoReturnable<?> ci) {
      this.saveMemoryIfEmpty(ci);
   }
}
