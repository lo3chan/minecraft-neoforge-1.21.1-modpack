package com.github.alexthe666.alexsmobs.citadel.client.event;

import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class EventGetOutlineColor extends Event {
   private Entity entityIn;
   private int color;
   private boolean handled;

   public EventGetOutlineColor(Entity entityIn, int color) {
      this.entityIn = entityIn;
      this.color = color;
   }

   public Entity getEntityIn() {
      return this.entityIn;
   }

   public void setEntityIn(Entity entityIn) {
      this.entityIn = entityIn;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int color) {
      this.color = color;
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
