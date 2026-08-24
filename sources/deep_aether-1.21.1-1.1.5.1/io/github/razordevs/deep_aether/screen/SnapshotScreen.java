package io.github.razordevs.deep_aether.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SnapshotScreen extends Screen {
   private static final ResourceLocation LOGO_LOCATION = ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/gui/deep_aether.png");
   private final Screen parentScreen;

   public SnapshotScreen(Screen parentScreen) {
      super(Component.literal("Deep Aether"));
      this.parentScreen = parentScreen;
   }

   protected void init() {
      super.init();
      Button button = (Button)this.addRenderableWidget(Button.builder(Component.literal("Got it!"), onpress -> this.onClose()).build());
      button.setPosition(this.width / 2 - 75, 350);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parentScreen);
      super.onClose();
   }

   public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
      this.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
      graphics.drawCenteredString(this.font, "Welcome.", this.width / 2, 260, 16777215);
      graphics.drawCenteredString(this.font, "This is an incomplete build of Deep Aether,", this.width / 2, 280, 16777215);
      graphics.drawCenteredString(this.font, "so remember to backup your worlds!", this.width / 2, 300, 16719360);
      super.render(graphics, pMouseX, pMouseY, pPartialTick);
   }

   public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
      RenderSystem.setShaderTexture(0, LOGO_LOCATION);
      graphics.blit(LOGO_LOCATION, this.width / 2 - 150, 50, 0.0F, 0.0F, 303, 189, 303, 189);
   }
}
