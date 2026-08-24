package net.nycto_team.overpacked.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantBackpackOnPlayerModel extends HumanoidModel<LivingEntity> {
   private final ModelPart root;
   private final ModelPart m_body;
   private final ModelPart big_cell;
   private final ModelPart right_cell;
   private final ModelPart right_cell_rot;
   private final ModelPart left_cell;
   private final ModelPart left_cell_rot;
   private final ModelPart right_belt;
   private final ModelPart left_belt;

   public GiantBackpackOnPlayerModel(ModelPart root) {
      super(root);
      this.root = root;
      this.m_body = root.getChild("body").getChild("model").getChild("m_body");
      this.big_cell = this.m_body.getChild("big_cell");
      this.right_cell = this.m_body.getChild("right_cell");
      this.right_cell_rot = this.right_cell.getChild("right_cell_rot");
      this.left_cell = this.m_body.getChild("left_cell");
      this.left_cell_rot = this.left_cell.getChild("left_cell_rot");
      this.right_belt = this.big_cell.getChild("right_belt");
      this.left_belt = this.big_cell.getChild("left_belt");
   }

   public static LayerDefinition model() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition head = root.addOrReplaceChild("head", new CubeListBuilder(), PartPose.ZERO);
      PartDefinition hat = root.addOrReplaceChild("hat", new CubeListBuilder(), PartPose.ZERO);
      PartDefinition right_arm = root.addOrReplaceChild("right_arm", new CubeListBuilder(), PartPose.ZERO);
      PartDefinition left_arm = root.addOrReplaceChild("left_arm", new CubeListBuilder(), PartPose.ZERO);
      PartDefinition right_leg = root.addOrReplaceChild("right_leg", new CubeListBuilder(), PartPose.ZERO);
      PartDefinition left_leg = root.addOrReplaceChild("left_leg", new CubeListBuilder(), PartPose.ZERO);
      PartDefinition body = root.addOrReplaceChild("body", new CubeListBuilder(), PartPose.ZERO);
      PartDefinition model = body.addOrReplaceChild(
         "model",
         CubeListBuilder.create().texOffs(50, 56).addBox(-4.0F, -25.0F, -1.5F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
         PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F)
      );
      PartDefinition m_body = model.addOrReplaceChild(
         "m_body",
         CubeListBuilder.create()
            .texOffs(0, 77)
            .addBox(-10.0F, -15.0F, -6.0F, 20.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(45, 0)
            .addBox(-6.0F, -14.0F, -3.9F, 12.0F, 5.0F, 0.0F, new CubeDeformation(0.001F))
            .texOffs(0, 0)
            .addBox(-8.0F, -1.0F, -13.0F, 16.0F, 8.0F, 13.0F, new CubeDeformation(0.0F))
            .texOffs(50, 42)
            .addBox(-8.0F, 7.0F, -10.0F, 16.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 45)
            .addBox(-7.75F, -6.0F, -9.01F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.001F))
            .texOffs(0, 45)
            .mirror()
            .addBox(7.75F, -6.0F, -9.01F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.001F))
            .mirror(false),
         PartPose.offset(0.0F, -16.0F, -2.0F)
      );
      PartDefinition big_cell = m_body.addOrReplaceChild(
         "big_cell",
         CubeListBuilder.create()
            .texOffs(0, 21)
            .addBox(-8.0F, -8.0F, -13.0F, 16.0F, 8.0F, 13.0F, new CubeDeformation(0.01F))
            .texOffs(58, 25)
            .addBox(-8.0F, -8.0F, -13.0F, 16.0F, 8.0F, 9.0F, new CubeDeformation(0.25F)),
         PartPose.offset(0.0F, -1.0F, 0.0F)
      );
      PartDefinition right_belt = big_cell.addOrReplaceChild(
         "right_belt",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.01F)),
         PartPose.offset(5.2F, 0.25F, -13.25F)
      );
      PartDefinition left_belt = big_cell.addOrReplaceChild(
         "left_belt",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.01F)),
         PartPose.offset(-5.2F, 0.25F, -13.25F)
      );
      PartDefinition right_cell = m_body.addOrReplaceChild(
         "right_cell",
         CubeListBuilder.create().texOffs(59, 0).addBox(0.0F, -2.0F, -5.0F, 5.0F, 15.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(8.0F, -6.0F, -5.0F)
      );
      PartDefinition right_cell_rot = right_cell.addOrReplaceChild(
         "right_cell_rot",
         CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, -3.0F, -5.0F, 6.0F, 8.0F, 10.0F, new CubeDeformation(0.025F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_cell = m_body.addOrReplaceChild(
         "left_cell",
         CubeListBuilder.create().texOffs(59, 0).mirror().addBox(-5.0F, -2.0F, -5.0F, 5.0F, 15.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(-8.0F, -6.0F, -5.0F)
      );
      PartDefinition left_cell_rot = left_cell.addOrReplaceChild(
         "left_cell_rot",
         CubeListBuilder.create().texOffs(0, 59).mirror().addBox(-6.0F, -3.0F, -5.0F, 6.0F, 8.0F, 10.0F, new CubeDeformation(0.025F)).mirror(false),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(mesh, 128, 128);
   }

   public void setupAnim(LivingEntity entity, float limb_swing, float limb_swing_amount, float age_in_ticks, float net_head_yaw, float head_pitch) {
      super.setupAnim(entity, limb_swing, limb_swing_amount, age_in_ticks, net_head_yaw, head_pitch);
      float l = Mth.clamp(0.35F / (Minecraft.getInstance().getFps() / 60.0F), 0.0F, 1.0F);
      this.m_body.xRot = Mth.lerp(l, this.m_body.xRot, this.get_rot(entity, limb_swing, limb_swing_amount, 0.0F) / 7.0F);
      this.m_body.y = Mth.clamp(Mth.lerp(l, this.m_body.y, this.get_rot(entity, limb_swing, limb_swing_amount, 1.0F) * 4.0F - 16.0F), -18.0F, -14.0F);
      this.big_cell.xRot = Mth.lerp(l, this.big_cell.xRot, this.get_rot(entity, limb_swing, limb_swing_amount, 2.9F));
      if (this.right_cell.visible) {
         this.right_cell_rot.zRot = Mth.lerp(l, this.right_cell_rot.zRot, this.get_rot(entity, limb_swing, limb_swing_amount, 2.7F) / 1.5F);
      }

      if (this.left_cell.visible) {
         this.left_cell_rot.zRot = Mth.lerp(l, this.left_cell_rot.zRot, -this.get_rot(entity, limb_swing, limb_swing_amount, 2.3F) / 1.5F);
      }

      this.right_belt.xRot = Mth.clamp(Mth.lerp(l, this.right_belt.xRot, this.get_rot(entity, limb_swing, limb_swing_amount, 1.8F) * 1.2F), -1.0F, 0.0F);
      this.left_belt.xRot = Mth.clamp(Mth.lerp(l, this.left_belt.xRot, this.get_rot(entity, limb_swing, limb_swing_amount, 1.4F)) * 1.2F, -1.0F, 0.0F);
   }

   public void UpdateCells(boolean right, boolean left) {
      this.right_cell.visible = right;
      this.left_cell.visible = left;
   }

   private float get_rot(LivingEntity entity, float limb_swing, float limb_swing_amount, float phase) {
      float f = 1.0F;
      if (entity.getFallFlyingTicks() > 4) {
         f = (float)entity.getDeltaMovement().lengthSqr();
         f /= 0.2F;
         f *= f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      float gv = Mth.cos(limb_swing * 0.67F + phase) * 2.0F * limb_swing_amount * 0.5F / f / 15.0F;
      float fv = Math.min(0.5F, entity.fallDistance / 20.0F);
      return gv * (1.0F - fv) / (entity.onGround() ? 1 : 3) - fv;
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.root.getChild("head"));
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.root.getChild("body"));
   }
}
