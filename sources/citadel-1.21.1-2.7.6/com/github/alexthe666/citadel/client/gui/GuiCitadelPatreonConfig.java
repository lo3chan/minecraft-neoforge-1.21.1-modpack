package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.rewards.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.neoforged.neoforge.network.PacketDistributor;

public class GuiCitadelPatreonConfig extends OptionsSubScreen {
   private ExtendedSlider distSlider;
   private ExtendedSlider speedSlider;
   private ExtendedSlider heightSlider;
   private Button changeButton;
   private float rotateDist;
   private float rotateSpeed;
   private float rotateHeight;
   private String followType;

   public GuiCitadelPatreonConfig(Screen parentScreenIn, Options gameSettingsIn) {
      super(parentScreenIn, gameSettingsIn, Component.translatable("citadel.gui.patreon_customization"));
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
      float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2.0F;
      float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1.0F;
      float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1.0F;
      this.rotateDist = roundTo(distance, 3);
      this.rotateSpeed = roundTo(speed, 3);
      this.rotateHeight = roundTo(height, 3);
      this.followType = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
   }

   private void setSliderValue(int i, float sliderValue) {
      boolean flag = false;
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
      if (i == 0) {
         this.rotateDist = roundTo(sliderValue, 3);
         tag.putFloat("CitadelRotateDistance", this.rotateDist);
      } else if (i == 1) {
         this.rotateSpeed = roundTo(sliderValue, 3);
         tag.putFloat("CitadelRotateSpeed", this.rotateSpeed);
      } else {
         this.rotateHeight = roundTo(sliderValue, 3);
         tag.putFloat("CitadelRotateHeight", this.rotateHeight);
      }

      CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
      PacketDistributor.sendToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getId()), new CustomPacketPayload[0]);
   }

   public static float roundTo(float value, int places) {
      return value;
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
   }

   protected void init() {
      super.init();
      int i = this.width / 2;
      int j = this.height / 6;
      Button doneButton = Button.builder(CommonComponents.GUI_DONE, p_213079_1_ -> this.minecraft.setScreen(this.lastScreen))
         .size(200, 20)
         .pos(i - 100, j + 120)
         .build();
      this.addRenderableWidget(doneButton);
      this.addRenderableWidget(
         this.distSlider = new ExtendedSlider(
            i - 75 - 25,
            j + 30,
            150,
            20,
            Component.translatable("citadel.gui.orbit_dist").append(Component.translatable(": ")),
            Component.translatable(""),
            0.125,
            5.0,
            this.rotateDist,
            0.1,
            1,
            true
         ) {
            protected void applyValue() {
               GuiCitadelPatreonConfig.this.setSliderValue(0, (float)this.getValue());
            }
         }
      );
      Button reset1Button = Button.builder(Component.translatable("citadel.gui.reset"), p_213079_1_ -> this.setSliderValue(0, 0.4F))
         .size(40, 20)
         .pos(i - 75 + 135, j + 30)
         .build();
      this.addRenderableWidget(reset1Button);
      this.addRenderableWidget(
         this.speedSlider = new ExtendedSlider(
            i - 75 - 25,
            j + 60,
            150,
            20,
            Component.translatable("citadel.gui.orbit_speed").append(Component.translatable(": ")),
            Component.translatable(""),
            0.0,
            5.0,
            this.rotateSpeed,
            0.1,
            2,
            true
         ) {
            protected void applyValue() {
               GuiCitadelPatreonConfig.this.setSliderValue(1, (float)this.getValue());
            }
         }
      );
      Button reset2Button = Button.builder(Component.translatable("citadel.gui.reset"), p_213079_1_ -> this.setSliderValue(1, 0.2F))
         .size(40, 20)
         .pos(i - 75 + 135, j + 60)
         .build();
      this.addRenderableWidget(reset2Button);
      this.addRenderableWidget(
         this.heightSlider = new ExtendedSlider(
            i - 75 - 25,
            j + 90,
            150,
            20,
            Component.translatable("citadel.gui.orbit_height").append(Component.translatable(": ")),
            Component.translatable(""),
            0.0,
            2.0,
            this.rotateHeight,
            0.1,
            2,
            true
         ) {
            protected void applyValue() {
               GuiCitadelPatreonConfig.this.setSliderValue(2, (float)this.getValue());
            }
         }
      );
      Button reset3Button = Button.builder(Component.translatable("citadel.gui.reset"), p_213079_1_ -> this.setSliderValue(2, 0.5F))
         .size(40, 20)
         .pos(i - 75 + 135, j + 90)
         .build();
      this.addRenderableWidget(reset3Button);
      this.changeButton = Button.builder(this.getTypeText(), p_213079_1_ -> {
         this.followType = CitadelPatreonRenderer.getIdOfNext(this.followType);
         CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
         tag.putString("CitadelFollowerType", this.followType);
         CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
         PacketDistributor.sendToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getId()), new CustomPacketPayload[0]);
         this.changeButton.setMessage(this.getTypeText());
      }).size(200, 20).pos(i - 100, j).build();
      this.addRenderableWidget(this.changeButton);
   }

   protected void addOptions() {
   }

   private Component getTypeText() {
      return Component.translatable("citadel.gui.follower_type").append(Component.translatable("citadel.follower." + this.followType));
   }
}
