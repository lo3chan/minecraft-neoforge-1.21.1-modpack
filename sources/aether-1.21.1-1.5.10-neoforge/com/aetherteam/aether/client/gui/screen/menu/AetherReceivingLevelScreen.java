package com.aetherteam.aether.client.gui.screen.menu;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.portal.AetherPortalBlock;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen.Reason;
import net.minecraft.world.level.block.Portal;

public class AetherReceivingLevelScreen extends ReceivingLevelScreen {
   private boolean isInAetherPortal;
   private float portalIntensity;
   private float oPortalIntensity;

   public AetherReceivingLevelScreen(BooleanSupplier levelReceived, Reason reason) {
      super(levelReceived, reason);
      if (Minecraft.getInstance().player != null
         && Minecraft.getInstance().player.portalProcess != null
         && Minecraft.getInstance().player.portalProcess.isSamePortal((Portal)AetherBlocks.AETHER_PORTAL.get())) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)Minecraft.getInstance().player.getData(AetherDataAttachments.AETHER_PLAYER);
         this.isInAetherPortal = true;
         this.portalIntensity = data.getPortalIntensity();
         this.oPortalIntensity = data.getOldPortalIntensity();
      }
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (this.isInAetherPortal) {
         guiGraphics.blit(
            0,
            0,
            -90,
            guiGraphics.guiWidth(),
            guiGraphics.guiHeight(),
            Minecraft.getInstance()
               .getBlockRenderer()
               .getBlockModelShaper()
               .getParticleIcon(((AetherPortalBlock)AetherBlocks.AETHER_PORTAL.get()).defaultBlockState())
         );
      }
   }

   public void onClose() {
      if (Minecraft.getInstance().player != null && this.isInAetherPortal) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)Minecraft.getInstance().player.getData(AetherDataAttachments.AETHER_PLAYER);
         data.portalIntensity = this.portalIntensity;
         data.oPortalIntensity = this.oPortalIntensity;
      }

      super.onClose();
   }
}
