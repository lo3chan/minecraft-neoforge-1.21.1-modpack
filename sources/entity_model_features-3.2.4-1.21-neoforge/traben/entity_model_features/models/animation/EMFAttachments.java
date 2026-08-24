package traben.entity_model_features.models.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class EMFAttachments {
   public Pose pose = null;
   private final float x;
   private final float y;
   private final float z;
   public final boolean right;

   public EMFAttachments(float x, float y, float z, boolean right) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.right = right;
   }

   public void setAttachment(PoseStack entry) {
      entry.pushPose();
      entry.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
      Pose copyOnly = entry.last();
      this.pose = new Pose(new Matrix4f(copyOnly.pose()), new Matrix3f(copyOnly.normal()));
      entry.popPose();
   }
}
