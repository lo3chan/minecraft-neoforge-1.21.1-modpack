package malte0811.ferritecore.mixin.dedupbakedquad;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.SimpleBakedModel.Builder;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Builder.class})
public class SimpleModelBuilderMixin {
   @Inject(
      method = {"addUnculledFace"},
      at = {@At("HEAD")}
   )
   public void deduplicate(BakedQuad quad, CallbackInfoReturnable<Builder> cir) {
      Deduplicator.deduplicate(quad);
   }

   @Inject(
      method = {"addCulledFace"},
      at = {@At("HEAD")}
   )
   public void deduplicate(Direction direction, BakedQuad quad, CallbackInfoReturnable<Builder> cir) {
      Deduplicator.deduplicate(quad);
   }
}
