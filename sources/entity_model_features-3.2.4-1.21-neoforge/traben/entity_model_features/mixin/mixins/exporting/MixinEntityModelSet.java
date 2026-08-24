package traben.entity_model_features.mixin.mixins.exporting;

import java.util.Map;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_model_features.utils.IEMFUnmodifiedLayerRootGetter;

@Mixin(
   value = {EntityModelSet.class},
   priority = 1001
)
public class MixinEntityModelSet implements IEMFUnmodifiedLayerRootGetter {
   @Shadow
   public Map<ModelLayerLocation, LayerDefinition> roots;

   @Override
   public Map<ModelLayerLocation, LayerDefinition> emf$getUnmodifiedRoots() {
      return this.roots;
   }
}
