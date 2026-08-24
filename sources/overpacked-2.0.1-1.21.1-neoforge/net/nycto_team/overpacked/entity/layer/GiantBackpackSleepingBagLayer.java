package net.nycto_team.overpacked.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.nycto_team.overpacked.entity.GiantBackpack;
import net.nycto_team.overpacked.entity.model.GiantBackpackModel;
import net.nycto_team.overpacked.util.ModLoc;

@OnlyIn(Dist.CLIENT)
public class GiantBackpackSleepingBagLayer<T extends GiantBackpack, M extends GiantBackpackModel<T>> extends RenderLayer<T, M> {
   public static final ResourceLocation[] textures = new ResourceLocation[]{
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/sleeping_bag.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/white.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/orange.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/magenta.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/light_blue.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/yellow.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/lime.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/pink.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/gray.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/light_gray.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/cyan.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/purple.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/blue.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/brown.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/green.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/red.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/black.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/maroon.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/rose.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/coral.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/indigo.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/navy.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/slate.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/olive.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/amber.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/beige.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/teal.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/mint.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/aqua.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/verdant.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/forest.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/ginger.png"),
      ModLoc.get("textures/entity/giant_backpack/sleeping_bag/dye_depot/tan.png")
   };

   public GiantBackpackSleepingBagLayer(RenderLayerParent<T, M> renderer) {
      super(renderer);
   }

   public void render(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      T entity,
      float limb_swing,
      float limb_swing_amount,
      float partial_ticks,
      float age_in_ticks,
      float net_head_yaw,
      float head_pitch
   ) {
      ((GiantBackpackModel)this.getParentModel())
         .renderToBuffer(pose, buffer.getBuffer(RenderType.entityCutoutNoCull(textures[entity.get_sleeping_bag_color()])), light, OverlayTexture.NO_OVERLAY);
   }
}
