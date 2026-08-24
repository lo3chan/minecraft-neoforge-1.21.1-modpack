package mezz.jei.common.util;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class QuadUtil {
   private QuadUtil() {
   }

   public static List<BakedQuad> getQuadsFacingDirection(List<BakedQuad> quads, PoseStack poseStack, Direction facing) {
      Matrix4f pose = poseStack.last().pose();
      Axis axis = facing.getAxis();
      float facingStep = facing.getAxisDirection().getStep();
      return quads.stream().filter(q -> {
         Direction quadDirection = q.getDirection();
         Vector3f transformedDirection = pose.transformDirection(quadDirection.step());
         double value = axis.choose(transformedDirection.x, transformedDirection.y, transformedDirection.z);
         return facingStep > 0.0F ? value > 0.0 : value < 0.0;
      }).toList();
   }
}
