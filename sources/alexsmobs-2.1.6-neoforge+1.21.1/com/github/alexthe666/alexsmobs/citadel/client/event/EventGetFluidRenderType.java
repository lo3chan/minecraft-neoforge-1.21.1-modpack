package com.github.alexthe666.alexsmobs.citadel.client.event;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class EventGetFluidRenderType extends Event {
   private final FluidState fluidState;
   private RenderType renderType;
   private boolean handled;

   public FluidState getFluidState() {
      return this.fluidState;
   }

   public EventGetFluidRenderType(FluidState fluidState, RenderType renderType) {
      this.fluidState = fluidState;
      this.renderType = renderType;
   }

   public RenderType getRenderType() {
      return this.renderType;
   }

   public void setRenderType(RenderType renderType) {
      this.renderType = renderType;
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
