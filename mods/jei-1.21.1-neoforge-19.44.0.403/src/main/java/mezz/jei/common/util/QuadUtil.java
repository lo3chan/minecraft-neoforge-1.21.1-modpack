/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package mezz.jei.common.util;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class QuadUtil {
    private QuadUtil() {
    }

    public static List<BakedQuad> getQuadsFacingDirection(List<BakedQuad> quads, PoseStack poseStack, Direction facing) {
        Matrix4f pose = poseStack.last().pose();
        Direction.Axis axis = facing.getAxis();
        float facingStep = facing.getAxisDirection().getStep();
        return quads.stream().filter(q -> {
            Direction quadDirection = q.getDirection();
            Vector3f transformedDirection = pose.transformDirection(quadDirection.step());
            double value = axis.choose((double)transformedDirection.x, (double)transformedDirection.y, (double)transformedDirection.z);
            if (facingStep > 0.0f) {
                return value > 0.0;
            }
            return value < 0.0;
        }).toList();
    }
}

