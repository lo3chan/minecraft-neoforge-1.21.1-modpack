package fuzs.puzzleslib.api.client.renderer.v1.model.geom.builders;

import java.util.function.Function;

@FunctionalInterface
public interface MeshTransformer {
   MeshTransformer IDENTITY = Function.identity()::apply;

   static MeshTransformer scaling(float f) {
      float g = 24.016F * (1.0F - f);
      return mesh -> mesh.transformed(partPose -> partPose.scaled(f).translated(0.0F, g, 0.0F));
   }

   MeshDefinition apply(MeshDefinition var1);
}
