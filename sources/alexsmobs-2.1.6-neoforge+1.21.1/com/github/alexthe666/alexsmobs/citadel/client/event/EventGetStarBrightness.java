package com.github.alexthe666.alexsmobs.citadel.client.event;

import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class EventGetStarBrightness extends Event {
   private final ClientLevel clientLevel;
   private float brightness;
   private final float partialTicks;
   private boolean handled;

   public EventGetStarBrightness(ClientLevel clientLevel, float brightness, float partialTicks) {
      this.clientLevel = clientLevel;
      this.brightness = brightness;
      this.partialTicks = partialTicks;
   }

   public ClientLevel getLevel() {
      return this.clientLevel;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public float getBrightness() {
      return this.brightness;
   }

   public void setBrightness(float brightness) {
      this.brightness = brightness;
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
