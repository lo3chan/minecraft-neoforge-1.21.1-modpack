package net.irisshaders.iris.mixin.shadows;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {LevelRenderer.class},
   priority = 1010
)
public abstract class MixinPreventRebuildNearInShadowPass {
   @Shadow
   @Final
   private ObjectArrayList<RenderSection> visibleSections;

   @Inject(
      method = {"setupRender"},
      at = {@At("TAIL")}
   )
   private void iris$preventRebuildNearInShadowPass(Camera camera, Frustum frustum, boolean bl, boolean bl2, CallbackInfo ci) {
      if (ShadowRenderer.ACTIVE) {
         ObjectListIterator var6 = this.visibleSections.iterator();

         while (var6.hasNext()) {
            RenderSection chunk = (RenderSection)var6.next();
            ShadowRenderer.visibleBlockEntities.addAll(chunk.getCompiled().getRenderableBlockEntities());
         }
      }
   }
}
