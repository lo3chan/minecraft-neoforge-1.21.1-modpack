package com.github.alexthe666.alexsmobs.citadel.client.event;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class EventPosePlayerHand extends Event {
   private final LivingEntity entityIn;
   private final HumanoidModel model;
   private final boolean left;
   private boolean handled;

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

   public boolean isHandled() {
      return this.handled;
   }

   public void setHandled(boolean handled) {
      this.handled = handled;
   }

   public void post() {
      NeoForge.EVENT_BUS.post(this);
   }
}
