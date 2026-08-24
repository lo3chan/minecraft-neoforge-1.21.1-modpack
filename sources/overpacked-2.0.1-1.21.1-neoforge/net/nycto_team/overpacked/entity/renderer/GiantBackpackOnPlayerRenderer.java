package net.nycto_team.overpacked.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.nycto_team.overpacked.entity.layer.GiantBackpackSleepingBagLayer;
import net.nycto_team.overpacked.entity.model.GiantBackpackOnPlayerModel;
import net.nycto_team.overpacked.item.GiantBackpackItem;
import net.nycto_team.overpacked.registry.ModModelLayers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class GiantBackpackOnPlayerRenderer implements ICurioRenderer {
   GiantBackpackOnPlayerModel model = null;
   GiantBackpackOnPlayerModel sleeping_bag = null;

   public <T extends LivingEntity, M extends EntityModel<T>> void render(
      ItemStack stack,
      SlotContext ctx,
      PoseStack pose,
      RenderLayerParent<T, M> parent,
      MultiBufferSource buffer,
      int light,
      float limb_swing,
      float limb_swing_amount,
      float partialTicks,
      float age_in_ticks,
      float net_head_yaw,
      float head_pitch
   ) {
      LivingEntity entity = ctx.entity();
      if (this.model == null) {
         this.model = new GiantBackpackOnPlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.giant_backpack_on_player));
      }

      ICurioRenderer.followBodyRotations(entity, new HumanoidModel[]{this.model});
      this.model.setupAnim(entity, limb_swing, limb_swing_amount, age_in_ticks, net_head_yaw, head_pitch);
      this.model
         .renderToBuffer(
            pose,
            buffer.getBuffer(RenderType.entityCutoutNoCull(GiantBackpackRenderer.textures[((GiantBackpackItem)stack.getItem()).color + 1])),
            light,
            OverlayTexture.NO_OVERLAY
         );
      CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
      if (data == null) {
         this.model.UpdateCells(false, false);
      } else {
         CompoundTag tag = data.copyTag();
         this.model.UpdateCells(tag.contains("RightCell"), tag.contains("LeftCell"));
         if (tag.contains("SleepingBagColor")) {
            if (this.sleeping_bag == null) {
               this.sleeping_bag = this.model;
            }

            this.sleeping_bag
               .renderToBuffer(
                  pose,
                  buffer.getBuffer(RenderType.entityCutoutNoCull(GiantBackpackSleepingBagLayer.textures[tag.getInt("SleepingBagColor")])),
                  light,
                  OverlayTexture.NO_OVERLAY
               );
         }
      }
   }
}
