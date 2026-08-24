package fuzs.puzzleslib.api.client.renderer.v1.model.geom.builders;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.client.renderer.v1.model.geom.PartPose;
import java.util.function.UnaryOperator;

public class MeshDefinition extends net.minecraft.client.model.geom.builders.MeshDefinition {
   private final PartDefinition root;

   public MeshDefinition() {
      this(new PartDefinition(ImmutableList.of(), PartPose.ZERO));
   }

   public MeshDefinition(net.minecraft.client.model.geom.builders.MeshDefinition meshDefinition) {
      this(new PartDefinition(meshDefinition.getRoot()));
   }

   private MeshDefinition(PartDefinition root) {
      this.root = root;
   }

   public PartDefinition getRoot() {
      return this.root;
   }

   public MeshDefinition transformed(UnaryOperator<PartPose> transformer) {
      return new MeshDefinition(this.root.transformed(transformer));
   }

   public MeshDefinition apply(MeshTransformer transformer) {
      return transformer.apply(this);
   }
}
