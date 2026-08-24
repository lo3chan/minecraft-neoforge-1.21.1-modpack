package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.alexsmobs.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.alexsmobs.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import com.google.common.collect.ImmutableList;

public class ModelBoneSerpentTail extends AdvancedEntityModel<EntityBoneSerpentPart> {
   private final AdvancedModelBox root;
   private final AdvancedModelBox tail;

   public ModelBoneSerpentTail() {
      this.texWidth = 64;
      this.texHeight = 64;
      this.root = new AdvancedModelBox(this, "root");
      this.root.setPos(0.0F, 24.0F, 0.0F);
      this.tail = new AdvancedModelBox(this, "tail");
      this.tail.setPos(0.0F, -4.75F, 0.0F);
      this.root.addChild(this.tail);
      this.tail.setTextureOffset(0, 0).addBox(-5.0F, -5.25F, -8.0F, 10.0F, 10.0F, 16.0F, 0.0F, false);
      this.tail.setTextureOffset(0, 27).addBox(-1.0F, -6.25F, -8.0F, 2.0F, 1.0F, 16.0F, 0.0F, false);
      this.updateDefaultPose();
   }

   public void setupAnim(EntityBoneSerpentPart entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.resetToDefaultPose();
      this.tail.rotateAngleX = headPitch * 0.017453292F;
      this.tail.rotateAngleY = netHeadYaw * 0.017453292F;
      float walkSpeed = 0.35F;
      float walkDegree = 3.0F;
      float idleDegree = 0.7F;
      float idleSpeed = 0.2F;
      double walkOffset = entityIn.getBodyIndex() + 1;
      this.tail.rotationPointY = this.tail.rotationPointY
         + (float)(Math.sin(limbSwing * walkSpeed - walkOffset) * limbSwingAmount * walkDegree - limbSwingAmount * walkDegree);
      this.tail.rotationPointY = this.tail.rotationPointY + (float)(Math.sin(ageInTicks * idleSpeed - walkOffset) * 1.0 * idleDegree - 1.0F * idleDegree);
   }

   @Override
   public Iterable<BasicModelPart> parts() {
      return ImmutableList.of(this.root);
   }

   @Override
   public Iterable<AdvancedModelBox> getAllParts() {
      return ImmutableList.of(this.root, this.tail);
   }
}
