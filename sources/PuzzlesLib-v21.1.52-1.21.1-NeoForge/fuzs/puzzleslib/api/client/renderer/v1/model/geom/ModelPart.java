package fuzs.puzzleslib.api.client.renderer.v1.model.geom;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart.Cube;

public class ModelPart extends net.minecraft.client.model.geom.ModelPart {
   public ModelPart(List<Cube> cubes, Map<String, ModelPart> children) {
      super(cubes, children);
   }

   public ModelPart(net.minecraft.client.model.geom.ModelPart modelPart) {
      this(
         modelPart.cubes,
         modelPart.children
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Entry::getKey, entry -> new ModelPart((net.minecraft.client.model.geom.ModelPart)entry.getValue())))
      );
   }

   public PartPose storePose() {
      return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
   }

   public PartPose getInitialPose() {
      return (PartPose)super.getInitialPose();
   }

   public void setInitialPose(net.minecraft.client.model.geom.PartPose initialPose) {
      super.setInitialPose((net.minecraft.client.model.geom.PartPose)(initialPose instanceof PartPose ? initialPose : new PartPose(initialPose)));
   }

   public void loadPose(net.minecraft.client.model.geom.PartPose partPose) {
      super.loadPose(partPose);
      if (partPose instanceof PartPose) {
         this.xScale = ((PartPose)partPose).xScale;
         this.yScale = ((PartPose)partPose).yScale;
         this.zScale = ((PartPose)partPose).zScale;
      }
   }
}
