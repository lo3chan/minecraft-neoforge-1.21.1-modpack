package traben.entity_model_features.utils;

import java.util.Map;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public interface IEMFUnmodifiedLayerRootGetter {
   Map<ModelLayerLocation, LayerDefinition> emf$getUnmodifiedRoots();
}
