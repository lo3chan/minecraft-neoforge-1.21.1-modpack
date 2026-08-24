package com.github.alexthe666.citadel.client.event;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.util.TriState;

public class EventPosePlayerHand extends Event {
   private LivingEntity entityIn;
   private HumanoidModel model;
   private boolean left;
   private TriState result = TriState.DEFAULT;

   public EventPosePlayerHand(LivingEntity entityIn, HumanoidModel model, boolean left) {
      this.entityIn = entityIn;
      this.model = model;
      this.left = left;
   }

   public Entity getEntityIn() {
      return this.entityIn;
   }

   public HumanoidModel getModel() {
      return this.model;
   }

   public boolean isLeftHand() {
      return this.left;
   }

   public void setResult(TriState result) {
      this.result = result;
   }

   public TriState getResult() {
      return this.result;
   }
}
