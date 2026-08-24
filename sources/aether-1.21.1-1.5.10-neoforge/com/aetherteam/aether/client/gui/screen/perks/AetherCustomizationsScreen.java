package com.aetherteam.aether.client.gui.screen.perks;

import com.aetherteam.aether.client.gui.component.customization.ColorBox;
import com.aetherteam.aether.client.gui.component.customization.CustomizationButton;
import com.aetherteam.aether.client.gui.component.customization.DeveloperGlowColorBox;
import com.aetherteam.aether.client.gui.component.customization.DeveloperGlowCustomizationButton;
import com.aetherteam.aether.client.gui.component.customization.HaloColorBox;
import com.aetherteam.aether.client.gui.component.customization.HaloCustomizationButton;
import com.aetherteam.aether.network.packet.serverbound.ServerDeveloperGlowPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerHaloPacket;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.aether.perk.PerkUtil;
import com.aetherteam.aether.perk.types.DeveloperGlow;
import com.aetherteam.aether.perk.types.Halo;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData.Client;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class AetherCustomizationsScreen extends Screen {
   public static final WidgetSprites SAVE_BUTTON = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("aether", "customization/save_button"),
      ResourceLocation.fromNamespaceAndPath("aether", "customization/save_button_disabled"),
      ResourceLocation.fromNamespaceAndPath("aether", "customization/save_button_highlighted")
   );
   public static final WidgetSprites UNDO_BUTTON = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("aether", "customization/undo_button"),
      ResourceLocation.fromNamespaceAndPath("aether", "customization/undo_button_disabled"),
      ResourceLocation.fromNamespaceAndPath("aether", "customization/undo_button_highlighted")
   );
   private final Screen lastScreen;
   private final CustomizationsOptions customizations = CustomizationsOptions.INSTANCE;
   private Button haloToggleButton;
   private ColorBox haloColorBox;
   private Button developerGlowToggleButton;
   private ColorBox developerGlowColorBox;
   public boolean haloEnabled;
   public String haloColor;
   public boolean developerGlowEnabled;
   public String developerGlowColor;

   public AetherCustomizationsScreen(Screen lastScreen) {
      super(Component.translatable("gui.aether.customization.title"));
      this.lastScreen = lastScreen;
   }

   protected void init() {
      this.customizations.load();
      this.haloEnabled = this.customizations.isHaloEnabled();
      this.haloColor = this.customizations.getHaloHex();
      this.developerGlowEnabled = this.customizations.isDeveloperGlowEnabled();
      this.developerGlowColor = this.customizations.getDeveloperGlowHex();
      if (this.getMinecraft().player != null) {
         User user = Client.getClientUser();
         boolean hasHalo = user != null && PerkUtil.hasHalo().test(user);
         boolean hasDeveloperGlow = user != null && PerkUtil.hasDeveloperGlow().test(user);
         int buttonCount = 0;
         if (hasHalo) {
            buttonCount++;
         }

         if (hasDeveloperGlow) {
            buttonCount++;
         }

         int xPos = this.width / 2 - 65;
         int yPos = this.height / 2 - 10 - (int)(buttonCount * 12.5);
         int i = 0;
         if (hasHalo) {
            this.setupHaloOptions(xPos, yPos, ++i);
         }

         if (hasDeveloperGlow) {
            this.setupDeveloperGlowOptions(xPos, yPos, ++i);
         }
      }

      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, pressed -> this.onClose()).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build()
      );
   }

   private void setupHaloOptions(int xPos, int yPos, int i) {
      this.haloToggleButton = (Button)this.addRenderableWidget(
         Button.builder(Component.translatable(this.haloEnabled ? "gui.aether.customization.halo.on" : "gui.aether.customization.halo.off"), pressed -> {
            this.haloEnabled = !this.haloEnabled;
            pressed.setMessage(Component.translatable(this.haloEnabled ? "gui.aether.customization.halo.on" : "gui.aether.customization.halo.off"));
         }).pos(xPos, yPos + 25 * i).build()
      );
      this.haloColorBox = (ColorBox)this.addRenderableWidget(
         new HaloColorBox(this, this.getMinecraft().font, xPos + 155, yPos + 25 * i, 60, 20, Component.translatable("gui.aether.customization.halo.color"))
      );
      if (this.haloColor != null && !this.haloColor.isEmpty()) {
         this.haloColorBox.setValue(this.haloColor);
      }

      Component undoText = Component.translatable("gui.aether.customization.undo");
      HaloCustomizationButton undoButton = new HaloCustomizationButton(
         this,
         CustomizationButton.ButtonType.UNDO,
         this.haloColorBox,
         xPos + 220,
         yPos + 25 * i,
         20,
         20,
         UNDO_BUTTON,
         pressed -> {
            if (pressed.isActive()) {
               this.haloEnabled = this.customizations.isHaloEnabled();
               this.haloColor = this.customizations.getHaloHex();
               this.haloToggleButton
                  .setMessage(Component.translatable(this.haloEnabled ? "gui.aether.customization.halo.on" : "gui.aether.customization.halo.off"));
               if (this.haloColor != null && !this.haloColor.isEmpty()) {
                  this.haloColorBox.setValue(this.haloColor);
               } else {
                  this.haloColorBox.setValue("");
               }

               this.customizations.load();
            }
         },
         undoText
      );
      undoButton.setTooltip(Tooltip.create(undoText));
      this.addRenderableWidget(undoButton);
      Component saveText = Component.translatable("gui.aether.customization.save");
      HaloCustomizationButton saveButton = new HaloCustomizationButton(
         this,
         CustomizationButton.ButtonType.SAVE,
         this.haloColorBox,
         xPos + 245,
         yPos + 25 * i,
         20,
         20,
         SAVE_BUTTON,
         pressed -> {
            if (pressed.isActive()) {
               if (this.haloColorBox.hasValidColor() && this.haloColorBox.hasTextChanged()) {
                  this.customizations.setHaloColor(this.haloColorBox.getValue());
                  this.haloColor = this.customizations.getHaloHex();
               }

               if (this.haloEnabled != this.customizations.isHaloEnabled()) {
                  this.customizations.setIsHaloEnabled(this.haloEnabled);
                  this.haloEnabled = this.customizations.isHaloEnabled();
               }

               if (this.haloEnabled) {
                  if (this.getMinecraft().player != null) {
                     PacketDistributor.sendToServer(
                        new ServerHaloPacket.Apply(this.getMinecraft().player.getUUID(), new Halo(this.haloColor)), new CustomPacketPayload[0]
                     );
                  }
               } else if (this.getMinecraft().player != null) {
                  PacketDistributor.sendToServer(new ServerHaloPacket.Remove(this.getMinecraft().player.getUUID()), new CustomPacketPayload[0]);
               }

               this.customizations.save();
               this.customizations.load();
            }
         },
         saveText
      );
      saveButton.setTooltip(Tooltip.create(saveText));
      this.addRenderableWidget(saveButton);
   }

   private void setupDeveloperGlowOptions(int xPos, int yPos, int i) {
      this.developerGlowToggleButton = (Button)this.addRenderableWidget(
         Button.builder(
               Component.translatable(this.developerGlowEnabled ? "gui.aether.customization.developer_glow.on" : "gui.aether.customization.developer_glow.off"),
               pressed -> {
                  this.developerGlowEnabled = !this.developerGlowEnabled;
                  pressed.setMessage(
                     Component.translatable(
                        this.developerGlowEnabled ? "gui.aether.customization.developer_glow.on" : "gui.aether.customization.developer_glow.off"
                     )
                  );
               }
            )
            .pos(xPos, yPos + 25 * i)
            .build()
      );
      this.developerGlowColorBox = (ColorBox)this.addRenderableWidget(
         new DeveloperGlowColorBox(
            this, this.getMinecraft().font, xPos + 155, yPos + 25 * i, 60, 20, Component.translatable("gui.aether.customization.developer_glow.color")
         )
      );
      if (this.developerGlowColor != null && !this.developerGlowColor.isEmpty()) {
         this.developerGlowColorBox.setValue(this.developerGlowColor);
      }

      Component undoText = Component.translatable("gui.aether.customization.undo");
      DeveloperGlowCustomizationButton undoButton = new DeveloperGlowCustomizationButton(
         this,
         CustomizationButton.ButtonType.UNDO,
         this.developerGlowColorBox,
         xPos + 220,
         yPos + 25 * i,
         20,
         20,
         UNDO_BUTTON,
         pressed -> {
            if (pressed.isActive()) {
               this.developerGlowEnabled = this.customizations.isDeveloperGlowEnabled();
               this.developerGlowColor = this.customizations.getDeveloperGlowHex();
               this.developerGlowToggleButton
                  .setMessage(
                     Component.translatable(
                        this.developerGlowEnabled ? "gui.aether.customization.developer_glow.on" : "gui.aether.customization.developer_glow.off"
                     )
                  );
               if (this.developerGlowColor != null && !this.developerGlowColor.isEmpty()) {
                  this.developerGlowColorBox.setValue(this.developerGlowColor);
               } else {
                  this.developerGlowColorBox.setValue("");
               }

               this.customizations.load();
            }
         },
         undoText
      );
      undoButton.setTooltip(Tooltip.create(undoText));
      this.addRenderableWidget(undoButton);
      Component saveText = Component.translatable("gui.aether.customization.save");
      DeveloperGlowCustomizationButton saveButton = new DeveloperGlowCustomizationButton(
         this,
         CustomizationButton.ButtonType.SAVE,
         this.developerGlowColorBox,
         xPos + 245,
         yPos + 25 * i,
         20,
         20,
         SAVE_BUTTON,
         pressed -> {
            if (pressed.isActive()) {
               if (this.developerGlowColorBox.hasValidColor() && this.developerGlowColorBox.hasTextChanged()) {
                  this.customizations.setDeveloperGlowColor(this.developerGlowColorBox.getValue());
                  this.developerGlowColor = this.customizations.getDeveloperGlowHex();
               }

               if (this.developerGlowEnabled != this.customizations.isDeveloperGlowEnabled()) {
                  this.customizations.setIsDeveloperGlowEnabled(this.developerGlowEnabled);
                  this.developerGlowEnabled = this.customizations.isDeveloperGlowEnabled();
               }

               if (this.developerGlowEnabled) {
                  if (this.getMinecraft().player != null) {
                     PacketDistributor.sendToServer(
                        new ServerDeveloperGlowPacket.Apply(this.getMinecraft().player.getUUID(), new DeveloperGlow(this.developerGlowColor)),
                        new CustomPacketPayload[0]
                     );
                  }
               } else if (this.getMinecraft().player != null) {
                  PacketDistributor.sendToServer(new ServerDeveloperGlowPacket.Remove(this.getMinecraft().player.getUUID()), new CustomPacketPayload[0]);
               }

               this.customizations.save();
               this.customizations.load();
            }
         },
         saveText
      );
      saveButton.setTooltip(Tooltip.create(saveText));
      this.addRenderableWidget(saveButton);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderTransparentBackground(guiGraphics);
      guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, 15, 16777215);
      guiGraphics.drawCenteredString(
         this.font, Component.translatable("gui.aether.customization.color"), this.width / 2 - 65 + 184, this.height / 2 - 10 - 14, 16777215
      );
      if (this.getMinecraft().player != null) {
         int x = this.width / 2 - 175;
         int y = this.height / 2 + 50;
         InventoryScreen.renderEntityInInventoryFollowsMouse(
            guiGraphics, x - 42, y - 208, x + 107, y + 12, 60, 0.725F, mouseX, mouseY, this.getMinecraft().player
         );
      }

      if (this.haloColorBox != null && !this.haloColorBox.getValue().equals(this.haloColor)) {
         if (this.haloColorBox.getValue().length() == 6 && this.haloColorBox.hasValidColor()) {
            this.haloColor = this.haloColorBox.getValue();
         } else if (!this.haloColor.isEmpty()) {
            this.haloColor = "";
         }
      }

      if (this.developerGlowColorBox != null && !this.developerGlowColorBox.getValue().equals(this.developerGlowColor)) {
         if (this.developerGlowColorBox.getValue().length() == 6 && this.developerGlowColorBox.hasValidColor()) {
            this.developerGlowColor = this.developerGlowColorBox.getValue();
         } else if (!this.developerGlowColor.isEmpty()) {
            this.developerGlowColor = "";
         }
      }
   }

   public void onClose() {
      this.getMinecraft().setScreen(this.lastScreen);
   }
}
