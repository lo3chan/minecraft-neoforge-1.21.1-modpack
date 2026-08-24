package com.aetherteam.aether.client.gui.component.inventory;

import com.aetherteam.aether.network.packet.serverbound.SunAltarUpdatePacket;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class SunAltarSlider extends AbstractSliderButton {
   private final int timeScale;

   public SunAltarSlider(int x, int y, int width, int height, Component title, double value, int timeScale) {
      super(x, y, width, height, title, value);
      this.timeScale = timeScale;
   }

   protected void applyValue() {
      long time = (long)(this.value * this.timeScale);
      PacketDistributor.sendToServer(new SunAltarUpdatePacket(time, this.timeScale), new CustomPacketPayload[0]);
   }

   protected void updateMessage() {
   }
}
