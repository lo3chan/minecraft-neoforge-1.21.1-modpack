package net.mcreator.undeadrevamp.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import net.mcreator.undeadrevamp.network.BlackpetalblockButtonMessage;
import net.mcreator.undeadrevamp.world.inventory.BlackpetalblockMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class BlackpetalblockScreen extends AbstractContainerScreen<BlackpetalblockMenu> {
   private static final HashMap<String, Object> guistate = BlackpetalblockMenu.guistate;
   private final Level world;
   private final int x;
   private final int y;
   private final int z;
   private final Player entity;
   Button button_activate;
   private static final ResourceLocation texture = ResourceLocation.parse("undead_revamp2:textures/screens/blackpetalblock.png");

   public BlackpetalblockScreen(BlackpetalblockMenu container, Inventory inventory, Component text) {
      super(container, inventory, text);
      this.world = container.world;
      this.x = container.x;
      this.y = container.y;
      this.z = container.z;
      this.entity = container.entity;
      this.imageWidth = 176;
      this.imageHeight = 166;
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      guiGraphics.blit(texture, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
      RenderSystem.disableBlend();
   }

   public boolean keyPressed(int key, int b, int c) {
      if (key == 256) {
         this.minecraft.player.closeContainer();
         return true;
      } else {
         return super.keyPressed(key, b, c);
      }
   }

   protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      guiGraphics.drawString(this.font, Component.translatable("gui.undead_revamp2.blackpetalblock.label_empty"), 66, 6, -16777216, false);
   }

   public void init() {
      super.init();
      this.button_activate = Button.builder(Component.translatable("gui.undead_revamp2.blackpetalblock.button_activate"), e -> {
         PacketDistributor.sendToServer(new BlackpetalblockButtonMessage(0, this.x, this.y, this.z), new CustomPacketPayload[0]);
         BlackpetalblockButtonMessage.handleButtonAction(this.entity, 0, this.x, this.y, this.z);
      }).bounds(this.leftPos + 80, this.topPos + 53, 67, 20).build();
      guistate.put("button:button_activate", this.button_activate);
      this.addRenderableWidget(this.button_activate);
   }
}
