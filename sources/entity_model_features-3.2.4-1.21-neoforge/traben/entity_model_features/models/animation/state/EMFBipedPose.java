package traben.entity_model_features.models.animation.state;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class EMFBipedPose {
   private final EMFBipedPose.PosePart head;
   private final EMFBipedPose.PosePart body;
   private final EMFBipedPose.PosePart leftArm;
   private final EMFBipedPose.PosePart rightArm;
   private final EMFBipedPose.PosePart leftLeg;
   private final EMFBipedPose.PosePart rightLeg;
   public final EMFBipedPose.PosePart root;

   public EMFBipedPose(HumanoidModel<?> model) {
      this.head = new EMFBipedPose.PosePart(model.head);
      this.body = new EMFBipedPose.PosePart(model.body);
      this.leftArm = new EMFBipedPose.PosePart(model.leftArm);
      this.rightArm = new EMFBipedPose.PosePart(model.rightArm);
      this.leftLeg = new EMFBipedPose.PosePart(model.leftLeg);
      this.rightLeg = new EMFBipedPose.PosePart(model.rightLeg);
      this.root = null;
   }

   public void applyTo(HumanoidModel<?> model) {
      this.head.applyTo(model.head);
      this.body.applyTo(model.body);
      this.leftArm.applyTo(model.leftArm);
      this.rightArm.applyTo(model.rightArm);
      this.leftLeg.applyTo(model.leftLeg);
      this.rightLeg.applyTo(model.rightLeg);
   }

   private static class PosePart {
      private final float xRot;
      private final float yRot;
      private final float zRot;
      private final float x;
      private final float y;
      private final float z;
      private final float xScale;
      private final float yScale;
      private final float zScale;

      PosePart(ModelPart modelPart) {
         this.xRot = modelPart.xRot;
         this.yRot = modelPart.yRot;
         this.zRot = modelPart.zRot;
         this.x = modelPart.x;
         this.y = modelPart.y;
         this.z = modelPart.z;
         this.xScale = modelPart.xScale;
         this.yScale = modelPart.yScale;
         this.zScale = modelPart.zScale;
      }

      private void applyTo(ModelPart modelPart) {
         modelPart.xRot = this.xRot;
         modelPart.yRot = this.yRot;
         modelPart.zRot = this.zRot;
         modelPart.x = this.x;
         modelPart.y = this.y;
         modelPart.z = this.z;
         modelPart.xScale = this.xScale;
         modelPart.yScale = this.yScale;
         modelPart.zScale = this.zScale;
      }
   }
}
