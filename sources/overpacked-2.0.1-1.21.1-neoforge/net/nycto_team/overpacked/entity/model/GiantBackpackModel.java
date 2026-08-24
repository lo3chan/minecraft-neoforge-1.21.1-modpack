package net.nycto_team.overpacked.entity.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.nycto_team.overpacked.entity.GiantBackpack;
import net.nycto_team.overpacked.entity.animation.GiantBackpackAnim;

@OnlyIn(Dist.CLIENT)
public class GiantBackpackModel<T extends GiantBackpack> extends HierarchicalModel<T> {
   private final ModelPart model;
   private final ModelPart right_cell;
   private final ModelPart left_cell;

   public GiantBackpackModel(ModelPart root) {
      this.model = root.getChild("model");
      ModelPart body = this.model.getChild("body");
      this.right_cell = body.getChild("right_cell");
      this.left_cell = body.getChild("left_cell");
   }

   public ModelPart root() {
      return this.model;
   }

   public static LayerDefinition model() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition model = partdefinition.addOrReplaceChild("model", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition body = model.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-8.0F, -12.0F, -13.0F, 16.0F, 8.0F, 13.0F, new CubeDeformation(0.0F))
            .texOffs(50, 42)
            .addBox(-8.0F, -4.0F, -10.0F, 16.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 45)
            .mirror()
            .addBox(7.75F, -17.0F, -9.01F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.001F))
            .mirror(false)
            .texOffs(0, 45)
            .addBox(-7.75F, -17.0F, -9.01F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.001F))
            .texOffs(50, 56)
            .addBox(-4.0F, -20.0F, 0.5F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
         PartPose.offset(0.0F, 0.0F, 5.0F)
      );
      PartDefinition big_cell = body.addOrReplaceChild(
         "big_cell",
         CubeListBuilder.create()
            .texOffs(0, 21)
            .addBox(-8.0F, -8.0F, -13.0F, 16.0F, 8.0F, 13.0F, new CubeDeformation(0.01F))
            .texOffs(58, 25)
            .addBox(-8.0F, -8.0F, -13.0F, 16.0F, 8.0F, 9.0F, new CubeDeformation(0.25F)),
         PartPose.offset(0.0F, -12.0F, 0.0F)
      );
      PartDefinition sleeping_bag = big_cell.addOrReplaceChild(
         "sleeping_bag",
         CubeListBuilder.create().texOffs(0, 77).addBox(-10.0F, -6.0F, -3.0F, 20.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -8.0F, -3.0F)
      );
      PartDefinition arch = big_cell.addOrReplaceChild(
         "arch",
         CubeListBuilder.create().texOffs(45, 0).addBox(-6.0F, -5.0F, 0.0F, 12.0F, 5.0F, 0.0F, new CubeDeformation(0.001F)),
         PartPose.offset(0.0F, -8.0F, -3.9F)
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
      PartDefinition right_cell = body.addOrReplaceChild(
         "right_cell",
         CubeListBuilder.create().texOffs(59, 0).addBox(0.0F, -2.0F, -5.0F, 5.0F, 15.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(8.0F, -17.0F, -5.0F)
      );
      PartDefinition right_cell_rot = right_cell.addOrReplaceChild(
         "right_cell_rot",
         CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, -3.0F, -5.0F, 6.0F, 8.0F, 10.0F, new CubeDeformation(0.025F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_cell = body.addOrReplaceChild(
         "left_cell",
         CubeListBuilder.create().texOffs(59, 0).mirror().addBox(-5.0F, -2.0F, -5.0F, 5.0F, 15.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(-8.0F, -17.0F, -5.0F)
      );
      PartDefinition left_cell_rot = left_cell.addOrReplaceChild(
         "left_cell_rot",
         CubeListBuilder.create().texOffs(0, 59).mirror().addBox(-6.0F, -3.0F, -5.0F, 6.0F, 8.0F, 10.0F, new CubeDeformation(0.025F)).mirror(false),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(T entity, float limb_swing, float limb_swing_amount, float age_in_ticks, float net_head_yaw, float head_pitch) {
   }

   public void SetupAnim(T entity, float age_in_ticks) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      this.right_cell.visible = entity.get_right_cell() != 0;
      this.left_cell.visible = entity.get_left_cell() != 0;
      this.animate(entity.big_cell_open_anim_state, GiantBackpackAnim.big_cell_open, age_in_ticks);
      this.animate(entity.big_cell_close_anim_state, GiantBackpackAnim.big_cell_close, age_in_ticks);
      this.animate(entity.right_cell_open_anim_state, GiantBackpackAnim.right_cell_open, age_in_ticks);
      this.animate(entity.right_cell_close_anim_state, GiantBackpackAnim.right_cell_close, age_in_ticks);
      this.animate(entity.left_cell_open_anim_state, GiantBackpackAnim.left_cell_open, age_in_ticks);
      this.animate(entity.left_cell_close_anim_state, GiantBackpackAnim.left_cell_close, age_in_ticks);
   }
}
