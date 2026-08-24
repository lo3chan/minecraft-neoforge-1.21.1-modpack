package net.diebuddies.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;

public class EggItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
   private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;
   private final ItemRenderer itemRenderer;
   private final float scale;
   private final boolean fullBright;

   public EggItemRenderer(Context context, float f, boolean bl) {
      super(context);
      this.itemRenderer = context.getItemRenderer();
      this.scale = f;
      this.fullBright = bl;
   }

   public EggItemRenderer(Context context) {
      this(context, 1.0F, false);
   }

   public int getBlockLightLevel(T entity, BlockPos blockPos) {
      return this.fullBright ? 15 : super.getBlockLightLevel(entity, blockPos);
   }

   public void render(T entity, float animationProgress, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
      if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
         if (ConfigClient.eggModel != 2) {
            RenderHelper.renderMesh(
               entity,
               tickDelta,
               multiBufferSource,
               this.entityRenderDispatcher,
               PhysicsMod.EGG_TEXTURE,
               PhysicsMod.eggMesh.get(ConfigClient.eggModel),
               poseStack,
               light,
               OverlayTexture.NO_OVERLAY,
               ConfigClient.eggShade
            );
         } else {
            poseStack.pushPose();
            poseStack.scale(this.scale, this.scale, this.scale);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            this.itemRenderer
               .renderStatic(
                  entity.getItem(), ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, entity.level(), entity.getId()
               );
            poseStack.popPose();
         }

         super.render(entity, animationProgress, tickDelta, poseStack, multiBufferSource, light);
      }
   }

   public ResourceLocation getTextureLocation(Entity entity) {
      return ConfigClient.eggModel != 2 ? PhysicsMod.EGG_TEXTURE : TextureAtlas.LOCATION_BLOCKS;
   }
}
