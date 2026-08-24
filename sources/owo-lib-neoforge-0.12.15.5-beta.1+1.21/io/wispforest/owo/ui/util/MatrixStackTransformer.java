package io.wispforest.owo.ui.util;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public interface MatrixStackTransformer {
   default MatrixStackTransformer translate(double x, double y, double z) {
      this.getMatrixStack().translate(x, y, z);
      return this;
   }

   default MatrixStackTransformer translate(float x, float y, float z) {
      this.getMatrixStack().translate(x, y, z);
      return this;
   }

   default MatrixStackTransformer scale(float x, float y, float z) {
      this.getMatrixStack().scale(x, y, z);
      return this;
   }

   default MatrixStackTransformer multiply(Quaternionf quaternion) {
      this.getMatrixStack().mulPose(quaternion);
      return this;
   }

   default MatrixStackTransformer multiply(Quaternionf quaternion, float originX, float originY, float originZ) {
      this.getMatrixStack().rotateAround(quaternion, originX, originY, originZ);
      return this;
   }

   default MatrixStackTransformer push() {
      this.getMatrixStack().pushPose();
      return this;
   }

   default MatrixStackTransformer pop() {
      this.getMatrixStack().popPose();
      return this;
   }

   default MatrixStackTransformer multiplyPositionMatrix(Matrix4f matrix) {
      this.getMatrixStack().mulPose(matrix);
      return this;
   }

   default PoseStack getMatrixStack() {
      throw new IllegalStateException("getMatrices() method hasn't been override leading to exception!");
   }
}
