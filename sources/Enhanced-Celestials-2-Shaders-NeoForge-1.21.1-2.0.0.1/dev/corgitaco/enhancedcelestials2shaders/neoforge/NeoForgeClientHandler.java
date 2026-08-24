package dev.corgitaco.enhancedcelestials2shaders.neoforge;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import dev.corgitaco.enhancedcelestials2shaders.client.ClientLunarHandler;
import dev.corgitaco.enhancedcelestials2shaders.render.LunarColorRenderer;
import dev.corgitaco.enhancedcelestials2shaders.render.LunarEffectCompute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class NeoForgeClientHandler {
   private static boolean initialized = false;

   public static void init() {
      if (!initialized) {
         NeoForge.EVENT_BUS.register(new NeoForgeClientHandler());
         initialized = true;
      }
   }

   @SubscribeEvent
   public void onClientTick(Post event) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && !mc.isPaused()) {
         ClientLunarHandler.getInstance().onClientTick();
      }
   }

   @SubscribeEvent
   public void onRenderLevel(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_LEVEL) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null) {
            LunarColorRenderer renderer = LunarColorRenderer.getInstance();
            renderer.update((LunarForecast)EnhancedCelestials.lunarForecastWorldData(mc.level).orElse(null));
            float blend = renderer.getBlend();
            if (!(blend < 0.001F)) {
               float nightFactor = Math.min(1.0F, mc.level.getStarBrightness(0.0F) * 2.0F);
               float rainFactor = 1.0F - mc.level.getRainLevel(1.0F);
               float effectiveIntensity = nightFactor * rainFactor;
               if (!(effectiveIntensity < 0.01F)) {
                  float[] color = renderer.getSkyColor();
                  float[] glowColor = renderer.getGlowColor();
                  this.renderColorGrading(color[0], color[1], color[2], effectiveIntensity, glowColor, renderer.getGlowIntensity());
               }
            }
         }
      }
   }

   private void renderColorGrading(float r, float g, float b, float intensity, float[] glowColor, float glowIntensity) {
      Minecraft mc = Minecraft.getInstance();
      float[] tint = LunarEffectCompute.tintRGBA(r, g, b, intensity);
      int width = mc.getWindow().getWidth();
      int height = mc.getWindow().getHeight();
      boolean depthTest = GL11.glIsEnabled(2929);
      boolean blend = GL11.glIsEnabled(3042);
      boolean cull = GL11.glIsEnabled(2884);
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.blendFuncSeparate(SourceFactor.DST_COLOR, DestFactor.ZERO, SourceFactor.ONE, DestFactor.ZERO);
      Matrix4f oldProj = RenderSystem.getProjectionMatrix();
      Matrix4f ortho = new Matrix4f().setOrtho(0.0F, width, height, 0.0F, -100.0F, 100.0F);
      RenderSystem.setProjectionMatrix(ortho, VertexSorting.ORTHOGRAPHIC_Z);
      RenderSystem.getModelViewStack().pushMatrix();
      RenderSystem.getModelViewStack().identity();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      buffer.addVertex(0.0F, 0.0F, 0.0F).setColor(tint[0], tint[1], tint[2], tint[3]);
      buffer.addVertex(0.0F, height, 0.0F).setColor(tint[0], tint[1], tint[2], tint[3]);
      buffer.addVertex(width, height, 0.0F).setColor(tint[0], tint[1], tint[2], tint[3]);
      buffer.addVertex(width, 0.0F, 0.0F).setColor(tint[0], tint[1], tint[2], tint[3]);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
      if (intensity > 0.3F) {
         RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
         float[] glow = LunarEffectCompute.glowRGBA(intensity, glowColor[0], glowColor[1], glowColor[2], glowIntensity);
         if (glow[3] > 0.01F) {
            buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            buffer.addVertex(0.0F, 0.0F, 0.0F).setColor(glow[0], glow[1], glow[2], glow[3]);
            buffer.addVertex(0.0F, height, 0.0F).setColor(glow[0], glow[1], glow[2], glow[3]);
            buffer.addVertex(width, height, 0.0F).setColor(glow[0], glow[1], glow[2], glow[3]);
            buffer.addVertex(width, 0.0F, 0.0F).setColor(glow[0], glow[1], glow[2], glow[3]);
            BufferUploader.drawWithShader(buffer.buildOrThrow());
         }
      }

      RenderSystem.getModelViewStack().popMatrix();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setProjectionMatrix(oldProj, VertexSorting.ORTHOGRAPHIC_Z);
      RenderSystem.defaultBlendFunc();
      if (!blend) {
         RenderSystem.disableBlend();
      }

      if (cull) {
         RenderSystem.enableCull();
      }

      RenderSystem.depthMask(true);
      if (depthTest) {
         RenderSystem.enableDepthTest();
      }
   }

   public static void onDisconnect() {
      ClientLunarHandler.getInstance().reset();
   }
}
