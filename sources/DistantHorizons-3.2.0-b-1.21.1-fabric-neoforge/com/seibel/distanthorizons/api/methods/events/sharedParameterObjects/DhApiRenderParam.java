package com.seibel.distanthorizons.api.methods.events.sharedParameterObjects;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiRenderPass;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;

public class DhApiRenderParam implements IDhApiEventParam {
   public EDhApiRenderPass renderPass;
   public float partialTicks;
   public float nearClipPlane;
   public float farClipPlane;
   public final DhApiMat4f mcProjectionMatrix = new DhApiMat4f();
   public final DhApiMat4f mcModelViewMatrix = new DhApiMat4f();
   public final DhApiMat4f mcInverseMvmProjectionMatrix = new DhApiMat4f();
   public final DhApiMat4f dhProjectionMatrix = new DhApiMat4f();
   public final DhApiMat4f dhModelViewMatrix = new DhApiMat4f();
   public final DhApiMat4f dhMvmProjMatrix = new DhApiMat4f();
   public final DhApiMat4f dhInverseMvmProjectionMatrix = new DhApiMat4f();
   public int worldYOffset;
   public IDhApiLevelWrapper clientLevelWrapper;

   public void update(DhApiRenderParam param) {
      this.update(
         param.renderPass,
         param.partialTicks,
         param.nearClipPlane,
         param.farClipPlane,
         param.mcProjectionMatrix,
         param.mcModelViewMatrix,
         param.dhProjectionMatrix,
         param.mcModelViewMatrix,
         param.worldYOffset,
         param.clientLevelWrapper
      );
   }

   public void update(
      EDhApiRenderPass renderPass,
      float newPartialTicks,
      float nearClipPlane,
      float farClipPlane,
      DhApiMat4f newMcProjectionMatrix,
      DhApiMat4f newMcModelViewMatrix,
      DhApiMat4f newDhProjectionMatrix,
      DhApiMat4f newDhModelViewMatrix,
      int worldYOffset,
      IDhApiLevelWrapper clientLevelWrapper
   ) {
      this.renderPass = renderPass;
      this.partialTicks = newPartialTicks;
      this.farClipPlane = farClipPlane;
      this.nearClipPlane = nearClipPlane;
      this.mcProjectionMatrix.set(newMcProjectionMatrix);
      this.mcModelViewMatrix.set(newMcModelViewMatrix);
      this.mcInverseMvmProjectionMatrix.set(newMcProjectionMatrix);
      this.mcInverseMvmProjectionMatrix.invert();
      this.dhProjectionMatrix.set(newDhProjectionMatrix);
      this.dhModelViewMatrix.set(newDhModelViewMatrix);
      this.dhMvmProjMatrix.set(this.dhProjectionMatrix);
      this.dhMvmProjMatrix.multiply(this.dhModelViewMatrix);
      this.dhInverseMvmProjectionMatrix.set(this.dhMvmProjMatrix);
      this.dhInverseMvmProjectionMatrix.invert();
      this.worldYOffset = worldYOffset;
      this.clientLevelWrapper = clientLevelWrapper;
   }

   @Override
   public boolean getCopyBeforeFire() {
      return false;
   }

   public DhApiRenderParam copy() {
      return this;
   }
}
