package net.nycto_team.overpacked.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.nycto_team.overpacked.entity.GiantBackpack;
import net.nycto_team.overpacked.entity.layer.GiantBackpackSleepingBagLayer;
import net.nycto_team.overpacked.entity.model.GiantBackpackModel;
import net.nycto_team.overpacked.registry.ModModelLayers;
import net.nycto_team.overpacked.util.ModLoc;

@OnlyIn(Dist.CLIENT)
public class GiantBackpackRenderer<T extends GiantBackpack, M extends GiantBackpackModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
   public static final ResourceLocation[] textures = new ResourceLocation[]{
      ModLoc.get("textures/entity/giant_backpack/giant_backpack.png"),
      ModLoc.get("textures/entity/giant_backpack/white.png"),
      ModLoc.get("textures/entity/giant_backpack/orange.png"),
      ModLoc.get("textures/entity/giant_backpack/magenta.png"),
      ModLoc.get("textures/entity/giant_backpack/light_blue.png"),
      ModLoc.get("textures/entity/giant_backpack/yellow.png"),
      ModLoc.get("textures/entity/giant_backpack/lime.png"),
      ModLoc.get("textures/entity/giant_backpack/pink.png"),
      ModLoc.get("textures/entity/giant_backpack/gray.png"),
      ModLoc.get("textures/entity/giant_backpack/light_gray.png"),
      ModLoc.get("textures/entity/giant_backpack/cyan.png"),
      ModLoc.get("textures/entity/giant_backpack/purple.png"),
      ModLoc.get("textures/entity/giant_backpack/blue.png"),
      ModLoc.get("textures/entity/giant_backpack/brown.png"),
      ModLoc.get("textures/entity/giant_backpack/green.png"),
      ModLoc.get("textures/entity/giant_backpack/red.png"),
      ModLoc.get("textures/entity/giant_backpack/black.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/maroon.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/rose.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/coral.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/indigo.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/navy.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/slate.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/olive.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/amber.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/beige.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/teal.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/mint.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/aqua.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/verdant.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/forest.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/ginger.png"),
      ModLoc.get("textures/entity/giant_backpack/dye_depot/tan.png")
   };
   private final M model;
   protected RenderLayer<T, M> layer;

   public GiantBackpackRenderer(Context ctx) {
      super(ctx);
      this.model = (M)(new GiantBackpackModel(ctx.bakeLayer(ModModelLayers.giant_backpack)));
      this.layer = new GiantBackpackSleepingBagLayer<>(this);
   }

   public ResourceLocation getTextureLocation(GiantBackpack entity) {
      return textures[entity.get_color() + 1];
   }

   public void render(T entity, float yaw, float partial_tick, PoseStack pose, MultiBufferSource buffer, int light) {
      this.model.SetupAnim(entity, entity.tickCount + partial_tick);
      pose.pushPose();
      pose.mulPose(Axis.XP.rotationDegrees(180.0F));
      float i_yaw = Mth.lerp(partial_tick, entity.yRotO, entity.getYRot());
      pose.mulPose(Axis.YP.rotationDegrees(i_yaw));
      float f0 = entity.get_hurt_time() - partial_tick;
      float f1 = entity.get_damage() - partial_tick;
      if (f1 < 0.0F) {
         f1 = 0.0F;
      }

      if (f0 > 0.0F) {
         pose.mulPose(Axis.ZP.rotationDegrees(Mth.sin(f0) * f1 / 10.0F * entity.get_hurt_dir()));
      }

      pose.translate(0.0F, -1.5F, 0.0F);
      this.model.renderToBuffer(pose, buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
      if (entity.get_sleeping_bag_color() != -1) {
         this.layer.render(pose, buffer, light, entity, 0.0F, 0.0F, partial_tick, 0.0F, 0.0F, 0.0F);
      }

      super.render(entity, yaw, partial_tick, pose, buffer, light);
      pose.popPose();
   }

   public M getModel() {
      return this.model;
   }
}
