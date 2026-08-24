package net.diebuddies.physics.settings.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.diebuddies.physics.settings.ButtonSettings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class FunctionButton extends Button {
   protected ResourceLocation texture;

   public FunctionButton(int x, int y, int width, int button, Component component, OnPress onPress, ResourceLocation texture) {
      super(x, y, width, button, component, onPress, Button.DEFAULT_NARRATION);
      this.texture = texture;
      ButtonSettings.addCustomButtonStyle(this);
   }

   public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
      super.renderWidget(guiGraphics, i, j, f);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, this.texture);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
      Matrix4f matrix = guiGraphics.pose().last().pose();
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferBuilder.addVertex(matrix, this.getX(), this.getY() + 20 - 1, 100.0F).setUv(0.0F, 1.0F);
      bufferBuilder.addVertex(matrix, this.getX() + 20, this.getY() + 20 - 1, 100.0F).setUv(1.0F, 1.0F);
      bufferBuilder.addVertex(matrix, this.getX() + 20, this.getY() - 1, 100.0F).setUv(1.0F, 0.0F);
      bufferBuilder.addVertex(matrix, this.getX(), this.getY() - 1, 100.0F).setUv(0.0F, 0.0F);
      BufferUploader.drawWithShader(bufferBuilder.build());
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }
}
