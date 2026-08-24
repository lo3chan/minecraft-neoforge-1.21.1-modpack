package net.diebuddies.physics.settings.vines;

import net.diebuddies.config.ConfigVines;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VineCustomizationScreen extends LegacyOptionsSubScreen {
   private VineObjectSelectionList vineList;

   public VineCustomizationScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.dynamicblocks.customize.title"));
   }

   protected void init() {
      super.init();
      this.vineList = new VineObjectSelectionList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(this.vineList);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 130,
            this.height - 27,
            80,
            20,
            Component.translatable("physicsmod.gui.reset"),
            button -> PopupWidget.create(
               Language.getInstance().getOrDefault("physicsmod.menu.dynamicblocks.customize.reset"),
               this,
               widget -> this.addRenderableWidget(widget),
               widget -> this.removeWidget(widget),
               response -> {
                  if (response == PopupWidget.PopupResponse.YES) {
                     ConfigVines.loadDefaultConfigSettings();
                     this.children.remove(this.vineList);
                     this.vineList = new VineObjectSelectionList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
                     this.children.add(this.vineList);
                     this.applyVineSettings();
                  }
               }
            )
         )
      );
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 40,
            this.height - 27,
            80,
            20,
            Component.translatable("physicsmod.menu.dynamicblocks.add"),
            button -> this.minecraft.setScreen(new VineEditScreen(this, this.minecraft.options, null, null))
         )
      );
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 50, this.height - 27, 80, 20, CommonComponents.GUI_DONE, button -> {
         this.applyVineSettings();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.vineList.render(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
      super.render(guiGraphics, mouseX, mouseY, delta);
   }

   private void applyVineSettings() {
      VineHelper.initFromConfigSettings();
      ConfigVines.save();
      Minecraft.getInstance().levelRenderer.allChanged();
   }

   @Override
   public void onClose() {
      super.onClose();
      this.applyVineSettings();
   }

   public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
   }
}
