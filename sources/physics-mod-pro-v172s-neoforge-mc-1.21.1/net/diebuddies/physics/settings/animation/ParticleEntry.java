package net.diebuddies.physics.settings.animation;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Map;
import net.diebuddies.mixins.MixinParticleEngineAccessor;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4fStack;

public class ParticleEntry extends BaseEntry {
   private final String particleID;
   private Particle particle;

   public ParticleEntry(LegacyObjectSelectionList objectSelectionList, String particleID) {
      super(objectSelectionList, particleID);
      this.particleID = particleID;

      try {
         ParticleOptions particleOptions = PhysicsMod.registeredParticles.get(particleID);
         Map<ResourceLocation, ParticleProvider<?>> provider = ((MixinParticleEngineAccessor)Minecraft.getInstance().particleEngine).getParticleProviders();
         ParticleProvider<ParticleOptions> particleProvider = (ParticleProvider<ParticleOptions>)provider.get(
            BuiltInRegistries.PARTICLE_TYPE.getKey(particleOptions.getType())
         );
         this.particle = particleProvider.createParticle(particleOptions, null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
      } catch (Exception var6) {
      }
   }

   @Override
   public void render(
      GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
   ) {
      Font font = Minecraft.getInstance().font;
      String text = this.particleID;
      if (font.width(Component.literal(text).withStyle(ChatFormatting.BOLD)) > this.objectSelectionList.getRowWidth() - 55) {
         String newText = font.plainSubstrByWidth(text, this.objectSelectionList.getRowWidth() - 58);
         if (!text.equalsIgnoreCase(newText)) {
            text = newText + "...";
         }
      }

      MutableComponent label = Component.literal(text);
      if (hovered) {
         label = label.withStyle(ChatFormatting.BOLD);
         guiGraphics.drawCenteredString(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 16777215);
      } else {
         guiGraphics.drawCenteredString(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 12763842);
      }

      if (this.particle != null && this.particle instanceof TextureSheetParticleExtension uvParticle) {
         Matrix4fStack matrices = RenderSystem.getModelViewStack();
         float scale = entryHeight / 2.0F * 0.9F;
         double startX = -1.0;
         double endX = 1.0;
         double startY = -1.0;
         double endY = 1.0;
         double startZ = -1.0;
         double endZ = 1.0;
         double particleWidth = endX - startX;
         double particleHeight = endY - startY;
         double particleDepth = endZ - startZ;
         double xPosition = this.objectSelectionList.getRowLeft() + 2 + (int)scale;
         double yPosition = y + entryHeight / 2;
         matrices.pushMatrix();
         matrices.translate((float)xPosition, (float)yPosition, 100.0F);
         matrices.scale(scale, -scale, scale);
         matrices.translate((float)(-particleWidth * 0.5 - startX), (float)(-particleHeight * 0.5 - startY), (float)(-particleDepth * 0.5 - startZ));
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableDepthTest();
         Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
         Lighting.setupForEntityInInventory();
         RenderSystem.enableBlend();
         RenderSystem.disableCull();

         try {
            TextureAtlasSprite sprite = uvParticle.getSprite();
            if (sprite != null) {
               Tesselator tesselator = Tesselator.getInstance();
               ParticleRenderType renderType = this.particle.getRenderType();
               BufferBuilder bufferbuilder = renderType.begin(tesselator, Minecraft.getInstance().getTextureManager());
               if (bufferbuilder != null) {
                  float uvXStart = sprite.getU0();
                  float uvXEnd = sprite.getU1();
                  float uvYStart = sprite.getV0();
                  float uvYEnd = sprite.getV1();
                  float rCol = ((ParticleExtension)this.particle).getRed();
                  float gCol = ((ParticleExtension)this.particle).getGreen();
                  float bCol = ((ParticleExtension)this.particle).getBlue();
                  float alpha = ((ParticleExtension)this.particle).getAlpha();
                  int lightColor = 15728640;
                  bufferbuilder.addVertex(-1.0F, -1.0F, 0.0F).setUv(uvXEnd, uvYEnd).setColor(rCol, gCol, bCol, alpha).setLight(lightColor);
                  bufferbuilder.addVertex(-1.0F, 1.0F, 0.0F).setUv(uvXEnd, uvYStart).setColor(rCol, gCol, bCol, alpha).setLight(lightColor);
                  bufferbuilder.addVertex(1.0F, 1.0F, 0.0F).setUv(uvXStart, uvYStart).setColor(rCol, gCol, bCol, alpha).setLight(lightColor);
                  bufferbuilder.addVertex(1.0F, -1.0F, 0.0F).setUv(uvXStart, uvYEnd).setColor(rCol, gCol, bCol, alpha).setLight(lightColor);
                  MeshData meshData = bufferbuilder.build();
                  if (meshData != null) {
                     BufferUploader.drawWithShader(meshData);
                  }
               }
            }
         } catch (Exception var53) {
         }

         matrices.popMatrix();
         RenderSystem.applyModelViewMatrix();
         Lighting.setupFor3DItems();
      }
   }

   public String getText() {
      return this.particleID;
   }

   @Override
   public Component getNarration() {
      return Component.literal(this.particleID);
   }
}
