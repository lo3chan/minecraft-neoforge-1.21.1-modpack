package dev.tr7zw.entityculling.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.NMSCullingHelper;
import dev.tr7zw.entityculling.access.EntityRendererInter;
import dev.tr7zw.entityculling.versionless.access.Cullable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class WorldRendererMixin {
   private EntityRenderDispatcher entityCulling$entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
   private List<Runnable> lateRenders = new ArrayList<>();
   private double aabbExpansion = 0.5;

   @Inject(
      at = {@At("HEAD")},
      method = {"renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"},
      cancellable = true
   )
   private void renderEntity(
      Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo info
   ) {
      if (!EntityCullingModBase.instance.config.skipEntityCulling) {
         Cullable cullable = (Cullable)entity;
         if (!cullable.isForcedVisible() && cullable.isCulled() && !NMSCullingHelper.ignoresCulling(entity)) {
            EntityRenderer x = this.entityCulling$entityRenderDispatcher.getRenderer(entity);
            if (x instanceof EntityRenderer && x instanceof EntityRendererInter entityRendererInter) {
               if (EntityCullingModBase.instance.config.renderNametagsThroughWalls
                  && matrices != null
                  && vertexConsumers != null
                  && entityRendererInter.shadowShouldShowName(entity)) {
                  double xx = Mth.lerp(tickDelta, entity.xOld, entity.getX()) - cameraX;
                  double y = Mth.lerp(tickDelta, entity.yOld, entity.getY()) - cameraY;
                  double z = Mth.lerp(tickDelta, entity.zOld, entity.getZ()) - cameraZ;
                  Vec3 vec3d = NMSCullingHelper.getRenderOffset(x, entity, tickDelta);
                  double d = xx + vec3d.x;
                  double e = y + vec3d.y;
                  double f = z + vec3d.z;
                  matrices.pushPose();
                  matrices.translate(d, e, f);
                  entityRendererInter.shadowRenderNameTag(
                     entity,
                     entity.getDisplayName(),
                     matrices,
                     vertexConsumers,
                     this.entityCulling$entityRenderDispatcher.getPackedLightCoords(entity, tickDelta),
                     tickDelta
                  );
                  matrices.popPose();
               }

               EntityCullingModBase.instance.skippedEntities++;
               info.cancel();
               return;
            }
         }

         EntityCullingModBase.instance.renderedEntities++;
         cullable.setOutOfCamera(false);
      }
   }
}
